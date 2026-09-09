package com.siddhi.paithani.config;

import com.siddhi.paithani.interceptor.AdminAuthInterceptor;
import com.siddhi.paithani.interceptor.CustomerAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Autowired
    private CustomerAuthInterceptor customerAuthInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "classpath:/static/images/",
                        "file:src/main/resources/static/images/",
                        "file:target/classes/static/images/"
                );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Enforce mandatory Customer Login / Registration for all storefront URLs
        registry.addInterceptor(customerAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/register",
                        "/register/**",
                        "/forgot-password",
                        "/forgot-password/**",
                        "/admin/login",
                        "/admin/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/h2-console/**"
                );

        // 2. Enforce Admin Authentication for protected Admin Panel URLs
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/customers",
                        "/search",
                        "/showNewCustomerForm",
                        "/addCustomer",
                        "/saveCustomer",
                        "/showFormForUpdate/**",
                        "/editCustomer/**",
                        "/updateCustomer",
                        "/deleteCustomer/**"
                )
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}
