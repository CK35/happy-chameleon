package de.ck35.raspberry.happy.chameleon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.ck35.raspberry.happy.chameleon.devices.Devices;
import de.ck35.raspberry.happy.chameleon.rest.DevicesController;

@Configuration
@EnableAutoConfiguration
@Import({ DeviceConfiguration.class, GpioConfiguration.class })
public class RootConfiguration {

	@Autowired Devices devices;

	@Bean
	public DevicesController devicesController() {
		return new DevicesController(devices);
	}
	
}