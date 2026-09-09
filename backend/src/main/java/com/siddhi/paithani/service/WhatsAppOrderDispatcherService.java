package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppOrderDispatcherService {

    private static final String MASTER_WEAVER_PHONE = "917219120935";

    /**
     * Generate WhatsApp Direct Link for Master Weaver (+91 72191 20935)
     */
    public String generateMasterWeaverWhatsAppLink(Order order) {
        if (order == null) return "https://wa.me/" + MASTER_WEAVER_PHONE;

        StringBuilder msg = new StringBuilder();
        msg.append("🧵 *NEW SIDDHI PAITHANI HANDLOOM ORDER RECEIVED!*\n");
        msg.append("-----------------------------------------\n");
        msg.append("📦 *Order #:* ").append(order.getOrderNumber() != null ? order.getOrderNumber() : "#" + order.getId()).append("\n");
        msg.append("👤 *Customer:* ").append(order.getCustomerName() != null ? order.getCustomerName() : "Valued Customer").append("\n");
        msg.append("📱 *Mobile:* ").append(order.getMobile() != null ? order.getMobile() : "N/A").append("\n");
        msg.append("📍 *Delivery City:* ").append(order.getCity() != null ? order.getCity() : "N/A").append(" (").append(order.getPincode() != null ? order.getPincode() : "").append(")\n\n");

        msg.append("👗 *ORDERED SAREES:*\n");
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            int idx = 1;
            for (OrderItem item : order.getOrderItems()) {
                msg.append(idx++).append(". ").append(item.getProductName()).append(" (Qty: ").append(item.getQuantity()).append(") - ₹").append(String.format("%.2f", item.getPrice())).append("\n");
            }
        } else {
            msg.append("• Handloom Pure Silk Saree\n");
        }

        if (Boolean.TRUE.equals(order.getGiftWrap())) {
            msg.append("\n🎁 *Royal Gift Wrap:* Yes (+₹150.00)");
            if (order.getGiftRecipientName() != null && !order.getGiftRecipientName().isEmpty()) {
                msg.append("\n👤 *Recipient:* ").append(order.getGiftRecipientName());
            }
            if (order.getGiftOccasion() != null && !order.getGiftOccasion().isEmpty()) {
                msg.append("\n✨ *Occasion:* ").append(order.getGiftOccasion());
            }
            if (order.getGiftMessage() != null && !order.getGiftMessage().isEmpty()) {
                msg.append("\n📝 *Handwritten Card Note:* \"").append(order.getGiftMessage()).append("\"");
            }
            msg.append("\n");
        }

        msg.append("\n💰 *TOTAL AMOUNT:* ₹").append(String.format("%.2f", order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)).append("\n");
        msg.append("💳 *Payment Status:* ").append(order.getPaymentStatus() != null ? order.getPaymentStatus() : "PAID").append("\n\n");
        msg.append("🚚 *Artisan Action:* Please prepare handloom packaging, attach official Silk Mark India tag, and dispatch via courier!");

        String encodedText = URLEncoder.encode(msg.toString(), StandardCharsets.UTF_8);
        return "https://wa.me/" + MASTER_WEAVER_PHONE + "?text=" + encodedText;
    }
}
