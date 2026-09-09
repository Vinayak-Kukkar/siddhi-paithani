package com.siddhi.paithani.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow public admin login and static resources
        if (uri.endsWith("/admin/login") || uri.endsWith("/admin/logout")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Boolean isAdmin = (session != null) ? (Boolean) session.getAttribute("isAdminLoggedIn") : null;

        if (isAdmin != null && isAdmin) {
            return true;
        }

        // Redirect unauthenticated customer/visitor to Admin Login portal
        response.sendRedirect(request.getContextPath() + "/admin/login?error=required");
        return false;
    }
}
