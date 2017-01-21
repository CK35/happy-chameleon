package de.ck35.raspberry.happy.chameleon.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.DeviceStatusUpdater;
import de.ck35.raspberry.happy.chameleon.devices.DeviceStatusUpdater.DHTSensorUpdater;
import de.ck35.raspberry.happy.chameleon.devices.DeviceStatusUpdater.DigitalPinStateUpdater;
import de.ck35.raspberry.happy.chameleon.devices.DeviceStatusUpdater.SensorUpdater;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.sensors.temperature.DHTSensor;

@Configuration
@Profile(GpioConfiguration.PROFILE)
public class GpioConfiguration {

    public static final String PROFILE = "GpioConfigurationEnabled";

    @Autowired Environment env;
    @Autowired GpioController gpioController;

    @Autowired Sensor temperatureSensor;
    @Autowired Sensor humiditySensor;
    @Autowired BinarySensor leftDoorSensor;
    @Autowired BinarySensor rightDoorSensor;

    @Autowired List<SensorUpdater> sensorUpdateTasks;

    @Bean
    public DeviceStatusUpdater deviceStatusUpdater() {
        return new DeviceStatusUpdater(sensorUpdateTasks);
    }

    @Bean
    public ThreadPoolTaskScheduler deviceStatusUpdateTaskScheduler() {
        long updatePeriodMillis = env.getProperty("gpioConfiguration.deviceStatusUpdateTaskScheduler.updatePeriodMillis", Long.TYPE, 100L);

        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setErrorHandler(deviceStatusUpdater());
        executor.setRejectedExecutionHandler(deviceStatusUpdater());
        executor.setThreadNamePrefix("SensorUpdateThread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.scheduleAtFixedRate(deviceStatusUpdater()::update, updatePeriodMillis);
        
        return executor;
    }

    @Bean
    public SensorUpdater dhtSensorTop() {
        DHTSensor.Type type = env.getProperty("gpioConfiguration.dhtSensorTop.type", DHTSensor.Type.class, DHTSensor.Type.AM2302);
        int pin = env.getRequiredProperty("gpioConfiguration.dhtSensorTop.pin", Integer.TYPE);
        DHTSensor sensor = new DHTSensor(type, pin);
        return new DHTSensorUpdater(sensor, temperatureSensor, humiditySensor);
    }

    @Bean
    public SensorUpdater dhtSensorBottom() {
        DHTSensor.Type type = env.getProperty("gpioConfiguration.dhtSensorBottom.type", DHTSensor.Type.class, DHTSensor.Type.AM2302);
        int pin = env.getRequiredProperty("gpioConfiguration.dhtSensorBottom.pin", Integer.TYPE);
        DHTSensor sensor = new DHTSensor(type, pin);
        return new DHTSensorUpdater(sensor, temperatureSensor, humiditySensor);
    }

    @Bean
    public SensorUpdater leftDoorSensor() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.leftDoorSensor.pin.name"));
        GpioPinDigitalInput digitalInputPin = gpioController.provisionDigitalInputPin(pin, "Door left", PinPullResistance.PULL_UP);
        return new DigitalPinStateUpdater(digitalInputPin, leftDoorSensor);
    }
    
    @Bean
    public SensorUpdater rightDoorSensor() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.rightDoorSensor.pin.name"));
        GpioPinDigitalInput digitalInputPin = gpioController.provisionDigitalInputPin(pin, "Door right", PinPullResistance.PULL_UP);
        return new DigitalPinStateUpdater(digitalInputPin, rightDoorSensor);
    }
    
    @Bean
    public GpioPinDigitalOutput rainSystem() {
        return gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(env.getRequiredProperty("gpio.relay_board.relay1")), "Relay One", PinState.LOW);
    }

}