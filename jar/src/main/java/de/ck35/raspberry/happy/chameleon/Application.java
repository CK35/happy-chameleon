package de.ck35.raspberry.happy.chameleon;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import com.pi4j.io.gpio.GpioFactory;

import de.ck35.raspberry.happy.chameleon.configuration.GpioConfiguration;
import de.ck35.raspberry.happy.chameleon.configuration.RootConfiguration;

public class Application extends SpringApplication {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Application.run(RootConfiguration.class, args);
    }

    @Override
    protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
        super.configurePropertySources(environment, args);

        ClassPathResource classPathApplicationProperties = new ClassPathResource("application.properties");
        if (classPathApplicationProperties.exists()) {
            try {
                environment.getPropertySources()
                           .addLast(new ResourcePropertySource(classPathApplicationProperties));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    protected ConfigurableApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setDisplayName("happy-chameleon-context");

        try {
            context.getBeanFactory().registerSingleton("gpioController", GpioFactory.getInstance());
            context.getEnvironment().addActiveProfile(GpioConfiguration.PROFILE);
        } catch (UnsatisfiedLinkError e) {
            LOG.warn("Could not create gpioController. Skipping further gpio configuration.");
        }

        return context;
    }
}