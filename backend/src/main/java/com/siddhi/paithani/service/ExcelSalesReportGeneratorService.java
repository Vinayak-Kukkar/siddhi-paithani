package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.OrderItem;
import com.siddhi.paithani.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExcelSalesReportGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSalesReportGeneratorService.class);

    public byte[] generateExcelSalesReport(List<Order> orders, List<Product> products, Map<String, Double> categoryRevenue, Map<String, Integer> categoryUnits) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String reportDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();
        long totalOrdersCount = orders.size();
        int totalSareesSold = orders.stream().flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty()).mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
        double avgOrderValue = totalOrdersCount > 0 ? totalRevenue / totalOrdersCount : 0.0;
        double totalGiftWrapRevenue = orders.stream().filter(o -> Boolean.TRUE.equals(o.getGiftWrap())).count() * 150.0;
        double totalDiscountGiven = orders.stream().mapToDouble(o -> o.getDiscountAmount() != null ? o.getDiscountAmount() : 0.0).sum();

        // Write HTML XML Excel Format compatible with Microsoft Excel & Google Sheets
        writer.println("<?xml version=\"1.0\"?>");
        writer.println("<?mso-application progid=\"Excel.Sheet\"?>");
        writer.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
        writer.println(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
        writer.println(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
        writer.println(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">");
        
        // Styles
        writer.println(" <Styles>");
        writer.println("  <Style ss:ID=\"HeaderStyle\">");
        writer.println("   <Font ss:FontName=\"Calibri\" ss:Size=\"14\" ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>");
        writer.println("   <Interior ss:Color=\"#800020\" ss:Pattern=\"Solid\"/>");
        writer.println("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>");
        writer.println("  </Style>");
        writer.println("  <Style ss:ID=\"SubHeaderStyle\">");
        writer.println("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>");
        writer.println("   <Interior ss:Color=\"#D4AF37\" ss:Pattern=\"Solid\"/>");
        writer.println("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>");
        writer.println("  </Style>");
        writer.println("  <Style ss:ID=\"TitleStyle\">");
        writer.println("   <Font ss:FontName=\"Calibri\" ss:Size=\"16\" ss:Bold=\"1\" ss:Color=\"#800020\"/>");
        writer.println("  </Style>");
        writer.println("  <Style ss:ID=\"BoldCell\">");
        writer.println("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\"/>");
        writer.println("  </Style>");
        writer.println(" </Styles>");

        // Sheet 1: Executive Analytics Summary
        writer.println(" <Worksheet ss:Name=\"Executive Summary\">");
        writer.println("  <Table>");
        writer.println("   <Column ss:Width=\"220\"/>");
        writer.println("   <Column ss:Width=\"180\"/>");
        writer.println("   <Row ss:Height=\"30\"><Cell ss:StyleID=\"TitleStyle\"><Data ss:Type=\"String\">SIDDHI PAITHANI &amp; SILK SAREES - EXECUTIVE SALES ANALYTICS</Data></Cell></Row>");
        writer.println("   <Row><Cell><Data ss:Type=\"String\">Report Generated: " + reportDate + "</Data></Cell></Row>");
        writer.println("   <Row><Cell><Data ss:Type=\"String\">GSTIN: 27AAACS1234F1Z9 | HSN Code: 5007</Data></Cell></Row>");
        writer.println("   <Row></Row>");

        writer.println("   <Row ss:Height=\"24\"><Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Key Performance Indicator (KPI)</Data></Cell><Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Value</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Total Gross Revenue</Data></Cell><Cell><Data ss:Type=\"String\">" + currencyFormat.format(totalRevenue) + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Total Customer Orders</Data></Cell><Cell><Data ss:Type=\"Number\">" + totalOrdersCount + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Total Sarees Sold</Data></Cell><Cell><Data ss:Type=\"Number\">" + totalSareesSold + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Average Order Value (AOV)</Data></Cell><Cell><Data ss:Type=\"String\">" + currencyFormat.format(avgOrderValue) + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Active Product Catalog Size</Data></Cell><Cell><Data ss:Type=\"Number\">" + products.size() + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Total Gift Wrap Revenue</Data></Cell><Cell><Data ss:Type=\"String\">" + currencyFormat.format(totalGiftWrapRevenue) + "</Data></Cell></Row>");
        writer.println("   <Row><Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">Total Coupon Savings Granted</Data></Cell><Cell><Data ss:Type=\"String\">" + currencyFormat.format(totalDiscountGiven) + "</Data></Cell></Row>");

        writer.println("   <Row></Row>");
        writer.println("   <Row ss:Height=\"24\"><Cell ss:StyleID=\"SubHeaderStyle\"><Data ss:Type=\"String\">Saree Category / Weave Type</Data></Cell><Cell ss:StyleID=\"SubHeaderStyle\"><Data ss:Type=\"String\">Category Revenue</Data></Cell></Row>");
        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
            writer.println("   <Row><Cell><Data ss:Type=\"String\">" + escapeXml(entry.getKey()) + "</Data></Cell><Cell><Data ss:Type=\"String\">" + currencyFormat.format(entry.getValue()) + "</Data></Cell></Row>");
        }

        writer.println("  </Table>");
        writer.println(" </Worksheet>");

        // Sheet 2: All Customer Orders Audit
        writer.println(" <Worksheet ss:Name=\"All Orders Audit\">");
        writer.println("  <Table>");
        writer.println("   <Column ss:Width=\"110\"/>");
        writer.println("   <Column ss:Width=\"150\"/>");
        writer.println("   <Column ss:Width=\"100\"/>");
        writer.println("   <Column ss:Width=\"180\"/>");
        writer.println("   <Column ss:Width=\"220\"/>");
        writer.println("   <Column ss:Width=\"90\"/>");
        writer.println("   <Column ss:Width=\"110\"/>");
        writer.println("   <Column ss:Width=\"160\"/>");
        writer.println("   <Column ss:Width=\"100\"/>");
        writer.println("   <Column ss:Width=\"100\"/>");
        writer.println("   <Column ss:Width=\"100\"/>");

        writer.println("   <Row ss:Height=\"26\">");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Order #</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Customer Name</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Mobile Phone</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Email Address</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Shipping Address</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">City</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">PIN Code</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Payment Method</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Status</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Gift Wrapped</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Total Amount</Data></Cell>");
        writer.println("   </Row>");

        for (Order o : orders) {
            writer.println("   <Row>");
            writer.println("    <Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">" + escapeXml(o.getOrderNumber() != null ? o.getOrderNumber() : ("SP-" + o.getId())) + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getCustomerName() != null ? o.getCustomerName() : "Valued Customer") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getMobile() != null ? o.getMobile() : "") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getEmail() != null ? o.getEmail() : "") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getAddress() != null ? o.getAddress() : "") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getCity() != null ? o.getCity() : "") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getPincode() != null ? o.getPincode() : "") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getPaymentMethod() != null ? o.getPaymentMethod() : "UPI") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(o.getStatus() != null ? o.getStatus() : "CONFIRMED") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + (Boolean.TRUE.equals(o.getGiftWrap()) ? "YES (+₹150)" : "NO") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + currencyFormat.format(o.getTotalAmount() != null ? o.getTotalAmount() : 0.0) + "</Data></Cell>");
            writer.println("   </Row>");
        }

        writer.println("  </Table>");
        writer.println(" </Worksheet>");

        // Sheet 3: Product Inventory & Low Stock Watchlist
        writer.println(" <Worksheet ss:Name=\"Inventory & Stock\">");
        writer.println("  <Table>");
        writer.println("   <Column ss:Width=\"50\"/>");
        writer.println("   <Column ss:Width=\"250\"/>");
        writer.println("   <Column ss:Width=\"150\"/>");
        writer.println("   <Column ss:Width=\"120\"/>");
        writer.println("   <Column ss:Width=\"100\"/>");
        writer.println("   <Column ss:Width=\"120\"/>");

        writer.println("   <Row ss:Height=\"26\">");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">ID</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Saree Name</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Category</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Price (₹)</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Stock Level</Data></Cell>");
        writer.println("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">Stock Alert</Data></Cell>");
        writer.println("   </Row>");

        for (Product p : products) {
            int stock = p.getStock() != null ? p.getStock() : 0;
            String alert = stock <= 3 ? "⚠️ LOW STOCK ALERT" : "OK";
            writer.println("   <Row>");
            writer.println("    <Cell><Data ss:Type=\"Number\">" + p.getId() + "</Data></Cell>");
            writer.println("    <Cell ss:StyleID=\"BoldCell\"><Data ss:Type=\"String\">" + escapeXml(p.getName()) + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + escapeXml(p.getCategory() != null ? p.getCategory() : "Yeola Paithani") + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + currencyFormat.format(p.getPrice() != null ? p.getPrice() : 0.0) + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"Number\">" + stock + "</Data></Cell>");
            writer.println("    <Cell><Data ss:Type=\"String\">" + alert + "</Data></Cell>");
            writer.println("   </Row>");
        }

        writer.println("  </Table>");
        writer.println(" </Worksheet>");

        writer.println("</Workbook>");
        writer.flush();

        logger.info("Successfully generated Excel Multi-Sheet Analytics Workbook");
        return out.toByteArray();
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
