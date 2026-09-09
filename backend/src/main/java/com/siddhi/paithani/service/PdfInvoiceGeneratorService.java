package com.siddhi.paithani.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PdfInvoiceGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfInvoiceGeneratorService.class);

    public byte[] generateGstTaxInvoicePdf(Order order) {
        if (order == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Color Palette
            Color primaryMaroon = new Color(128, 0, 32);     // #800020
            Color goldAccent = new Color(212, 175, 55);      // #D4AF37
            Color darkText = new Color(43, 38, 37);         // #2B2625
            Color lightBg = new Color(250, 247, 242);       // #FAF7F2
            Color borderGray = new Color(220, 220, 220);

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, primaryMaroon);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, goldAccent);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryMaroon);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, darkText);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, darkText);
            Font whiteBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, Color.WHITE);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

            // 1. Header Banner
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{65f, 35f});

            PdfPCell leftHeaderCell = new PdfPCell();
            leftHeaderCell.setBorder(Rectangle.NO_BORDER);
            leftHeaderCell.addElement(new Paragraph("SIDDHI PAITHANI", headerFont));
            leftHeaderCell.addElement(new Paragraph("THE AUTHENTIC HERITAGE OF YEOLA SILK", subHeaderFont));
            leftHeaderCell.addElement(new Paragraph("Yeola High School Road, Yeola, Nashik, Maharashtra - 423401", smallFont));
            leftHeaderCell.addElement(new Paragraph("GSTIN: 27AAACS1234F1Z9 | Silk Mark Certified | Phone: +91 72191 20935", smallFont));
            headerTable.addCell(leftHeaderCell);

            PdfPCell rightHeaderCell = new PdfPCell();
            rightHeaderCell.setBorder(Rectangle.NO_BORDER);
            rightHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph invTitle = new Paragraph("TAX INVOICE", titleFont);
            invTitle.setAlignment(Element.ALIGN_RIGHT);
            rightHeaderCell.addElement(invTitle);

            String orderNum = order.getOrderNumber() != null ? order.getOrderNumber() : "SP-" + order.getId();
            Paragraph invNo = new Paragraph("Invoice No: INV-" + orderNum, boldFont);
            invNo.setAlignment(Element.ALIGN_RIGHT);
            rightHeaderCell.addElement(invNo);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            String orderDateStr = order.getCreatedAt() != null ? order.getCreatedAt().toString() : sdf.format(new Date());
            Paragraph invDate = new Paragraph("Date: " + orderDateStr, normalFont);
            invDate.setAlignment(Element.ALIGN_RIGHT);
            rightHeaderCell.addElement(invDate);

            headerTable.addCell(rightHeaderCell);
            document.add(headerTable);

            // Divider Line
            Paragraph pLine = new Paragraph("__________________________________________________________________________________", subHeaderFont);
            pLine.setSpacingAfter(12);
            document.add(pLine);

            // 2. Customer & Shipping Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{50f, 50f});
            infoTable.setSpacingAfter(15);

            // Billed To / Shipped To Cell
            PdfPCell billCell = new PdfPCell();
            billCell.setBackgroundColor(lightBg);
            billCell.setPadding(10);
            billCell.setBorderColor(goldAccent);
            billCell.addElement(new Paragraph("CUSTOMER BILLING & SHIPPING DETAILS", boldFont));
            billCell.addElement(new Paragraph("Customer Name: " + (order.getCustomerName() != null ? order.getCustomerName() : "Valued Customer"), normalFont));
            billCell.addElement(new Paragraph("Mobile Phone: " + (order.getMobile() != null ? order.getMobile() : order.getCustomerPhone()), normalFont));
            billCell.addElement(new Paragraph("Email Address: " + (order.getEmail() != null ? order.getEmail() : order.getCustomerEmail()), normalFont));
            billCell.addElement(new Paragraph("Address: " + (order.getAddress() != null ? order.getAddress() : "") + ", " + (order.getCity() != null ? order.getCity() : "") + " - " + (order.getPincode() != null ? order.getPincode() : ""), normalFont));
            infoTable.addCell(billCell);

            // Order & Payment Status Cell
            PdfPCell payCell = new PdfPCell();
            payCell.setBackgroundColor(lightBg);
            payCell.setPadding(10);
            payCell.setBorderColor(goldAccent);
            payCell.addElement(new Paragraph("PAYMENT & ORDER DETAILS", boldFont));
            payCell.addElement(new Paragraph("Order Status: " + (order.getStatus() != null ? order.getStatus() : "CONFIRMED"), normalFont));
            payCell.addElement(new Paragraph("Payment Method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "Cash on Delivery / UPI"), normalFont));
            payCell.addElement(new Paragraph("Payment Status: " + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "PAID"), normalFont));
            payCell.addElement(new Paragraph("Handloom Mark: Certified 100% Pure Mulberry Silk & Real Zari", normalFont));
            infoTable.addCell(payCell);


            document.add(infoTable);

            // Gift Wrap Note Box
            if (Boolean.TRUE.equals(order.getGiftWrap())) {
                PdfPTable giftTable = new PdfPTable(1);
                giftTable.setWidthPercentage(100);
                giftTable.setSpacingAfter(15);

                PdfPCell giftCell = new PdfPCell();
                giftCell.setBackgroundColor(lightBg);
                giftCell.setPadding(10);
                giftCell.setBorderColor(goldAccent);
                giftCell.setBorderWidth(1.5f);

                giftCell.addElement(new Paragraph("🎁 ROYAL GIFT WRAPPING & HANDWRITTEN GREETING CARD (+₹150)", boldFont));
                String recipient = (order.getGiftRecipientName() != null && !order.getGiftRecipientName().trim().isEmpty()) ? order.getGiftRecipientName() : "Honored Recipient";
                String occasion = (order.getGiftOccasion() != null && !order.getGiftOccasion().trim().isEmpty()) ? order.getGiftOccasion() : "Special Celebration";
                giftCell.addElement(new Paragraph("Gift Recipient: " + recipient + " | Occasion: " + occasion, smallFont));

                if (order.getGiftMessage() != null && !order.getGiftMessage().trim().isEmpty()) {
                    Paragraph gMsg = new Paragraph("Handwritten Message: \"" + order.getGiftMessage() + "\"", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 10, primaryMaroon));
                    giftCell.addElement(gMsg);
                }
                giftCell.addElement(new Paragraph("✍️ Our Yeola artisans will handwrite this greeting note on a golden Paithani heirloom card!", smallFont));
                giftTable.addCell(giftCell);
                document.add(giftTable);
            }


            // 3. Itemized Tax Invoice Table
            PdfPTable itemsTable = new PdfPTable(6);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{8f, 40f, 12f, 10f, 15f, 15f});
            itemsTable.setSpacingAfter(15);

            // Table Headers
            String[] headers = {"S.No", "Item Description & Weave", "HSN Code", "Qty", "Unit Rate (₹)", "Total (₹)"};
            for (String headerText : headers) {
                PdfPCell hCell = new PdfPCell(new Phrase(headerText, whiteBoldFont));
                hCell.setBackgroundColor(primaryMaroon);
                hCell.setPadding(8);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itemsTable.addCell(hCell);
            }

            double totalItemAmount = 0.0;
            int sno = 1;

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (OrderItem item : order.getItems()) {
                    String pName = (item.getProduct() != null && item.getProduct().getName() != null) ? item.getProduct().getName() : "Yeola Pure Silk Paithani Saree";
                    int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                    double price = item.getPrice() != null ? item.getPrice() : (item.getProduct() != null && item.getProduct().getPrice() != null ? item.getProduct().getPrice() : 0.0);
                    double itemTotal = price * qty;
                    totalItemAmount += itemTotal;

                    itemsTable.addCell(createCell(String.valueOf(sno++), normalFont, Element.ALIGN_CENTER));
                    itemsTable.addCell(createCell(pName + "\n(Pure Silk & Gold Zari Handloom)", normalFont, Element.ALIGN_LEFT));
                    itemsTable.addCell(createCell("5007", normalFont, Element.ALIGN_CENTER));
                    itemsTable.addCell(createCell(String.valueOf(qty), normalFont, Element.ALIGN_CENTER));
                    itemsTable.addCell(createCell(String.format("₹%.2f", price), normalFont, Element.ALIGN_RIGHT));
                    itemsTable.addCell(createCell(String.format("₹%.2f", itemTotal), normalFont, Element.ALIGN_RIGHT));
                }
            } else {
                double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
                totalItemAmount = total;
                itemsTable.addCell(createCell("1", normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell("Authentic Yeola Silk Paithani Saree\n(Handwoven Pure Silk)", normalFont, Element.ALIGN_LEFT));
                itemsTable.addCell(createCell("5007", normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell("1", normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell(String.format("₹%.2f", total), normalFont, Element.ALIGN_RIGHT));
                itemsTable.addCell(createCell(String.format("₹%.2f", total), normalFont, Element.ALIGN_RIGHT));
            }

            // Royal Gift Wrapping & Greeting Card Line Item (+₹150.00)
            if (Boolean.TRUE.equals(order.getGiftWrap())) {
                double giftFee = order.getGiftWrapFee() != null ? order.getGiftWrapFee() : 150.0;
                itemsTable.addCell(createCell(String.valueOf(sno++), normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell("🎁 Royal Paithani Gift Wrapping & Greeting Card\n(Custom Handwritten Heirloom Card)", normalFont, Element.ALIGN_LEFT));
                itemsTable.addCell(createCell("9997", normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell("1", normalFont, Element.ALIGN_CENTER));
                itemsTable.addCell(createCell(String.format("₹%.2f", giftFee), normalFont, Element.ALIGN_RIGHT));
                itemsTable.addCell(createCell(String.format("₹%.2f", giftFee), normalFont, Element.ALIGN_RIGHT));
            }

            document.add(itemsTable);

            // 4. GST Breakdown & Total Calculation
            double grandTotal = order.getTotalAmount() != null ? order.getTotalAmount() : totalItemAmount;
            double taxableAmount = grandTotal / 1.05; // 5% GST included
            double gstAmount = grandTotal - taxableAmount;
            double cgst = gstAmount / 2.0;
            double sgst = gstAmount / 2.0;

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(45);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setSpacingAfter(20);

            summaryTable.addCell(createSummaryCell("Taxable Value:", normalFont));
            summaryTable.addCell(createSummaryCell(String.format("₹%.2f", taxableAmount), normalFont));

            summaryTable.addCell(createSummaryCell("CGST (2.5%):", normalFont));
            summaryTable.addCell(createSummaryCell(String.format("₹%.2f", cgst), normalFont));

            summaryTable.addCell(createSummaryCell("SGST (2.5%):", normalFont));
            summaryTable.addCell(createSummaryCell(String.format("₹%.2f", sgst), normalFont));

            summaryTable.addCell(createSummaryCell("Handloom Delivery / Packaging:", normalFont));
            summaryTable.addCell(createSummaryCell("FREE (₹0.00)", normalFont));

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL PAID INCL. GST:", boldFont));
            totalLabelCell.setBackgroundColor(lightBg);
            totalLabelCell.setPadding(6);
            totalLabelCell.setBorderColor(borderGray);

            PdfPCell totalValCell = new PdfPCell(new Phrase(String.format("₹%.2f", grandTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, primaryMaroon)));
            totalValCell.setBackgroundColor(lightBg);
            totalValCell.setPadding(6);
            totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValCell.setBorderColor(borderGray);

            summaryTable.addCell(totalLabelCell);
            summaryTable.addCell(totalValCell);

            document.add(summaryTable);

            // 5. Terms & Authenticity Seal Footer
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{60f, 40f});

            PdfPCell termsCell = new PdfPCell();
            termsCell.setBorder(Rectangle.NO_BORDER);
            termsCell.addElement(new Paragraph("TERMS & AUTHENTICITY CERTIFICATION:", boldFont));
            termsCell.addElement(new Paragraph("1. Certified 100% Handwoven Silk Mark India product.", smallFont));
            termsCell.addElement(new Paragraph("2. 7-Day Hassle-free return policy applies.", smallFont));
            termsCell.addElement(new Paragraph("3. Dry Clean Only recommended for long-lasting vibrant sheen.", smallFont));
            footerTable.addCell(termsCell);

            PdfPCell sealCell = new PdfPCell();
            sealCell.setBorder(Rectangle.NO_BORDER);
            sealCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph signP = new Paragraph("For SIDDHI PAITHANI YEOLA", boldFont);
            signP.setAlignment(Element.ALIGN_RIGHT);
            sealCell.addElement(signP);

            Paragraph sealP = new Paragraph("Vinayak Kukkar\n[ Authorized Master Weaver Seal ]", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, goldAccent));
            sealP.setAlignment(Element.ALIGN_RIGHT);
            sealCell.addElement(sealP);

            footerTable.addCell(sealCell);
            document.add(footerTable);

            document.close();
            logger.info("Successfully generated PDF GST Tax Invoice for Order #{}", order.getOrderNumber());
            return out.toByteArray();

        } catch (Exception e) {
            logger.error("Failed to generate PDF GST Tax Invoice for Order #{}: {}", order.getOrderNumber(), e.getMessage(), e);
            return new byte[0];
        }
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new Color(220, 220, 220));
        return cell;
    }

    private PdfPCell createSummaryCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderColor(new Color(220, 220, 220));
        return cell;
    }
}
