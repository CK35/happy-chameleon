package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import de.ck35.raspberry.sensors.temperature.DHTSensor;
import de.ck35.raspberry.sensors.temperature.DHTSensor.Type;

@Configuration
public class GpioControllerConfiguration {

    @Bean
    public GpioController gpioController() {
        return GpioFactory.getInstance();
    }
    
    @Bean
    public DHTSensor dhtSensor() {
        return new DHTSensor(Type.AM2302, 17);
    }
    
}