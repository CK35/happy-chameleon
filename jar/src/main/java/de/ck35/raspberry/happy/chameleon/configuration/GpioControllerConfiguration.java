package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import de.ck35.raspberry.sensors.temperature.DHTSensor;
import de.ck35.raspberry.sensors.temperature.DHTSensor.Type;

@Configuration
public class GpioControllerConfiguration {

    @Autowired Environment env;
    
    @Bean
    public GpioController gpioController() {
        return GpioFactory.getInstance();
    }
    
    @Bean
    public DHTSensor dhtSensor() {
        return new DHTSensor(Type.AM2302, 17);
    }
    
    @Bean
    public GpioPinDigitalOutput myFirstSwitch() {
        return gpioController().provisionDigitalOutputPin(RaspiPin.getPinByName(env.getRequiredProperty("my_first_switch.pin")), PinState.HIGH);
    }
    
}