package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.repository.OrderRepository;
import com.siddhi.paithani.service.NotificationDispatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.siddhi.paithani.entity.Customer;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class OrderTrackerController {

    private final OrderRepository orderRepository;
    private final NotificationDispatcherService notificationDispatcherService;

    @Autowired
    public OrderTrackerController(OrderRepository orderRepository,
                                  NotificationDispatcherService notificationDispatcherService) {
        this.orderRepository = orderRepository;
        this.notificationDispatcherService = notificationDispatcherService;
    }

    @GetMapping("/track-order")
    public String trackOrderPage(@RequestParam(value = "query", required = false) String query,
                                 HttpSession session,
                                 Model model) {
        model.addAttribute("dispatcher", notificationDispatcherService);
        Customer loggedInCustomer = session != null ? (Customer) session.getAttribute("loggedInCustomer") : null;

        if (loggedInCustomer != null) {
            String userEmail = loggedInCustomer.getEmail() != null ? loggedInCustomer.getEmail().trim() : "";
            String userMobile = loggedInCustomer.getMobile() != null ? loggedInCustomer.getMobile().trim().replaceAll("[^0-9]", "") : "";

            String trimmed = query != null ? query.trim() : "";

            if (!trimmed.isEmpty()) {
                boolean isEmailOrMobileSearch = trimmed.contains("@") || trimmed.matches("^[0-9+]+$");

                if (isEmailOrMobileSearch) {
                    boolean matchesEmail = !userEmail.isEmpty() && trimmed.equalsIgnoreCase(userEmail);
                    boolean matchesMobile = !userMobile.isEmpty() && trimmed.replaceAll("[^0-9]", "").equals(userMobile);

                    if (!matchesEmail && !matchesMobile) {
                        model.addAttribute("notFoundMessage", "Add your registered email");
                        model.addAttribute("searchedQuery", userEmail);
                        model.addAttribute("trackedOrders", List.of());
                        return "track-order";
                    }
                }

                List<Order> orders = orderRepository.findByOrderNumberIgnoreCaseOrMobileOrderByCreatedAtDesc(trimmed, trimmed);
                if (orders.isEmpty()) {
                    orders = orderRepository.findByEmailIgnoreCaseOrMobileOrderByCreatedAtDesc(trimmed, trimmed);
                }

                List<Order> myOrders = orders.stream().filter(order -> {
                    String orderEmail = order.getEmail() != null ? order.getEmail() : order.getCustomerEmail();
                    String orderMobile = order.getMobile() != null ? order.getMobile() : order.getCustomerPhone();

                    boolean emailMatch = !userEmail.isEmpty() && orderEmail != null && orderEmail.equalsIgnoreCase(userEmail);
                    boolean mobileMatch = !userMobile.isEmpty() && orderMobile != null && orderMobile.replaceAll("[^0-9]", "").equals(userMobile);

                    return emailMatch || mobileMatch;
                }).toList();

                if (myOrders.isEmpty()) {
                    model.addAttribute("notFoundMessage", "Add your registered email");
                }

                model.addAttribute("searchedQuery", trimmed);
                model.addAttribute("trackedOrders", myOrders);
            } else {
                List<Order> myOrders = orderRepository.findByEmailIgnoreCaseOrMobileOrderByCreatedAtDesc(userEmail, userMobile);
                model.addAttribute("searchedQuery", userEmail);
                model.addAttribute("trackedOrders", myOrders);
            }
        } else {
            if (query != null && !query.trim().isEmpty()) {
                String trimmed = query.trim();
                List<Order> orders = orderRepository.findByOrderNumberIgnoreCaseOrMobileOrderByCreatedAtDesc(trimmed, trimmed);
                if (orders.isEmpty()) {
                    orders = orderRepository.findByEmailIgnoreCaseOrMobileOrderByCreatedAtDesc(trimmed, trimmed);
                }
                model.addAttribute("searchedQuery", trimmed);
                model.addAttribute("trackedOrders", orders);
                if (orders.isEmpty()) {
                    model.addAttribute("notFoundMessage", "No orders found matching Order # or Mobile '" + trimmed + "'. Please verify your receipt details.");
                }
            }
        }

        return "track-order";
    }

    @GetMapping("/orders/invoice")
    public String generateGstInvoice(@RequestParam("orderNumber") String orderNumber, Model model) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if (order == null) {
            return "redirect:/track-order";
        }
        
        double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        double taxableAmount = Math.round((total / 1.05) * 100.0) / 100.0;
        double gstAmount = Math.round((total - taxableAmount) * 100.0) / 100.0;
        double cgst = Math.round((gstAmount / 2.0) * 100.0) / 100.0;
        double sgst = Math.round((gstAmount / 2.0) * 100.0) / 100.0;

        model.addAttribute("order", order);
        model.addAttribute("taxableAmount", taxableAmount);
        model.addAttribute("gstAmount", gstAmount);
        model.addAttribute("cgst", cgst);
        model.addAttribute("sgst", sgst);

        return "gst-invoice";
    }
}
