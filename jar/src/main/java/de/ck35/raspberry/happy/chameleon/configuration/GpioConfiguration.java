package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.sensors.temperature.DHTSensor;
import de.ck35.raspberry.sensors.temperature.DHTSensor.Type;

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

    @Bean
    public DHTSensor dhtSensorTop() {
        return new DHTSensor(Type.AM2302, 17);
    }
    
    @Bean
    public DHTSensor dhtSensorBottom() {
        return new DHTSensor(Type.AM2302, 17);
    }

    @Bean
    public GpioPinDigitalOutput myFirstSwitch() {
        return gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(env.getRequiredProperty("gpio.relay_board.relay1")), "Relay One", PinState.LOW);
    }

    @Bean
    public GpioPinDigitalInput myDoorSwitch() {
        return gpioController.provisionDigitalInputPin(RaspiPin.getPinByName(env.getRequiredProperty("gpio.switch.left")),
                                                       "Button Left",
                                                       PinPullResistance.PULL_UP);
    }

}