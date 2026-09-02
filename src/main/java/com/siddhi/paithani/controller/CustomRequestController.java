package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.CustomRequest;
import com.siddhi.paithani.repository.CustomRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class CustomRequestController {

    @Autowired
    private CustomRequestRepository customRequestRepository;

    @GetMapping("/custom-order")
    public String showCustomOrderPage(Model model) {
        model.addAttribute("customRequest", new CustomRequest());
        return "custom-order";
    }

    @PostMapping("/custom-order/submit")
    public String submitCustomRequest(@ModelAttribute CustomRequest request, RedirectAttributes redirectAttributes) {
        customRequestRepository.save(request);

        // Format WhatsApp message to Yeola Weavers (+91 72191 20935)
        String waMsg = String.format(
            "🚩 *NEW CUSTOM PAITHANI WEAVE & STITCHING REQUEST* 🚩\n" +
            "----------------------------------------\n" +
            "👤 *Customer*: %s\n" +
            "📞 *Mobile*: %s\n" +
            "🛍️ *Saree Model*: %s\n" +
            "🪡 *Blouse Size*: %s\n" +
            "📐 *Measurements*: %s\n" +
            "🦚 *Preferred Zari Motif*: %s\n" +
            "🎨 *Color Choice*: %s\n" +
            "📝 *Instructions*: %s\n" +
            "----------------------------------------\n" +
            "Please confirm feasibility & estimated price with customer!",
            request.getCustomerName(),
            request.getMobile(),
            request.getSareeName() != null ? request.getSareeName() : "Custom Yeola Paithani",
            request.getBlouseSize(),
            request.getCustomMeasurements() != null ? request.getCustomMeasurements() : "Standard",
            request.getPreferredZariMotif(),
            request.getPreferredColor(),
            request.getSpecialInstructions() != null ? request.getSpecialInstructions() : "None"
        );

        String encodedMsg = URLEncoder.encode(waMsg, StandardCharsets.UTF_8);
        String waUrl = "https://wa.me/917219120935?text=" + encodedMsg;

        redirectAttributes.addFlashAttribute("customSuccess", "Your custom saree & blouse stitching request has been submitted! Redirecting to WhatsApp...");
        redirectAttributes.addFlashAttribute("waUrl", waUrl);

        return "redirect:/custom-order";
    }

    @GetMapping({"/admin/custom-requests", "/custom-requests"})
    public String listCustomRequests(Model model) {
        model.addAttribute("requests", customRequestRepository.findAllByOrderByCreatedAtDesc());
        return "admin-custom-requests";
    }

    @PostMapping("/admin/custom-requests/update-status")
    public String updateStatus(@RequestParam("id") Long id, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        CustomRequest req = customRequestRepository.findById(id).orElse(null);
        if (req != null) {
            req.setStatus(status);
            customRequestRepository.save(req);
            redirectAttributes.addFlashAttribute("successMessage", "Custom request #" + id + " status updated to " + status);
        }
        return "redirect:/admin/custom-requests";
    }
}
