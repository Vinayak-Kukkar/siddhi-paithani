package com.siddhi.paithani.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.repository.CustomerRepository;
import com.siddhi.paithani.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // Search Customer by Name
    @Override
    public List<Customer> searchCustomer(String keyword) {
        return customerRepository.findByCustomerNameContainingIgnoreCase(keyword);
    }

    @Override
    public Customer registerOrLoginCustomer(Customer customer) {
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(customer.getEmail().trim());
            if (existingByEmail.isPresent()) {
                Customer c = existingByEmail.get();
                if (customer.getCustomerName() != null && !customer.getCustomerName().trim().isEmpty()) {
                    c.setCustomerName(customer.getCustomerName().trim());
                }
                if (customer.getMobile() != null && !customer.getMobile().trim().isEmpty()) {
                    c.setMobile(customer.getMobile().trim());
                }
                if (customer.getAddress() != null && !customer.getAddress().trim().isEmpty()) {
                    c.setAddress(customer.getAddress().trim());
                }
                if (customer.getCity() != null && !customer.getCity().trim().isEmpty()) {
                    c.setCity(customer.getCity().trim());
                }
                if (customer.getPincode() != null && !customer.getPincode().trim().isEmpty()) {
                    c.setPincode(customer.getPincode().trim());
                }
                return customerRepository.save(c);
            }
        }
        
        if (customer.getMobile() != null && !customer.getMobile().trim().isEmpty()) {
            Optional<Customer> existingByMobile = customerRepository.findByMobile(customer.getMobile().trim());
            if (existingByMobile.isPresent()) {
                Customer c = existingByMobile.get();
                if (customer.getCustomerName() != null && !customer.getCustomerName().trim().isEmpty()) {
                    c.setCustomerName(customer.getCustomerName().trim());
                }
                if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
                    c.setEmail(customer.getEmail().trim());
                }
                if (customer.getAddress() != null && !customer.getAddress().trim().isEmpty()) {
                    c.setAddress(customer.getAddress().trim());
                }
                if (customer.getCity() != null && !customer.getCity().trim().isEmpty()) {
                    c.setCity(customer.getCity().trim());
                }
                if (customer.getPincode() != null && !customer.getPincode().trim().isEmpty()) {
                    c.setPincode(customer.getPincode().trim());
                }
                return customerRepository.save(c);
            }
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer registerCustomer(Customer customer, String rawPassword) {
        if (customer.getEmail() != null) {
            customer.setEmail(customer.getEmail().trim().toLowerCase());
        }
        if (customer.getMobile() != null) {
            customer.setMobile(customer.getMobile().trim());
        }

        // Check if existing customer exists by email
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            Optional<Customer> existingByEmail = customerRepository.findByEmailIgnoreCase(customer.getEmail());
            if (existingByEmail.isPresent()) {
                Customer existing = existingByEmail.get();
                if (existing.getPassword() != null && !existing.getPassword().trim().isEmpty()) {
                    throw new IllegalArgumentException("This email is already registered. Please sign in or use a different email.");
                }
                // Upgrade unpassworded profile
                if (customer.getCustomerName() != null) existing.setCustomerName(customer.getCustomerName());
                if (customer.getMobile() != null) existing.setMobile(customer.getMobile());
                if (customer.getAddress() != null) existing.setAddress(customer.getAddress());
                if (customer.getCity() != null) existing.setCity(customer.getCity());
                if (customer.getPincode() != null) existing.setPincode(customer.getPincode());
                if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                    existing.setPassword(passwordEncoder.encode(rawPassword.trim()));
                }
                return customerRepository.save(existing);
            }
        }

        // Check if existing customer exists by mobile
        if (customer.getMobile() != null && !customer.getMobile().trim().isEmpty()) {
            Optional<Customer> existingByMobile = customerRepository.findByMobile(customer.getMobile());
            if (existingByMobile.isPresent()) {
                Customer existing = existingByMobile.get();
                if (existing.getPassword() != null && !existing.getPassword().trim().isEmpty()) {
                    throw new IllegalArgumentException("This mobile number is already registered. Please sign in or use a different mobile number.");
                }
                // Upgrade unpassworded profile
                if (customer.getCustomerName() != null) existing.setCustomerName(customer.getCustomerName());
                if (customer.getEmail() != null) existing.setEmail(customer.getEmail());
                if (customer.getAddress() != null) existing.setAddress(customer.getAddress());
                if (customer.getCity() != null) existing.setCity(customer.getCity());
                if (customer.getPincode() != null) existing.setPincode(customer.getPincode());
                if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                    existing.setPassword(passwordEncoder.encode(rawPassword.trim()));
                }
                return customerRepository.save(existing);
            }
        }

        // Encode password with BCrypt
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(rawPassword.trim()));
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer loginCustomer(String emailOrMobile, String rawPassword) {
        if (emailOrMobile == null || emailOrMobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your Email Address or Mobile Number.");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your Password.");
        }

        String input = emailOrMobile.trim();
        Customer customer = findByEmailOrMobile(input);

        if (customer == null) {
            throw new IllegalArgumentException("No account found for: " + input + ". Please check your credentials or register.");
        }

        if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            // Legacy/unpassworded customer profile - update password with BCrypt
            customer.setPassword(passwordEncoder.encode(rawPassword.trim()));
            return customerRepository.save(customer);
        }

        if (!passwordEncoder.matches(rawPassword.trim(), customer.getPassword())) {
            throw new IllegalArgumentException("Incorrect password. Please try again.");
        }

        return customer;
    }

    @Override
    public Customer findByEmailOrMobile(String emailOrMobile) {
        if (emailOrMobile == null || emailOrMobile.trim().isEmpty()) {
            return null;
        }
        String input = emailOrMobile.trim();
        Optional<Customer> byEmail = customerRepository.findByEmailIgnoreCase(input);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }
        return customerRepository.findByMobile(input).orElse(null);
    }
}