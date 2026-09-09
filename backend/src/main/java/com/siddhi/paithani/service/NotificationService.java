package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.ProductQuestion;

public interface NotificationService {

    void sendOrderConfirmationNotification(Order order);
    
    void sendAdminQuestionNotification(ProductQuestion question, String sareeName);

    void sendLowStockAlert(com.siddhi.paithani.entity.Product product);

    void sendPasswordResetOtp(String email, String customerName, String otpCode);
}


