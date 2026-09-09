package com.siddhi.paithani.interceptor;

import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.repository.CustomerRepository;
import com.siddhi.paithani.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class CustomerAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. Allow login/registration endpoints and static assets
        if (uri.equals("/login") ||
            uri.startsWith("/login/") ||
            uri.equals("/register") ||
            uri.startsWith("/register/") ||
            uri.equals("/forgot-password") ||
            uri.startsWith("/forgot-password/") ||
            uri.equals("/admin/login") ||
            uri.startsWith("/admin/login") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/images/") ||
            uri.startsWith("/webjars/") ||
            uri.startsWith("/h2-console")) {
            return true;
        }

        // 2. Check if customer or admin is logged in via session
        HttpSession session = request.getSession(true);
        Object loggedInCustomer = session.getAttribute("loggedInCustomer");
        Boolean isAdminLoggedIn = (Boolean) session.getAttribute("isAdminLoggedIn");

        if (loggedInCustomer != null || (isAdminLoggedIn != null && isAdminLoggedIn)) {
            return true;
        }

        // 3. Attempt JWT Cookie Auto-Authentication for future logins
        String jwtToken = extractJwtFromCookie(request);
        if (jwtToken == null) {
            jwtToken = extractJwtFromHeader(request);
        }

        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            Long customerId = jwtUtil.extractCustomerId(jwtToken);
            String email = jwtUtil.extractEmail(jwtToken);

            Customer customer = null;
            if (customerId != null) {
                customer = customerRepository.findById(customerId).orElse(null);
            }
            if (customer == null && email != null) {
                Optional<Customer> optionalCustomer = customerRepository.findByEmailIgnoreCase(email);
                customer = optionalCustomer.orElse(null);
            }

            if (customer != null) {
                session.setAttribute("loggedInCustomer", customer);
                return true;
            }
        }

        // 4. Redirect to Login & Registration page if unauthenticated
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String extractJwtFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
