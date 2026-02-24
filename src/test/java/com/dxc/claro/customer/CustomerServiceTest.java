package com.dxc.claro.customer;

import com.dxc.claro.customer.model.Customer;
import com.dxc.claro.customer.repository.CustomerRepository;
import com.dxc.claro.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(customerService, "queueName", "customer-queue");
    }

    @Test
    void testSaveCustomer() {
        Customer customer = Customer.builder()
                .name("Jose da Silva")
                .email("josedasilva@example.com")
                .document("123456789")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.save(customer);

        assertEquals("Jose da Silva", savedCustomer.getName());
        verify(customerRepository, times(1)).save(customer);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("customer-queue"), any(String.class));
    }

    @Test
    void testFindById() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Jose da Silva")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> foundCustomer = customerService.findById(1L);

        assertEquals("Jose da Silva", foundCustomer.get().getName());
        verify(customerRepository, times(1)).findById(1L);
    }
}
