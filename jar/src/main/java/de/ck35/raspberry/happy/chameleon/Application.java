package de.ck35.raspberry.happy.chameleon;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import de.ck35.raspberry.happy.chameleon.configuration.RootConfiguration;

public class Application extends SpringApplication {

    public static void main(String[] args) {
        Application.run(RootConfiguration.class, args);
    }
    @Override
    protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
        super.configurePropertySources(environment, args);
        
//        environment.getPropertySources().addLast(new ClassPathResource("application.properties"));
    }
}