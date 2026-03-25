package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    void testCustomerName() {
        Customer customer = new Customer("Takagi", "email.com", "651", "Street");

        assertEquals("Takagi", customer.getName());
    }

    void testCustomerEmail() {
        Customer customer = new Customer("Takagi", "email.com", "651", "Street");

        assertEquals("email.com", customer.getEmail());
    }

    void testCustomerPhoneNumber() {
        Customer customer = new Customer("Takagi", "email.com", "651", "Street");

        assertEquals("651", customer.getPhone());
    }

    void testCustomerAddress() {
        Customer customer = new Customer("Takagi", "email.com", "651", "Street");

        assertEquals("Street", customer.getAddress());
    }
}
