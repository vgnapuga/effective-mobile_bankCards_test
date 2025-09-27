package com.example.bankcards;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.bankcards.service.UserService;

import jakarta.annotation.PostConstruct;


@SpringBootApplication
public class BankcardsApplication {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }

    @PostConstruct
    public void checkBeans() {
        String[] userServiceBeans = context.getBeanNamesForType(UserService.class);
        System.out.println("UserService beans count: " + userServiceBeans.length);
        for (String beanName : userServiceBeans) {
            System.out.println("Bean: " + beanName + " -> " + context.getBean(beanName).getClass());
        }
    }

}
