package com.dxc.claro.customer.service;

import com.dxc.claro.customer.model.Customer;
import com.dxc.claro.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${customer.queue.name}")
    private String queueName;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        sendMessageToQueue("Customer created/updated: " + savedCustomer.getName());
        return savedCustomer;
    }

    public void deleteById(Long id) {
        customerRepository.deleteById(id);
        sendMessageToQueue("Customer deleted with ID: " + id);
    }

    private void sendMessageToQueue(String message) {
        log.info("Sending message to RabbitMQ: {}", message);
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @RabbitListener(queues = "${customer.queue.name}")
    public void receiveMessage(String message) {
        log.info("Message received from RabbitMQ: {}", message);
    }
}
