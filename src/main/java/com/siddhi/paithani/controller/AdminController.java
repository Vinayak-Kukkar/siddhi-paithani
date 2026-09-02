package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.service.OrderService;
import com.siddhi.paithani.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final com.siddhi.paithani.service.ProductQuestionService questionService;
    private final com.siddhi.paithani.service.PdfSalesReportGeneratorService pdfSalesReportGeneratorService;
    private final com.siddhi.paithani.service.ExcelSalesReportGeneratorService excelSalesReportGeneratorService;

    @Autowired
    public AdminController(ProductService productService,
                           OrderService orderService,
                           com.siddhi.paithani.service.ProductQuestionService questionService,
                           com.siddhi.paithani.service.PdfSalesReportGeneratorService pdfSalesReportGeneratorService,
                           com.siddhi.paithani.service.ExcelSalesReportGeneratorService excelSalesReportGeneratorService) {
        this.productService = productService;
        this.orderService = orderService;
        this.questionService = questionService;
        this.pdfSalesReportGeneratorService = pdfSalesReportGeneratorService;
        this.excelSalesReportGeneratorService = excelSalesReportGeneratorService;
    }


    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<Product> products = productService.getAllProducts();

        double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();
        long totalOrders = orders.size();
        int totalSareesSold = orders.stream()
                .flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty())
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        // Sales by Category
        Map<String, Double> categoryRevenue = new HashMap<>();
        Map<String, Integer> categoryUnits = new HashMap<>();
        for (Order order : orders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String cat = (item.getProduct() != null && item.getProduct().getCategory() != null) ? item.getProduct().getCategory() : "Yeola Paithani";
                    categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + item.getSubtotal());
                    categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0) + item.getQuantity());
                }
            }
        }

        List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getStock() != null && p.getStock() <= 3)
                .toList();

        long pendingQuestionsCount = questionService.getAllQuestions().stream()
                .filter(q -> q.getAnswer() == null || q.getAnswer().trim().isEmpty())
                .count();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSareesSold", totalSareesSold);
        model.addAttribute("averageOrderValue", averageOrderValue);
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("recentOrders", orders.stream().limit(5).toList());
        model.addAttribute("categoryRevenue", categoryRevenue);
        model.addAttribute("categoryUnits", categoryUnits);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("pendingQuestionsCount", pendingQuestionsCount);

        return "admin-dashboard";
    }


    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin-products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin-product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", productService.getAllCategories());
        return "admin-product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin-product-form";
        }
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product ID #" + id + " has been successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete Product ID #" + id + ": " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status,
                                    @RequestParam(value = "courierName", required = false) String courierName,
                                    @RequestParam(value = "trackingNumber", required = false) String trackingNumber,
                                    RedirectAttributes redirectAttributes) {
        orderService.updateOrderTracking(orderId, status, courierName, trackingNumber);
        redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " delivery status updated to '" + status + "'!");
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/export-csv")
    public void exportOrdersCsv(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"siddhi_paithani_orders_" + System.currentTimeMillis() + ".csv\"");

        List<Order> orders = orderService.getAllOrders();
        java.io.PrintWriter writer = response.getWriter();

        // Write CSV Header
        writer.println("Order Number,Customer Name,Mobile,Email,Shipping Address,City,Pincode,Total Amount (INR),Payment Method,Payment Status,Order Status,Gift Wrapped,Created Date");

        // Write CSV Rows
        for (Order o : orders) {
            String cleanAddress = (o.getAddress() != null) ? o.getAddress().replace(",", " ").replace("\n", " ") : "";
            String cleanName = (o.getCustomerName() != null) ? o.getCustomerName().replace(",", " ") : "";
            
            writer.printf("%s,%s,%s,%s,\"%s\",%s,%s,%.2f,%s,%s,%s,%s,%s\n",
                    o.getOrderNumber() != null ? o.getOrderNumber() : ("SP-" + o.getId()),
                    cleanName,
                    o.getMobile() != null ? o.getMobile() : "",
                    o.getEmail() != null ? o.getEmail() : "",
                    cleanAddress,
                    o.getCity() != null ? o.getCity() : "",
                    o.getPincode() != null ? o.getPincode() : "",
                    o.getTotalAmount() != null ? o.getTotalAmount() : 0.0,
                    o.getPaymentMethod() != null ? o.getPaymentMethod().replace(",", " ") : "UPI",
                    o.getPaymentStatus() != null ? o.getPaymentStatus() : "COMPLETED",
                    o.getStatus() != null ? o.getStatus() : "PLACED",
                    (o.getGiftWrap() != null && o.getGiftWrap()) ? "YES (+₹150)" : "NO",
                    o.getCreatedAt() != null ? o.getCreatedAt().toString() : ""
            );
        }
        writer.flush();
    }

    @GetMapping("/questions")
    public String adminQuestions(Model model) {
        var questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        model.addAttribute("productsMap", productService.getAllProducts().stream().collect(java.util.stream.Collectors.toMap(Product::getId, p -> p, (p1, p2) -> p1)));
        return "admin-questions";
    }

    @PostMapping("/questions/{id}/answer")
    public String answerQuestion(@PathVariable("id") Long id,
                                 @RequestParam("answer") String answer,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (answer != null && !answer.trim().isEmpty()) {
            questionService.answerQuestion(id, answer.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Answer posted successfully!");
        }
        return "redirect:/admin/questions";
    }

    @PostMapping("/products/restock/{id}")
    public String restockProduct(@PathVariable("id") Long id,
                                 @RequestParam(value = "quantity", defaultValue = "10") Integer quantity,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(id);
        if (product != null) {
            int currentStock = product.getStock() != null ? product.getStock() : 0;
            int newStock = currentStock + Math.max(1, quantity);
            product.setStock(newStock);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "⚡ Restocked +" + quantity + " items for '" + product.getName() + "'. New Stock Level: " + newStock);
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<Product> products = productService.getAllProducts();

        double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();
        long totalOrders = orders.size();
        int totalSareesSold = orders.stream()
                .flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty())
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        Map<String, Double> categoryRevenue = new HashMap<>();
        Map<String, Integer> categoryUnits = new HashMap<>();
        for (Order order : orders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String cat = (item.getProduct() != null && item.getProduct().getCategory() != null) ? item.getProduct().getCategory() : "Yeola Paithani";
                    categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + item.getSubtotal());
                    categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0) + item.getQuantity());
                }
            }
        }

        double totalGiftWrapRevenue = orders.stream().filter(o -> Boolean.TRUE.equals(o.getGiftWrap())).count() * 150.0;
        double totalDiscountGiven = orders.stream().mapToDouble(o -> o.getDiscountAmount() != null ? o.getDiscountAmount() : 0.0).sum();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSareesSold", totalSareesSold);
        model.addAttribute("averageOrderValue", averageOrderValue);
        model.addAttribute("totalGiftWrapRevenue", totalGiftWrapRevenue);
        model.addAttribute("totalDiscountGiven", totalDiscountGiven);
        model.addAttribute("categoryRevenue", categoryRevenue);
        model.addAttribute("categoryUnits", categoryUnits);
        model.addAttribute("orders", orders);
        model.addAttribute("products", products);

        return "admin-reports";
    }

    @GetMapping("/reports/export-pdf")
    public void exportSalesReportPdf(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"siddhi_paithani_sales_report_" + System.currentTimeMillis() + ".pdf\"");

        List<Order> orders = orderService.getAllOrders();
        List<Product> products = productService.getAllProducts();

        Map<String, Double> categoryRevenue = new HashMap<>();
        Map<String, Integer> categoryUnits = new HashMap<>();
        for (Order order : orders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String cat = (item.getProduct() != null && item.getProduct().getCategory() != null) ? item.getProduct().getCategory() : "Yeola Paithani";
                    categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + item.getSubtotal());
                    categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0) + item.getQuantity());
                }
            }
        }

        byte[] pdfBytes = pdfSalesReportGeneratorService.generateSalesReportPdf(orders, products, categoryRevenue, categoryUnits);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/reports/export-excel")
    public void exportSalesReportExcel(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"siddhi_paithani_sales_report_" + System.currentTimeMillis() + ".xls\"");

        List<Order> orders = orderService.getAllOrders();
        List<Product> products = productService.getAllProducts();

        Map<String, Double> categoryRevenue = new HashMap<>();
        Map<String, Integer> categoryUnits = new HashMap<>();
        for (Order order : orders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String cat = (item.getProduct() != null && item.getProduct().getCategory() != null) ? item.getProduct().getCategory() : "Yeola Paithani";
                    categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + item.getSubtotal());
                    categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0) + item.getQuantity());
                }
            }
        }

        byte[] excelBytes = excelSalesReportGeneratorService.generateExcelSalesReport(orders, products, categoryRevenue, categoryUnits);
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }
}


