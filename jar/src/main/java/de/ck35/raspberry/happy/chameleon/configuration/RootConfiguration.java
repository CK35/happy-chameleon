package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

import de.ck35.raspberry.happy.chameleon.rest.DHTSensorController;
import de.ck35.raspberry.happy.chameleon.rest.DoorSwitchController;
import de.ck35.raspberry.happy.chameleon.rest.RelayController;
import de.ck35.raspberry.sensors.temperature.DHTSensor;

@Configuration
@EnableAutoConfiguration
@Import({ GpioControllerConfiguration.class })
public class RootConfiguration {

	@Autowired
	DHTSensor dhtSensor;

	@Autowired
	GpioPinDigitalOutput myFirstRelay;

	@Autowired
	GpioPinDigitalInput myDoorSwitch;
    
	@Bean
	public DHTSensorController helloWorldController() {
		return new DHTSensorController(dhtSensor);
	}
	
	@Bean
	public RelayController helloRelay() {
		return new RelayController(myFirstRelay);
	}

	@Bean
	public DoorSwitchController helloDoor() {
		return new DoorSwitchController(myDoorSwitch);
	}
    
}