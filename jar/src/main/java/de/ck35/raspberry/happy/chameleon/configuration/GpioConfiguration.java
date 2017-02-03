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
import de.ck35.raspberry.happy.chameleon.devices.Switch;
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

    @Autowired Switch rainSystemSwitch;
    @Autowired Switch heatLampSwitch1;
    @Autowired Switch lightBulpSwitch1;
    
    @Autowired List<SensorUpdater> sensorUpdateTasks;

    @Bean
    public DeviceStatusUpdater deviceStatusUpdater() {
        long updatePeriodMillis = env.getProperty("gpioConfiguration.deviceStatusUpdateTaskScheduler.updatePeriodMillis", Long.TYPE, 500L);
        DeviceStatusUpdater updater = new DeviceStatusUpdater(sensorUpdateTasks);
        deviceStatusUpdateTaskScheduler().scheduleAtFixedRate(updater::update, updatePeriodMillis);
        return updater;
    }

    @Bean
    public ThreadPoolTaskScheduler deviceStatusUpdateTaskScheduler() {
        
        DeviceStatusUpdater.Errors errorHandler = new DeviceStatusUpdater.Errors();
        
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setErrorHandler(errorHandler);
        executor.setRejectedExecutionHandler(errorHandler);
        executor.setThreadNamePrefix("SensorUpdateThread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        return executor;
    }

    @Bean
    public SensorUpdater dhtSensorTopUpdater() {
        DHTSensor.Type type = env.getProperty("gpioConfiguration.dhtSensorTop.type", DHTSensor.Type.class, DHTSensor.Type.AM2302);
        int pin = env.getRequiredProperty("gpioConfiguration.dhtSensorTop.pin", Integer.TYPE);
        DHTSensor sensor = new DHTSensor(type, pin);
        return new DHTSensorUpdater(sensor, temperatureSensor, humiditySensor);
    }

    @Bean
    public SensorUpdater dhtSensorBottomUpdater() {
        DHTSensor.Type type = env.getProperty("gpioConfiguration.dhtSensorBottom.type", DHTSensor.Type.class, DHTSensor.Type.AM2302);
        int pin = env.getRequiredProperty("gpioConfiguration.dhtSensorBottom.pin", Integer.TYPE);
        DHTSensor sensor = new DHTSensor(type, pin);
        return new DHTSensorUpdater(sensor, temperatureSensor, humiditySensor);
    }

    @Bean
    public SensorUpdater leftDoorSensorUpdater() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.leftDoorSensor.pin.name"));
        PinPullResistance pinPullResistance = env.getRequiredProperty("gpioConfiguration.leftDoorSensor.pinPullResistance", PinPullResistance.class);
        GpioPinDigitalInput digitalInputPin = gpioController.provisionDigitalInputPin(pin, "door left", pinPullResistance);
        return new DigitalPinStateUpdater(digitalInputPin, leftDoorSensor);
    }
    
    @Bean
    public SensorUpdater rightDoorSensorUpdater() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.rightDoorSensor.pin.name"));
        PinPullResistance pinPullResistance = env.getRequiredProperty("gpioConfiguration.rightDoorSensor.pinPullResistance", PinPullResistance.class);
        GpioPinDigitalInput digitalInputPin = gpioController.provisionDigitalInputPin(pin, "door right", pinPullResistance);
        return new DigitalPinStateUpdater(digitalInputPin, rightDoorSensor);
    }
    
    @Bean
    public GpioPinDigitalOutput rainSystem() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.rainSystem.pin.name"));
        PinState defaultState = env.getRequiredProperty("gpioConfiguration.rainSystem.pin.defaultState", PinState.class);
        GpioPinDigitalOutput digitalOutput = gpioController.provisionDigitalOutputPin(pin, "rain system", defaultState);
        rainSystemSwitch.connect(digitalOutput::setState);
        return digitalOutput;
    }
    
    @Bean
    public GpioPinDigitalOutput heatLamp1() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.heatLamp1.pin.name"));
        PinState defaultState = env.getRequiredProperty("gpioConfiguration.heatLamp1.pin.defaultState", PinState.class);
        GpioPinDigitalOutput digitalOutput = gpioController.provisionDigitalOutputPin(pin, "heat lamp 1", defaultState);
        heatLampSwitch1.connect(digitalOutput::setState);
        return digitalOutput;
    }
    
    @Bean
    public GpioPinDigitalOutput lightBulp1() {
        Pin pin = RaspiPin.getPinByName(env.getRequiredProperty("gpioConfiguration.lightBulp1.pin.name"));
        PinState defaultState = env.getRequiredProperty("gpioConfiguration.lightBulp1.pin.defaultState", PinState.class);
        GpioPinDigitalOutput digitalOutput = gpioController.provisionDigitalOutputPin(pin, "light bulp 1", defaultState);
        lightBulpSwitch1.connect(digitalOutput::setState);
        return digitalOutput;
    }

}