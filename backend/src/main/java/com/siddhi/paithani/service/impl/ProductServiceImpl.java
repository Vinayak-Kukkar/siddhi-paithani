package com.siddhi.paithani.service.impl;

import com.siddhi.paithani.entity.OrderItem;
import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.repository.OrderItemRepository;
import com.siddhi.paithani.repository.ProductRepository;
import com.siddhi.paithani.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("ALL")) {
            return productRepository.findAll();
        }
        return productRepository.findByCategoryIgnoreCase(category);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            List<OrderItem> orderItems = orderItemRepository.findByProductId(id);
            if (orderItems != null && !orderItems.isEmpty()) {
                orderItemRepository.deleteAll(orderItems);
            }
            productRepository.delete(product);
        }
    }

    @Override
    public List<String> getAllCategories() {
        return Arrays.asList("Yeola Paithani", "Maharani Paithani", "Peshwai Paithani", "Pure Silk Saree", "Silk Dupatta");
    }
}
