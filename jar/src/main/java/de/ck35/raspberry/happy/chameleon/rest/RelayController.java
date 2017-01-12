package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@RestController
@RequestMapping("/relay")
public class RelayController {

	private static GpioController gpio;
	private static GpioPinDigitalOutput gpioDigitalOut;
	
	public RelayController() {
		gpio = GpioFactory.getInstance();
	}
	
	@RequestMapping("/on")
	public String on() {		
		gpioDigitalOut = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "RelayOn", PinState.HIGH);
		gpioDigitalOut.high();
		
		return "Relay on";
	}
	
	@RequestMapping("/off")
	public String off() {
		gpioDigitalOut = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "RelayOff", PinState.HIGH);
		gpioDigitalOut.low();
		
		return "Relay off";
	}
	
}
