package com.siddhi.paithani.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminAuthController {

    @Value("${admin.username:Vinuuu}")
    private String adminUsername;

    @Value("${admin.password:Anvita@2404}")
    private String adminPassword;

    @GetMapping("/admin/login")
    public String showAdminLoginPage(@RequestParam(value = "error", required = false) String error,
                                     @RequestParam(value = "logout", required = false) String logout,
                                     Model model,
                                     HttpSession session) {

        Boolean isAdminLoggedIn = (Boolean) session.getAttribute("isAdminLoggedIn");
        if (Boolean.TRUE.equals(isAdminLoggedIn)) {
            return "redirect:/admin/dashboard";
        }

        if ("required".equals(error)) {
            model.addAttribute("errorMessage", "🔒 Admin Access Required! Please sign in with administrator credentials.");
        } else if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "❌ Invalid Admin Username or Password! Please try again.");
        }

        if ("success".equals(logout)) {
            model.addAttribute("successMessage", "🚪 You have been successfully logged out of the Admin Portal.");
        }

        model.addAttribute("configuredUsername", adminUsername);
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String processAdminLogin(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        if (username != null && password != null) {
            String u = username.trim();
            String p = password.trim();

            // Validate against user-customized credentials (Vinuuu / Anvita@2404) or default admin/admin123
            boolean isValidUsername = adminUsername.equalsIgnoreCase(u) || "admin".equalsIgnoreCase(u);
            boolean isValidPassword = adminPassword.equals(p) || "admin123".equals(p);

            if (isValidUsername && isValidPassword) {
                session.setAttribute("isAdminLoggedIn", true);
                session.setAttribute("adminUser", "Vinuuu (Master Weaver)");
                redirectAttributes.addFlashAttribute("successMessage", "✅ Welcome Vinuuu! Successfully signed into Siddhi Paithani Admin Portal.");
                return "redirect:/admin/dashboard";
            }
        }

        return "redirect:/admin/login?error=invalid";
    }

    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {
        if (session != null) {
            session.removeAttribute("isAdminLoggedIn");
            session.removeAttribute("adminUser");
            session.invalidate();
        }
        return "redirect:/admin/login?logout=success";
    }
}

