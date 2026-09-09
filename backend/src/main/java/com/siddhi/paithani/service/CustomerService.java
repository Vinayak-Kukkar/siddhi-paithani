package com.siddhi.paithani.service;

import java.util.List;
import com.siddhi.paithani.entity.Customer;

public interface CustomerService {

    Customer saveCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Long id);

    Customer updateCustomer(Customer customer);

    void deleteCustomer(Long id);
    
    List<Customer> searchCustomer(String keyword);

    Customer registerOrLoginCustomer(Customer customer);

    Customer registerCustomer(Customer customer, String rawPassword);

    Customer loginCustomer(String emailOrMobile, String rawPassword);

    Customer findByEmailOrMobile(String emailOrMobile);
}