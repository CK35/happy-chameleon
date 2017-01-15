package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.Devices;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;

@Configuration
public class DeviceConfiguration {

    @Bean
    public Sensor temperatureSensor() {
        return new Sensor(5, 50d);
    }
    @Bean
    public Sensor humiditySensor() {
        return new Sensor(5, 50d);
    }
    @Bean
    public BinarySensor leftDoorSensor() {
        return new BinarySensor(5, 50d);
    }
    @Bean
    public BinarySensor rightDoorSensor() {
        return new BinarySensor(5, 50d);
    }
 
    @Bean
    public Devices devices() {
        return new Devices(temperatureSensor(), humiditySensor(), leftDoorSensor(), rightDoorSensor());
    }
}