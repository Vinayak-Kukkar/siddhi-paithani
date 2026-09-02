package com.siddhi.paithani.config;

import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(ProductRepository productRepository, com.siddhi.paithani.repository.CouponRepository couponRepository) {
        return args -> {
            // Seed Coupons
            if (couponRepository.count() == 0) {
                couponRepository.save(new com.siddhi.paithani.entity.Coupon(null, "PAITHANI10", "PERCENTAGE", 10.0, 1000.0, 12, 500, true, "10% Off on Pure Silk Paithanis", java.time.LocalDateTime.now()));
                couponRepository.save(new com.siddhi.paithani.entity.Coupon(null, "YEOLA15", "PERCENTAGE", 15.0, 5000.0, 8, 200, true, "15% Off on Authentic Yeola Handlooms", java.time.LocalDateTime.now()));
                couponRepository.save(new com.siddhi.paithani.entity.Coupon(null, "FESTIVE500", "FLAT_AMOUNT", 500.0, 3000.0, 25, 1000, true, "Flat ₹500 Off Festival Special Discount", java.time.LocalDateTime.now()));
                couponRepository.save(new com.siddhi.paithani.entity.Coupon(null, "ROYAL20", "PERCENTAGE", 20.0, 15000.0, 5, 100, true, "20% Off Royal Bridal Silk Sarees", java.time.LocalDateTime.now()));
                couponRepository.save(new com.siddhi.paithani.entity.Coupon(null, "WELCOME50", "FLAT_AMOUNT", 250.0, 1500.0, 42, 500, true, "₹250 Off First Order Welcome Offer", java.time.LocalDateTime.now()));
            }

            List<String> imageUrls = Arrays.asList(

                "https://www.nishalee.com/wp-content/uploads/2021/07/Katan-Banarasi-Paithani-Design-4-662x1024.jpeg",
                "https://tse1.mm.bing.net/th/id/OIP.YU4-S1q2PD8_F4JBjV90UAHaJ5?r=0&w=766&h=1024&rs=1&pid=ImgDetMain&o=7&rm=3",
                "https://tse4.mm.bing.net/th/id/OIP.T667vTSaW9fvn4UNd5g2AQHaJ4?r=0&pid=ImgDet&w=178&h=237&c=7&dpr=1.5&o=7&rm=3",
                "https://tse2.mm.bing.net/th/id/OIP.eKf7VW55TZKK5kHTfUiT6gAAAA?r=0&pid=ImgDet&w=178&h=310&c=7&dpr=1.5&o=7&rm=3",
                "https://pratishthani.com/wp-content/uploads/2023/03/Blue-Handloom-Tissue-Pallu-Paithani-Saree3-480x638.jpg",
                "https://pratishthani.com/wp-content/uploads/2023/03/Blue-Handloom-Tissue-Pallu-Paithani-Saree.jpg.webp",
                "https://i.pinimg.com/736x/86/70/8c/86708c513d1b03bfdf37caf944a0c84a.jpg",
                "https://tse4.mm.bing.net/th/id/OIP.Exl0uTjvjRtY7sZgyjRIrwHaJ3?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
                "https://i.pinimg.com/736x/63/21/7a/63217a76613f78136ed7d4e21296a303.jpg",
                "https://www.quicklly.com/upload_images/product/1699541558-2-traditional-handloom-pure-silk-paithani-beautiful-peach-pink-with-fresh-green-border.jpg",
                "https://www.quicklly.com/upload_images/product/1699539796-2-exclusive-traditional-pretty-peach-pure-silk-double-pallu-paithani-with-designer-peacocks-pallu.jpg",
                "https://in.ompaithani.com/wp-content/uploads/2024/05/WhatsApp-Image-2022-12-07-at-11.43.42-AM-5.jpeg",
                "https://5.imimg.com/data5/SELLER/Default/2024/11/467339981/CL/NB/DY/81961985/154-gsm-yeola-silk-paithani-saree-1000x1000.jpg"
            );

            List<Product> seedProducts = Arrays.asList(
                new Product(
                        null,
                        "Yeola Fancy Double Pallu With Large Butti",
                        "Yeola Paithani",
                        24999.00,
                        "Authentic handwoven Yeola Paithani silk saree featuring rich peacock (Mor) zari motifs on pallu and golden borders.",
                        imageUrls.get(0),
                        "Crimson Red",
                        "Pure Mulberry Silk",
                        10,
                        true
                ),
                new Product(
                        null,
                        "Yeola Fancy Double Pallu With samll Butti",
                        "Yeola Paithani",
                        32500.00,
                        "Exquisite Grand Yeola Paithani saree in deep emerald green woven with intricate gold zari brocade and traditional lotus border.",
                        imageUrls.get(1),
                        "Emerald Green",
                        "Pure Silk & Gold Zari",
                        8,
                        true
                ),
                new Product(
                        null,
                        "Fancy Dark Blue Colour in Double Pallu",
                        "Yeola Paithani",
                        18999.00,
                        "Classic Maharashtrian Peshwai style Paithani saree in vibrant yellow with contrasting magenta purple pallu.",
                        imageUrls.get(2),
                        "Dark Blue",
                        "Soft Handloom Silk",
                        15,
                        true
                ),
                new Product(
                        null,
                        "Fancy Peacock Pallu with uniqye colour",
                        "Pure Silk Saree",
                        12499.00,
                        "Elegant royal blue silk saree with intricate peacock zari border, lightweight and perfect for traditional festivities.",
                        imageUrls.get(3),
                        "Peacock Blue",
                        "Pure Silk",
                        20,
                        false
                ),
                new Product(
                        null,
                        "Golden Zari Fancy Tissue pallu",
                        "Tissue Paithani",
                        14999.00,
                        "Handcrafted rich tissue silk Paithani with golden zari woven borders and tassels.",
                        imageUrls.get(4),
                        "Golden Magenta",
                        "Tissue Silk",
                        25,
                        false
                ),
                new Product(
                        null,
                        "Fancy Tissue Pallu",
                        "Tissue Paithani",
                        38999.00,
                        "Premium heavy tissue double gas woven Paithani saree with kaleidoscope tissue pallu and double peacocks.",
                        imageUrls.get(5),
                        "Royal Purple",
                        "Tissue Pure Silk",
                        5,
                        true
                ),
                new Product(
                        null,
                        "Yeola fancy Brocket Single Munia with Dark Pink Colour",
                        "Yeola Paithani",
                        34999.00,
                        "Regal dark pink pure silk Yeola Paithani with full gold zari brocade body, Single Munia border, and temple border.",
                        imageUrls.get(6),
                        "Dark Pink",
                        "Pure Silk & Real Zari",
                        7,
                        true
                ),
                new Product(
                        null,
                        "Yeola fancy Brocket Triple Munia with Parrot Design",
                        "Yeola Paithani",
                        28500.00,
                        "Delicate handloom Yeola Paithani saree with Triple Munia parrot design border.",
                        imageUrls.get(7),
                        "Parrot Green",
                        "Handloom Mulberry Silk",
                        12,
                        true
                ),
                new Product(
                        null,
                        "Yeola Fancy Brocket with Single Munia with Unique Design in Bright Golden Colour",
                        "Yeola Paithani",
                        22999.00,
                        "Stunning bright golden Yeola Paithani featuring Single Munia woven pallu and unique designer motifs.",
                        imageUrls.get(8),
                        "Bright Golden",
                        "Pure Soft Silk",
                        15,
                        false
                ),
                new Product(
                        null,
                        "Yeola Fancy Double Pallu with Famous Colour",
                        "Yeola Paithani",
                        26750.00,
                        "Vibrant famous colour Yeola Paithani saree embellished with Double Pallu and intricate zari buttis.",
                        imageUrls.get(9),
                        "Peach Pink",
                        "Pure Silk",
                        9,
                        false
                ),
                new Product(
                        null,
                        "Yeola Fancy Double Pallu with Baby Pink Colour",
                        "Yeola Paithani",
                        42000.00,
                        "Royal Maharashtrian Paithani in elegant baby pink with heavy gold finish zari Double Pallu and peacock borders.",
                        imageUrls.get(10),
                        "Baby Pink",
                        "Tissue Silk & Gold Zari",
                        4,
                        true
                ),
                new Product(
                        null,
                        "Voilet and Golden zari Double Pallu",
                        "Yeola Paithani",
                        15999.00,
                        "Graceful violet purple and golden zari Double Pallu Paithani saree with soft silk finish.",
                        imageUrls.get(11),
                        "Violet & Gold",
                        "Pure Silk Blend",
                        18,
                        false
                ),
                new Product(
                        null,
                        "Yeola Fancy Double Pallu with Large Butti with Brownish Colour",
                        "Yeola Paithani",
                        1.00,
                        "Exquisite 154 GSM Yeola Silk Paithani Saree featuring Large Butti and Double Pallu weaving.",
                        imageUrls.get(12),
                        "Brownish Gold",
                        "Pure Mulberry Silk",
                        10,
                        true
                )
            );

            // Ensure missing products are created
            if (productRepository.count() < seedProducts.size()) {
                for (Product p : seedProducts) {
                    if (productRepository.findByName(p.getName()).isEmpty()) {
                        productRepository.save(p);
                    }
                }
            }

            // Update product images, names, categories, and prices for all existing items to stay perfectly in sync
            List<Product> existingProducts = productRepository.findAll();
            for (Product p : existingProducts) {
                if (p.getImageUrl() != null && (p.getImageUrl().contains("bing.net") || p.getImageUrl().contains("5Ig6JXE3U0BDAACX5tbZQQ") || p.getId() == 1L || (p.getName() != null && p.getName().contains("Yeola Fancy Double Pallu With Large Butti")))) {
                    p.setImageUrl("https://www.nishalee.com/wp-content/uploads/2021/07/Katan-Banarasi-Paithani-Design-4-662x1024.jpeg");
                    productRepository.save(p);
                }
            }

            for (int i = 0; i < seedProducts.size(); i++) {
                Product seed = seedProducts.get(i);
                if (i < existingProducts.size()) {
                    Product p = existingProducts.get(i);
                    p.setName(seed.getName());
                    p.setCategory(seed.getCategory());
                    p.setPrice(seed.getPrice());
                    p.setImageUrl(seed.getImageUrl());
                    p.setColor(seed.getColor());
                    productRepository.save(p);
                } else {
                    productRepository.save(seed);
                }
            }
        };
    }
}
