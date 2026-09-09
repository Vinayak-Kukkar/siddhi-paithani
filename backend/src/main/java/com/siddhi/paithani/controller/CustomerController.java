package com.siddhi.paithani.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.service.CustomerService;
import com.siddhi.paithani.service.OrderService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private com.siddhi.paithani.service.NotificationService notificationService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/my-orders")
    public String showMyOrdersPage(@RequestParam(value = "search", required = false) String search,
                                   Model model,
                                   HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        String lookupQuery = search;

        if (loggedInCustomer != null) {
            String userEmail = loggedInCustomer.getEmail() != null ? loggedInCustomer.getEmail().trim() : "";
            String userMobile = loggedInCustomer.getMobile() != null ? loggedInCustomer.getMobile().trim().replaceAll("[^0-9]", "") : "";

            if (lookupQuery != null && !lookupQuery.trim().isEmpty()) {
                String cleanQuery = lookupQuery.trim();

                boolean isEmailOrMobileSearch = cleanQuery.contains("@") || cleanQuery.matches("^[0-9+]+$");

                if (isEmailOrMobileSearch) {
                    boolean matchesEmail = !userEmail.isEmpty() && cleanQuery.equalsIgnoreCase(userEmail);
                    boolean matchesMobile = !userMobile.isEmpty() && cleanQuery.replaceAll("[^0-9]", "").equals(userMobile);

                    if (!matchesEmail && !matchesMobile) {
                        model.addAttribute("error", "Add your registered email");
                        lookupQuery = userEmail;
                    }
                }
            } else {
                lookupQuery = userEmail;
            }

            List<Order> orders = orderService.getOrdersByCustomerSearch(lookupQuery);

            // Restrict results strictly to logged-in customer's orders
            List<Order> myOrders = orders.stream().filter(order -> {
                String orderEmail = order.getEmail() != null ? order.getEmail() : order.getCustomerEmail();
                String orderMobile = order.getMobile() != null ? order.getMobile() : order.getCustomerPhone();

                boolean emailMatch = !userEmail.isEmpty() && orderEmail != null && orderEmail.equalsIgnoreCase(userEmail);
                boolean mobileMatch = !userMobile.isEmpty() && orderMobile != null && orderMobile.replaceAll("[^0-9]", "").equals(userMobile);

                return emailMatch || mobileMatch;
            }).toList();

            if (search != null && !search.trim().isEmpty() && myOrders.isEmpty() && !model.containsAttribute("error")) {
                model.addAttribute("error", "Add your registered email");
            }

            model.addAttribute("orders", myOrders);
            model.addAttribute("searchQuery", lookupQuery);
        } else {
            if (lookupQuery != null && !lookupQuery.trim().isEmpty()) {
                List<Order> orders = orderService.getOrdersByCustomerSearch(lookupQuery);
                model.addAttribute("orders", orders);
                model.addAttribute("searchQuery", lookupQuery);
            } else {
                model.addAttribute("orders", List.of());
            }
        }

        return "my-orders";
    }

    @PostMapping("/my-orders")
    public String searchMyOrders(@RequestParam("search") String search,
                                 RedirectAttributes redirectAttributes) {
        if (search == null || search.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please enter your Mobile Number, Email, or Order ID.");
            return "redirect:/my-orders";
        }
        return "redirect:/my-orders?search=" + search.trim();
    }

    @Autowired
    private com.siddhi.paithani.service.PdfInvoiceGeneratorService pdfInvoiceGeneratorService;

    @GetMapping("/orders/invoice/{id}")
    public String printOrderInvoice(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/my-orders";
        }
        model.addAttribute("order", order);
        return "invoice";
    }

    @GetMapping("/orders/invoice/{id}/pdf")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfInvoiceGeneratorService.generateGstTaxInvoicePdf(order);
        String filename = "Siddhi_Paithani_GST_Invoice_" + (order.getOrderNumber() != null ? order.getOrderNumber() : id) + ".pdf";

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @Autowired
    private com.siddhi.paithani.service.ReferralService referralService;

    @Autowired
    private com.siddhi.paithani.util.JwtUtil jwtUtil;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "ref", required = false) String ref,
                                @RequestParam(value = "tab", required = false, defaultValue = "login") String tab,
                                Model model,
                                HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer != null) {
            return "redirect:/";
        }
        if (ref != null) {
            session.setAttribute("signupRefCode", ref.trim());
            model.addAttribute("referralDiscountNotice", "🎉 Special Invitation Code applied! Sign up now to claim your ₹500 Welcome Discount Voucher!");
        }
        model.addAttribute("customer", new Customer());
        model.addAttribute("activeTab", tab);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("emailOrMobile") String emailOrMobile,
                               @RequestParam("password") String password,
                               HttpSession session,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            Customer authenticatedCustomer = customerService.loginCustomer(emailOrMobile, password);

            // Generate JWT Token & Set HttpOnly Cookie
            String token = jwtUtil.generateToken(authenticatedCustomer);
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days persistence
            response.addCookie(jwtCookie);

            session.setAttribute("loggedInCustomer", authenticatedCustomer);
            redirectAttributes.addFlashAttribute("success", "✅ Welcome back, " + authenticatedCustomer.getCustomerName() + "!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            model.addAttribute("emailOrMobile", emailOrMobile);
            model.addAttribute("activeTab", "login");
            model.addAttribute("customer", new Customer());
            return "login";
        }
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("customer") Customer customer,
                                  BindingResult result,
                                  @RequestParam("rawPassword") String rawPassword,
                                  HttpSession session,
                                  HttpServletResponse response,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        if (rawPassword == null || rawPassword.trim().length() < 6) {
            result.rejectValue("password", "error.customer", "Password must be at least 6 characters long");
        }

        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            Customer existing = customerService.findByEmailOrMobile(customer.getEmail().trim());
            if (existing != null && existing.getPassword() != null && !existing.getPassword().trim().isEmpty()) {
                result.rejectValue("email", "error.customer", "This email is already registered. Please sign in or use a different email.");
            }
        }

        if (customer.getMobile() != null && !customer.getMobile().trim().isEmpty()) {
            Customer existing = customerService.findByEmailOrMobile(customer.getMobile().trim());
            if (existing != null && existing.getPassword() != null && !existing.getPassword().trim().isEmpty()) {
                result.rejectValue("mobile", "error.customer", "This mobile number is already registered. Please sign in or use a different mobile number.");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("registerError", "Please correct the highlighted errors in the registration form below.");
            model.addAttribute("activeTab", "register");
            return "login";
        }

        try {
            Customer registeredCustomer = customerService.registerCustomer(customer, rawPassword);

            // Process referral reward if referral code exists in session
            String refCode = (String) session.getAttribute("signupRefCode");
            if (refCode != null) {
                referralService.processReferralOnSignup(registeredCustomer, refCode);
                session.removeAttribute("signupRefCode");
                redirectAttributes.addFlashAttribute("success", "🎁 Welcome " + registeredCustomer.getCustomerName() + "! Your ₹500 Referral Discount Bonus has been added to your Loyalty Wallet!");
            } else {
                redirectAttributes.addFlashAttribute("success", "🎉 Account registered successfully! Welcome to Siddhi Paithani, " + registeredCustomer.getCustomerName() + "!");
            }

            // Generate JWT Token & Set HttpOnly Cookie
            String token = jwtUtil.generateToken(registeredCustomer);
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days persistence
            response.addCookie(jwtCookie);

            session.setAttribute("loggedInCustomer", registeredCustomer);
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("registerError", e.getMessage());
            model.addAttribute("activeTab", "register");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logoutCustomer(HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        session.removeAttribute("loggedInCustomer");

        // Clear JWT Cookie
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        redirectAttributes.addFlashAttribute("success", "Logged out successfully.");
        return "redirect:/login";
    }

    // --- FORGOT & RESET PASSWORD VIA EMAIL OTP ---
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a valid registered email address.");
            return "forgot-password";
        }

        String cleanEmail = email.trim();
        Customer customer = customerService.findByEmailOrMobile(cleanEmail);

        if (customer == null) {
            model.addAttribute("error", "No registered customer account found with email '" + cleanEmail + "'. Please check the spelling or register a new profile.");
            return "forgot-password";
        }

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new java.util.Random().nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + (10 * 60 * 1000); // 10 minutes

        session.setAttribute("resetOtpEmail", customer.getEmail());
        session.setAttribute("resetOtpCode", otpCode);
        session.setAttribute("resetOtpExpiry", expiryTime);

        // Send OTP Email
        try {
            notificationService.sendPasswordResetOtp(customer.getEmail(), customer.getCustomerName(), otpCode);
        } catch (Exception e) {
            // Log fallback
        }

        redirectAttributes.addFlashAttribute("success", "A 6-digit Password Reset OTP has been dispatched to " + customer.getEmail() + ". Please check your inbox!");
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(HttpSession session, Model model) {
        String resetEmail = (String) session.getAttribute("resetOtpEmail");
        if (resetEmail == null) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", resetEmail);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("otp") String otp,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        String savedEmail = (String) session.getAttribute("resetOtpEmail");
        String savedOtp = (String) session.getAttribute("resetOtpCode");
        Long expiryTime = (Long) session.getAttribute("resetOtpExpiry");

        if (savedEmail == null || savedOtp == null || expiryTime == null) {
            redirectAttributes.addFlashAttribute("error", "Password reset session expired. Please request a new OTP.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("email", savedEmail);

        if (System.currentTimeMillis() > expiryTime) {
            session.removeAttribute("resetOtpEmail");
            session.removeAttribute("resetOtpCode");
            session.removeAttribute("resetOtpExpiry");
            model.addAttribute("error", "Your OTP code has expired (valid 10 mins). Please request a new OTP.");
            return "reset-password";
        }

        if (otp == null || !otp.trim().equals(savedOtp)) {
            model.addAttribute("error", "Invalid 6-digit OTP code. Please check your email inbox and try again.");
            return "reset-password";
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            model.addAttribute("error", "New password must be at least 6 characters long.");
            return "reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and Confirm password do not match.");
            return "reset-password";
        }

        Customer customer = customerService.findByEmailOrMobile(savedEmail);
        if (customer != null) {
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.saveCustomer(customer);
        }

        session.removeAttribute("resetOtpEmail");
        session.removeAttribute("resetOtpCode");
        session.removeAttribute("resetOtpExpiry");

        redirectAttributes.addFlashAttribute("success", "✅ Password updated successfully! Please sign in with your new password.");
        return "redirect:/login";
    }


    @GetMapping("/customers")
    public String viewCustomers(Model model) {
        List<Customer> customersList = customerService.getAllCustomers();
        model.addAttribute("customers", customersList);
        model.addAttribute("listCustomers", customersList);
        return "customers";
    }
    
    @GetMapping("/search")
    public String searchCustomer(@RequestParam("keyword") String keyword, Model model) {
        List<Customer> searchResult = customerService.searchCustomer(keyword);
        model.addAttribute("customers", searchResult);
        model.addAttribute("listCustomers", searchResult);
        model.addAttribute("keyword", keyword);
        return "customers";
    }

    @GetMapping({"/showNewCustomerForm", "/addCustomer"})
    public String addCustomerPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    @PostMapping("/saveCustomer")
    public String saveCustomer(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "add-customer";
        }

        customerService.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("success", "Customer saved successfully!");
        return "redirect:/customers";
    }
    
    @GetMapping({"/showFormForUpdate/{id}", "/editCustomer/{id}"})
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "update-customer";
    }
    
    @PostMapping("/updateCustomer")
    public String updateCustomer(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "update-customer";
        }

        customerService.updateCustomer(customer);
        redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
        return "redirect:/customers";
    }
    
    @GetMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteCustomer(id);
        redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        return "redirect:/customers";
    }
}