package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.OutfitRecommendation;
import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutfitAssistantService {

    private final ProductRepository productRepository;

    @Autowired
    public OutfitAssistantService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OutfitRecommendation generateOutfitRecommendation(String color, String occasion) {
        OutfitRecommendation rec = new OutfitRecommendation();
        String searchColor = (color != null && !color.trim().isEmpty()) ? color.trim() : "Blue";
        String searchOccasion = (occasion != null && !occasion.trim().isEmpty()) ? occasion.trim() : "Wedding";

        rec.setPrimaryColor(searchColor);
        rec.setOccasion(searchOccasion);

        String lowerColor = searchColor.toLowerCase();

        if (lowerColor.contains("blue") || lowerColor.contains("peacock")) {
            rec.setRecommendedBlouseColor("Crimson Red / Golden Yellow Brocade");
            rec.setBlouseFabric("Pure Yeola Mulberry Silk with Woven Gold Zari Borders");
            rec.setBlouseNecklineStyle("Deep Oval Back with Pearl & Gold Tassel Dori Latkan");
            rec.setBlouseSleevePattern("Elbow Length Sleeves featuring Triple Munia / Peacock Motifs");
            rec.setJewelleryRecommendation("Traditional Gold Kolhapuri Thushi, Laxmi Haar, Brahmani Pearl Nath & Gold Bugadi");
            rec.setHairAndMakeupTips("Classic Gajra Bun with Maharashtrian Crescent Moon Bindi & Matte Red Lips");
            rec.setStyleSummary("Royal Peacock Blue paired with rich Crimson Red creates an iconic regal Maharashtrian bride look!");
        } else if (lowerColor.contains("red") || lowerColor.contains("maroon") || lowerColor.contains("crimson")) {
            rec.setRecommendedBlouseColor("Bottle Green / Mustard Yellow Brocade");
            rec.setBlouseFabric("Handloom Kadiyal Silk with Contrasting Zari Weave");
            rec.setBlouseNecklineStyle("Sweetheart Neckline with Gold Piping & Zardozi Accent");
            rec.setBlouseSleevePattern("3/4th Sleeves with Heavy Zari Border & Parrot Motif");
            rec.setJewelleryRecommendation("Antique Gold Kolhapuri Saaj, Choker, Green Emerald Nath & Temple Jhumkas");
            rec.setHairAndMakeupTips("Low Braided Hair with Fresh Mogra Flowers & Warm Gold Eyeshadow");
            rec.setStyleSummary("Kathpadar Crimson Red paired with Emerald Bottle Green signifies timeless wedding prosperity.");
        } else if (lowerColor.contains("green") || lowerColor.contains("emerald")) {
            rec.setRecommendedBlouseColor("Magenta Pink / Royal Purple Silk");
            rec.setBlouseFabric("Pure Tissue Brocade Silk with Heavy Zari Embroidered Back");
            rec.setBlouseNecklineStyle("Square Neck Front & Oval Cut Back with Pearl Edging");
            rec.setBlouseSleevePattern("Cap Sleeves with Woven Lotus/Asavali Border");
            rec.setJewelleryRecommendation("Polki Diamond & Emerald Choker, Traditional Pearl Nath, Bajuband (Armlet)");
            rec.setHairAndMakeupTips("Messy Floral Bun with Pink Tinted Lips and Golden Highlighter");
            rec.setStyleSummary("Vibrant Emerald Green with Magenta Pink blouse radiates enchanting traditional grace.");
        } else if (lowerColor.contains("pink") || lowerColor.contains("magenta")) {
            rec.setRecommendedBlouseColor("Royal Purple / Mulberry Wine Brocade");
            rec.setBlouseFabric("Soft Pure Silk with Golden Tissue Weave");
            rec.setBlouseNecklineStyle("High Neck Brocade Collar with Front Gold Button Accents");
            rec.setBlouseSleevePattern("Full Length Sheer Net / Silk Sleeves with Zari Wrist Band");
            rec.setJewelleryRecommendation("Pearl & Ruby Choker, Antique Gold Jhumkas & Baji Rao Style Nath");
            rec.setHairAndMakeupTips("Soft Curls with Jasmine Gajra & Radiant Nude Pink Makeup");
            rec.setStyleSummary("Soft Baby Pink with Mulberry Wine Blouse creates a dreamlike festive aura.");
        } else if (lowerColor.contains("yellow") || lowerColor.contains("gold") || lowerColor.contains("mustard")) {
            rec.setRecommendedBlouseColor("Royal Blue / Maroon Velvet-Silk");
            rec.setBlouseFabric("Heavy Raw Silk with Hand-Embroidered Zardozi");
            rec.setBlouseNecklineStyle("U-Shape Back with Metallic Golden Latkan");
            rec.setBlouseSleevePattern("Elbow Length Sleeves with Classic Temple Border");
            rec.setJewelleryRecommendation("Kundan & Green Meenakari Necklace Set with Temple Kada Bangles");
            rec.setHairAndMakeupTips("Traditional Ambada Haircut with Jasmine Flowers & Bold Khol Eyeliner");
            rec.setStyleSummary("Bright Mustard Gold paired with Royal Blue reflects sunshine festive cheer.");
        } else {
            rec.setRecommendedBlouseColor("Contrast Gold Brocade / Deep Crimson Silk");
            rec.setBlouseFabric("Authentic Yeola Handloom Silk");
            rec.setBlouseNecklineStyle("Classic Boat Neck with Gold Zari Border");
            rec.setBlouseSleevePattern("Elbow Length Sleeves with Signature Paithani Pallu Motif");
            rec.setJewelleryRecommendation("Classic Gold Thushi, Pearl Nath, Green Glass Bangles & Gold Kada");
            rec.setHairAndMakeupTips("Sleek Bun with Pearl Hairpins & Classic Red Lip");
            rec.setStyleSummary("Elegant heritage combination customized for celebratory grandeur.");
        }

        // Fetch matching products from store catalog
        List<Product> allProducts = productRepository.findAll();
        List<Product> matching = allProducts.stream()
                .filter(p -> p.getColor() != null && p.getColor().toLowerCase().contains(searchColor.toLowerCase())
                        || p.getName() != null && p.getName().toLowerCase().contains(searchColor.toLowerCase()))
                .limit(4)
                .collect(Collectors.toList());

        if (matching.isEmpty()) {
            matching = allProducts.stream().limit(4).collect(Collectors.toList());
        }

        rec.setMatchingSarees(matching);
        return rec;
    }
}
