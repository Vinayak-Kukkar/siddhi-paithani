package com.siddhi.paithani.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PdfSalesReportGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfSalesReportGeneratorService.class);

    public byte[] generateSalesReportPdf(List<Order> orders, List<Product> products, Map<String, Double> categoryRevenue, Map<String, Integer> categoryUnits) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Color Palette
            Color primaryMaroon = new Color(128, 0, 32);     // #800020
            Color goldAccent = new Color(212, 175, 55);      // #D4AF37
            Color darkCharcoal = new Color(43, 38, 37);      // #2B2625
            Color lightBg = new Color(255, 253, 245);       // #FFFDF5

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, primaryMaroon);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, goldAccent);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, darkCharcoal);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, darkCharcoal);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Font whiteBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font metricValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryMaroon);

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

            // 1. Header Banner Table
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{65f, 35f});
            headerTable.setSpacingAfter(15);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.addElement(new Paragraph("SIDDHI PAITHANI & SILK SAREES", headerFont));
            titleCell.addElement(new Paragraph("Yeola Handloom Heritage | Executive Sales Analytics & Business Report", subHeaderFont));
            titleCell.addElement(new Paragraph("GSTIN: 27AAACS1234F1Z9 | HSN: 5007 (Pure Handloom Silk)", smallFont));
            headerTable.addCell(titleCell);

            PdfPCell metaCell = new PdfPCell();
            metaCell.setBorder(Rectangle.NO_BORDER);
            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            String reportDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"));
            Paragraph pDate = new Paragraph("Report Date:\n" + reportDate, boldFont);
            pDate.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(pDate);

            Paragraph pScope = new Paragraph("Scope: Lifetime Sales Audit", smallFont);
            pScope.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(pScope);
            headerTable.addCell(metaCell);

            document.add(headerTable);

            // Divider Line
            Paragraph pLine = new Paragraph("__________________________________________________________________________________", subHeaderFont);
            pLine.setSpacingAfter(15);
            document.add(pLine);

            // 2. Executive KPI Cards Table (4 Columns)
            double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();
            long totalOrdersCount = orders.size();
            int totalSareesSold = orders.stream().flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty()).mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
            double avgOrderValue = totalOrdersCount > 0 ? totalRevenue / totalOrdersCount : 0.0;
            double totalGiftWrapRevenue = orders.stream().filter(o -> Boolean.TRUE.equals(o.getGiftWrap())).count() * 150.0;
            double totalDiscountGiven = orders.stream().mapToDouble(o -> o.getDiscountAmount() != null ? o.getDiscountAmount() : 0.0).sum();

            PdfPTable kpiTable = new PdfPTable(4);
            kpiTable.setWidthPercentage(100);
            kpiTable.setWidths(new float[]{25f, 25f, 25f, 25f});
            kpiTable.setSpacingAfter(15);

            kpiTable.addCell(createKpiCard("TOTAL REVENUE", currencyFormat.format(totalRevenue), lightBg, goldAccent, boldFont, metricValFont));
            kpiTable.addCell(createKpiCard("TOTAL ORDERS", String.valueOf(totalOrdersCount), lightBg, goldAccent, boldFont, metricValFont));
            kpiTable.addCell(createKpiCard("SAREES SOLD", totalSareesSold + " Units", lightBg, goldAccent, boldFont, metricValFont));
            kpiTable.addCell(createKpiCard("AVG ORDER VALUE", currencyFormat.format(avgOrderValue), lightBg, goldAccent, boldFont, metricValFont));

            document.add(kpiTable);

            // Additional Secondary Metrics Table (Gift Wrap & Discounts)
            PdfPTable secKpiTable = new PdfPTable(3);
            secKpiTable.setWidthPercentage(100);
            secKpiTable.setWidths(new float[]{33f, 34f, 33f});
            secKpiTable.setSpacingAfter(15);

            secKpiTable.addCell(createKpiCard("ACTIVE SAREE CATALOG", products.size() + " Sarees", lightBg, goldAccent, boldFont, metricValFont));
            secKpiTable.addCell(createKpiCard("GIFT WRAP REVENUE", currencyFormat.format(totalGiftWrapRevenue), lightBg, goldAccent, boldFont, metricValFont));
            secKpiTable.addCell(createKpiCard("COUPON DISCOUNTS SAVINGS", currencyFormat.format(totalDiscountGiven), lightBg, goldAccent, boldFont, metricValFont));

            document.add(secKpiTable);

            // 3. Category Sales Performance Breakdown Table
            Paragraph pCatTitle = new Paragraph("CATEGORY SALES & WEAVE PERFORMANCE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, primaryMaroon));
            pCatTitle.setSpacingAfter(8);
            document.add(pCatTitle);

            PdfPTable catTable = new PdfPTable(4);
            catTable.setWidthPercentage(100);
            catTable.setWidths(new float[]{40f, 20f, 25f, 15f});
            catTable.setSpacingAfter(15);

            String[] catHeaders = {"Saree Category / Weave Type", "Units Sold", "Category Revenue (₹)", "% Contribution"};
            for (String h : catHeaders) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, whiteBoldFont));
                hCell.setBackgroundColor(primaryMaroon);
                hCell.setPadding(6);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                catTable.addCell(hCell);
            }

            for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
                String catName = entry.getKey();
                Double rev = entry.getValue();
                Integer units = categoryUnits.getOrDefault(catName, 0);
                double pct = totalRevenue > 0 ? (rev / totalRevenue) * 100.0 : 0.0;

                catTable.addCell(createCell(catName, boldFont, Element.ALIGN_LEFT));
                catTable.addCell(createCell(String.valueOf(units), normalFont, Element.ALIGN_CENTER));
                catTable.addCell(createCell(currencyFormat.format(rev), boldFont, Element.ALIGN_RIGHT));
                catTable.addCell(createCell(String.format("%.1f%%", pct), normalFont, Element.ALIGN_CENTER));
            }
            document.add(catTable);

            // 4. Recent Top Orders Summary Table
            Paragraph pOrdTitle = new Paragraph("RECENT ORDERS AUDIT LOG", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, primaryMaroon));
            pOrdTitle.setSpacingAfter(8);
            document.add(pOrdTitle);

            PdfPTable ordTable = new PdfPTable(6);
            ordTable.setWidthPercentage(100);
            ordTable.setWidths(new float[]{15f, 25f, 18f, 15f, 12f, 15f});
            ordTable.setSpacingAfter(20);

            String[] ordHeaders = {"Order #", "Customer Name", "Payment Method", "Status", "Gift Wrap", "Total (₹)"};
            for (String h : ordHeaders) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, whiteBoldFont));
                hCell.setBackgroundColor(primaryMaroon);
                hCell.setPadding(6);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ordTable.addCell(hCell);
            }

            int count = 0;
            for (Order o : orders) {
                if (count++ >= 10) break; // Limit top 10 orders on PDF summary
                ordTable.addCell(createCell(o.getOrderNumber() != null ? o.getOrderNumber() : ("SP-" + o.getId()), boldFont, Element.ALIGN_CENTER));
                ordTable.addCell(createCell(o.getCustomerName() != null ? o.getCustomerName() : "Customer", normalFont, Element.ALIGN_LEFT));
                ordTable.addCell(createCell(o.getPaymentMethod() != null ? o.getPaymentMethod() : "UPI", smallFont, Element.ALIGN_LEFT));
                ordTable.addCell(createCell(o.getStatus() != null ? o.getStatus() : "CONFIRMED", boldFont, Element.ALIGN_CENTER));
                ordTable.addCell(createCell(Boolean.TRUE.equals(o.getGiftWrap()) ? "YES (+₹150)" : "NO", smallFont, Element.ALIGN_CENTER));
                ordTable.addCell(createCell(currencyFormat.format(o.getTotalAmount() != null ? o.getTotalAmount() : 0.0), boldFont, Element.ALIGN_RIGHT));
            }
            document.add(ordTable);

            // 5. Official Master Weaver Signature & Authenticity Stamp
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{60f, 40f});

            PdfPCell noteCell = new PdfPCell();
            noteCell.setBorder(Rectangle.NO_BORDER);
            noteCell.addElement(new Paragraph("REPORT NOTES & COMPLIANCE:", boldFont));
            noteCell.addElement(new Paragraph("1. Generated directly from Siddhi Paithani Master Database.", smallFont));
            noteCell.addElement(new Paragraph("2. All handloom transactions are subject to 5% GST (HSN 5007).", smallFont));
            noteCell.addElement(new Paragraph("3. Certified by Silk Mark Organization of India (SMOI).", smallFont));
            footerTable.addCell(noteCell);

            PdfPCell signCell = new PdfPCell();
            signCell.setBorder(Rectangle.NO_BORDER);
            signCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph signP = new Paragraph("For SIDDHI PAITHANI YEOLA", boldFont);
            signP.setAlignment(Element.ALIGN_RIGHT);
            signCell.addElement(signP);

            Paragraph sealP = new Paragraph("Vinayak Kukkar\n[ Master Weaver & Managing Director ]", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, goldAccent));
            sealP.setAlignment(Element.ALIGN_RIGHT);
            signCell.addElement(sealP);

            footerTable.addCell(signCell);
            document.add(footerTable);

            document.close();
            logger.info("Successfully generated Executive PDF Sales Report");
            return out.toByteArray();

        } catch (Exception e) {
            logger.error("Failed to generate PDF Sales Report: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private PdfPCell createKpiCard(String label, String value, Color bgColor, Color borderColor, Font labelFont, Font valFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(borderColor);
        cell.setBorderWidth(1.2f);
        cell.setPadding(8);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valFont));
        return cell;
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new Color(220, 220, 220));
        return cell;
    }
}
