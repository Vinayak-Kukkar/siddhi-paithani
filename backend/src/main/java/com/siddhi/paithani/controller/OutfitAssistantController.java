package com.siddhi.paithani.controller;

import com.siddhi.paithani.dto.OutfitRecommendation;
import com.siddhi.paithani.service.OutfitAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OutfitAssistantController {

    private final OutfitAssistantService outfitAssistantService;

    @Autowired
    public OutfitAssistantController(OutfitAssistantService outfitAssistantService) {
        this.outfitAssistantService = outfitAssistantService;
    }

    @GetMapping("/outfit-assistant")
    public String outfitAssistantPage(@RequestParam(value = "color", required = false, defaultValue = "Blue") String color,
                                      @RequestParam(value = "occasion", required = false, defaultValue = "Wedding") String occasion,
                                      Model model) {
        OutfitRecommendation recommendation = outfitAssistantService.generateOutfitRecommendation(color, occasion);
        model.addAttribute("recommendation", recommendation);
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedOccasion", occasion);
        return "outfit-assistant";
    }

    @PostMapping("/api/outfit/recommend")
    @ResponseBody
    public OutfitRecommendation getOutfitRecommendationApi(@RequestParam("color") String color,
                                                           @RequestParam(value = "occasion", defaultValue = "Wedding") String occasion) {
        return outfitAssistantService.generateOutfitRecommendation(color, occasion);
    }
}
