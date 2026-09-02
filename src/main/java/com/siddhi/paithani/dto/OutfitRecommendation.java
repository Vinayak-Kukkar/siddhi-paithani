package com.siddhi.paithani.dto;

import com.siddhi.paithani.entity.Product;
import java.util.ArrayList;
import java.util.List;

public class OutfitRecommendation {

    private String primaryColor;
    private String occasion;
    private String recommendedBlouseColor;
    private String blouseFabric;
    private String blouseNecklineStyle;
    private String blouseSleevePattern;
    private String jewelleryRecommendation;
    private String hairAndMakeupTips;
    private String styleSummary;
    private List<Product> matchingSarees = new ArrayList<>();

    public OutfitRecommendation() {}

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getRecommendedBlouseColor() {
        return recommendedBlouseColor;
    }

    public void setRecommendedBlouseColor(String recommendedBlouseColor) {
        this.recommendedBlouseColor = recommendedBlouseColor;
    }

    public String getBlouseFabric() {
        return blouseFabric;
    }

    public void setBlouseFabric(String blouseFabric) {
        this.blouseFabric = blouseFabric;
    }

    public String getBlouseNecklineStyle() {
        return blouseNecklineStyle;
    }

    public void setBlouseNecklineStyle(String blouseNecklineStyle) {
        this.blouseNecklineStyle = blouseNecklineStyle;
    }

    public String getBlouseSleevePattern() {
        return blouseSleevePattern;
    }

    public void setBlouseSleevePattern(String blouseSleevePattern) {
        this.blouseSleevePattern = blouseSleevePattern;
    }

    public String getJewelleryRecommendation() {
        return jewelleryRecommendation;
    }

    public void setJewelleryRecommendation(String jewelleryRecommendation) {
        this.jewelleryRecommendation = jewelleryRecommendation;
    }

    public String getHairAndMakeupTips() {
        return hairAndMakeupTips;
    }

    public void setHairAndMakeupTips(String hairAndMakeupTips) {
        this.hairAndMakeupTips = hairAndMakeupTips;
    }

    public String getStyleSummary() {
        return styleSummary;
    }

    public void setStyleSummary(String styleSummary) {
        this.styleSummary = styleSummary;
    }

    public List<Product> getMatchingSarees() {
        return matchingSarees;
    }

    public void setMatchingSarees(List<Product> matchingSarees) {
        this.matchingSarees = matchingSarees;
    }
}
