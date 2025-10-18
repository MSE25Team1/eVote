package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test
    void greetReturnsDefaultForBlank() {
        App a = new App();
        assertEquals("Hello, world!", a.greet(""));
    }

    @Test
    void greetReturnsName() {
        App a = new App();
        assertEquals("Hello, Pascal!", a.greet("Pascal"));
    }
}