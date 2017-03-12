package de.ck35.raspberry.happy.chameleon.configuration;

import java.time.Clock;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.RetentionPolicy;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.happy.chameleon.devices.SlidingValues;
import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.EventDispatcher;
import de.ck35.raspberry.happy.chameleon.terrarium.RainSystemTimer;
import de.ck35.raspberry.happy.chameleon.terrarium.Supervisor;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.RainProgramm.RainProgramms;

@Configuration
public class DeviceConfiguration {

    @Autowired Environment env;
    @Autowired Clock clock;
    @Autowired RainProgramms rainProgramms;

    @Bean
    public Sensor temperatureSensor() {
        Duration retentionDuration = Duration.parse(env.getProperty("deviceConfiguration.temperatureSensor.retentionDuration", "PT2M"));
        int numberOfValues = env.getProperty("deviceConfiguration.temperatureSensor.numberOfValues", Integer.TYPE, 5);
        double quantile = env.getProperty("deviceConfiguration.temperatureSensor.quantile", Double.TYPE, 0.5);
        double delta = env.getProperty("deviceConfiguration.temperatureSensor.delta", Double.TYPE, 0.5);
        return new Sensor(new RetentionPolicy(clock, retentionDuration), new SlidingValues(numberOfValues, quantile), eventDispatcher(), delta);
    }

    @Bean
    public Sensor humiditySensor() {
        Duration retentionDuration = Duration.parse(env.getProperty("deviceConfiguration.humiditySensor.retentionDuration", "PT2M"));
        int numberOfValues = env.getProperty("deviceConfiguration.humiditySensor.numberOfValues", Integer.TYPE, 5);
        double quantile = env.getProperty("deviceConfiguration.humiditySensor.quantile", Double.TYPE, 0.5);
        double delta = env.getProperty("deviceConfiguration.humiditySensor.delta", Double.TYPE, 0.5);
        return new Sensor(new RetentionPolicy(clock, retentionDuration), new SlidingValues(numberOfValues, quantile), eventDispatcher(), delta);
    }

    @Bean
    public BinarySensor leftDoorSensor() {
        Duration retentionDuration = Duration.parse(env.getProperty("deviceConfiguration.leftDoorSensor.retentionDuration", "PT2M"));
        int minNumberOfSameValues = env.getProperty("deviceConfiguration.leftDoorSensor.minNumberOfSameValues", Integer.TYPE, 5);
        return new BinarySensor(new RetentionPolicy(clock, retentionDuration), minNumberOfSameValues, eventDispatcher());
    }

    @Bean
    public BinarySensor rightDoorSensor() {
        Duration retentionDuration = Duration.parse(env.getProperty("deviceConfiguration.rightDoorSensor.retentionDuration", "PT2M"));
        int minNumberOfSameValues = env.getProperty("deviceConfiguration.rightDoorSensor.minNumberOfSameValues", Integer.TYPE, 5);
        return new BinarySensor(new RetentionPolicy(clock, retentionDuration), minNumberOfSameValues, eventDispatcher());
    }
    
    @Bean
    public Switch rainSystemTimerSwitch() {
        return new Switch(eventDispatcher());
    }

    @Bean
    public Switch rainSystemSwitch() {
        return new Switch(eventDispatcher());
    }
    
    @Bean
    public Switch heatLampSwitch1() {
        return new Switch(eventDispatcher());
    }
    
    @Bean
    public Switch lightBulpSwitch1() {
        return new Switch(eventDispatcher());
    }
    
    @Bean
    public EventDispatcher eventDispatcher() {
        return new EventDispatcher();
    }
    
    @Bean
    public Supervisor supervisor() {
        Supervisor supervisor = new Supervisor();
        eventDispatcher().addListener(supervisor::update);
        return supervisor;
    }

    @Bean
    public RainSystemTimer rainSystemTimer() {
        return new RainSystemTimer(rainSystemTimerSwitch(), rainProgramms, clock);
    }
}