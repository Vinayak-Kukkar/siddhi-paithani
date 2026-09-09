package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getFeaturedProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String keyword);
    Product getProductById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
    List<String> getAllCategories();
}
