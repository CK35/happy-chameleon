package de.ck35.raspberry.happy.chameleon;

import org.springframework.boot.SpringApplication;

import de.ck35.raspberry.happy.chameleon.configuration.RootConfiguration;

public class Application extends SpringApplication {

    public static void main(String[] args) {
        Application.run(RootConfiguration.class, args);
    }
    
}