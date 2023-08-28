package com.bronx.productservice.service;

import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    public void sendEmail(String orderNumber){
        System.out.println("Order placed successfully - order number is: "+ orderNumber);
        System.out.println("Email Sent");
    }
}
