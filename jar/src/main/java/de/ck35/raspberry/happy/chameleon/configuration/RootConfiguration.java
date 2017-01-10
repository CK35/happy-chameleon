package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.ck35.raspberry.happy.chameleon.rest.DHTSensorController;
import de.ck35.raspberry.sensors.temperature.DHTSensor;

@Configuration
@EnableAutoConfiguration
@Import({GpioControllerConfiguration.class})
public class RootConfiguration {

    @Autowired DHTSensor dhtSensor;
    
    @Bean
    public DHTSensorController helloWorldController() {
        return new DHTSensorController(dhtSensor);
    }
    
}