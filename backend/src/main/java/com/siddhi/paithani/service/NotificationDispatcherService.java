package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Order;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NotificationDispatcherService {

    private static final String FAST2SMS_API_KEY = "VwfqKlPM6WekSR5mgr492sjLt8xY0y7XBJGNUCzT3Ip1nOoHah2CAZBHncmN34htVFrbw9zuRUIL7Ddq";

    /**
     * Generate 1-Click WhatsApp Customer Tracking Notification Link
     */
    public String generateCustomerWhatsAppTrackingLink(Order order) {
        if (order == null || order.getMobile() == null) return "https://wa.me/";

        String cleanPhone = order.getMobile().replaceAll("[^0-9]", "");
        if (cleanPhone.length() > 10) {
            cleanPhone = cleanPhone.substring(cleanPhone.length() - 10);
        }

        String orderNum = order.getOrderNumber() != null ? order.getOrderNumber() : "SP-" + order.getId();
        String status = order.getStatus() != null ? order.getStatus() : "ORDER_PLACED";
        String courier = order.getCourierName() != null && !order.getCourierName().trim().isEmpty() ? order.getCourierName() : "Siddhi Express Logistics";
        String trackingNo = order.getTrackingNumber() != null && !order.getTrackingNumber().trim().isEmpty() ? order.getTrackingNumber() : "N/A";

        StringBuilder msg = new StringBuilder();
        msg.append("👑 *SIDDHI PAITHANI - ORDER TRACKING UPDATE*\n");
        msg.append("-----------------------------------------\n");
        msg.append("Dear ").append(order.getCustomerName() != null ? order.getCustomerName() : "Valued Customer").append(",\n\n");

        if ("SHIPPED".equalsIgnoreCase(status)) {
            msg.append("🚚 *Great News! Your Paithani Saree Has Been Shipped!*\n");
            msg.append("📦 *Order #:* ").append(orderNum).append("\n");
            msg.append("🚛 *Courier Partner:* ").append(courier).append("\n");
            msg.append("🎫 *AWB Tracking #:* ").append(trackingNo).append("\n\n");
            msg.append("🔗 *Live Shipment Tracker:* http://localhost:8084/track-order?query=").append(orderNum).append("\n\n");
            msg.append("✨ Your saree is carefully packed in royal silk velvet box with official Silk Mark India certification!");
        } else if ("OUT_FOR_DELIVERY".equalsIgnoreCase(status)) {
            msg.append("📍 *Your Paithani Saree is Out for Delivery Today!*\n");
            msg.append("📦 *Order #:* ").append(orderNum).append("\n");
            msg.append("🚛 *Courier Partner:* ").append(courier).append("\n");
            msg.append("🎫 *AWB Tracking #:* ").append(trackingNo).append("\n\n");
            msg.append("Please ensure someone is available at your shipping address to receive your handwoven heritage saree.");
        } else if ("DELIVERED".equalsIgnoreCase(status)) {
            msg.append("🎉 *Order Delivered Successfully!*\n");
            msg.append("📦 *Order #:* ").append(orderNum).append("\n\n");
            msg.append("Thank you for choosing Siddhi Paithani Yeola! We hope you cherish your handwoven silk masterpiece.\n");
            msg.append("🌿 *Care Tip:* Dry clean only & store in soft cotton cloth to preserve authentic gold zari shimmer.");
        } else if ("IN_PRODUCTION".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status)) {
            msg.append("🧶 *Your Saree is Being Handwoven & Packed!*\n");
            msg.append("📦 *Order #:* ").append(orderNum).append("\n");
            msg.append("Our master weavers in Yeola are inspecting gold zari weaves and attaching Silk Mark certification.");
        } else {
            msg.append("📦 *Order Received & Confirmed!*\n");
            msg.append("📦 *Order #:* ").append(orderNum).append("\n");
            msg.append("Total Amount: ₹").append(String.format("%.2f", order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)).append("\n");
        }

        msg.append("\n-----------------------------------------\n");
        msg.append("Need Help? Reply to this message or Call +91 72191 20935");

        try {
            String encoded = URLEncoder.encode(msg.toString(), StandardCharsets.UTF_8.name());
            return "https://wa.me/91" + cleanPhone + "?text=" + encoded;
        } catch (Exception e) {
            return "https://wa.me/91" + cleanPhone;
        }
    }

    /**
     * Send Real-Time SMS via Fast2SMS API
     */
    public boolean sendSmsNotification(Order order) {
        if (order == null || order.getMobile() == null || order.getMobile().trim().isEmpty()) {
            return false;
        }

        String cleanPhone = order.getMobile().replaceAll("[^0-9]", "");
        if (cleanPhone.length() > 10) {
            cleanPhone = cleanPhone.substring(cleanPhone.length() - 10);
        }

        if (cleanPhone.length() != 10) return false;

        String orderNum = order.getOrderNumber() != null ? order.getOrderNumber() : "SP-" + order.getId();
        String status = order.getStatus() != null ? order.getStatus() : "CONFIRMED";
        String courier = order.getCourierName() != null ? order.getCourierName() : "Courier";
        String trackingNo = order.getTrackingNumber() != null ? order.getTrackingNumber() : "N/A";

        String smsText = String.format(
                "Siddhi Paithani Order #%s update: Status is %s via %s (AWB: %s). Track at http://localhost:8084/track-order?query=%s",
                orderNum, status, courier, trackingNo, orderNum
        );

        try {
            String requestUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=" + FAST2SMS_API_KEY +
                    "&route=q&message=" + URLEncoder.encode(smsText, StandardCharsets.UTF_8.name()) +
                    "&flash=0&numbers=" + cleanPhone;

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int responseCode = conn.getResponseCode();

            return responseCode == 200;
        } catch (Exception e) {
            System.err.println("Fast2SMS Dispatch Notice: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get Direct Courier Partner Tracking Web Link
     */
    public String getCourierTrackingUrl(String courierName, String trackingNumber) {
        if (courierName == null || trackingNumber == null || trackingNumber.trim().isEmpty()) {
            return "#";
        }
        String c = courierName.toLowerCase();
        if (c.contains("bluedart")) {
            return "https://www.bluedart.com/tracking?handler=tne&action=sub&numbers=" + trackingNumber;
        } else if (c.contains("dtdc")) {
            return "https://www.dtdc.in/tracking/shipment-tracking.asp?strCno=" + trackingNumber;
        } else if (c.contains("delhivery")) {
            return "https://www.delhivery.com/track/package/" + trackingNumber;
        } else if (c.contains("india post") || c.contains("post")) {
            return "https://www.indiapost.gov.in/_layouts/15/dop.portal.tracking/trackconsignment.aspx";
        } else if (c.contains("fedex")) {
            return "https://www.fedex.com/fedextrack/?trknbr=" + trackingNumber;
        }
        return "https://www.google.com/search?q=" + URLEncoder.encode(courierName + " tracking " + trackingNumber, StandardCharsets.UTF_8);
    }
}
