package de.ck35.raspberry.happy.chameleon.configuration;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.SerializationFeature;

import de.ck35.raspberry.happy.chameleon.rest.DevicesController;

@Configuration
@EnableAutoConfiguration
@Import({ DeviceConfiguration.class, GpioConfiguration.class, JpaConfiguration.class })
public class RootConfiguration {

    @Autowired Environment env;

    @Bean
    public DevicesController devicesController() {
        return new DevicesController();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.autoDetectGettersSetters(false)
                   .autoDetectFields(true)
                   .indentOutput(true)
                   .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(env.getProperty("rootConfiguration.clock.zoneId", "Europe/Berlin")));
    }
}