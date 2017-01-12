package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

@RequestMapping("/relay")
public class RelayController {

	private final GpioPinDigitalOutput myFirstSwitch;

    public RelayController(GpioPinDigitalOutput myFirstSwitch) {
		this.myFirstSwitch = myFirstSwitch;
	}
	
	@RequestMapping("/on")
	public String on() {
	    myFirstSwitch.high();
		return "Relay on";
	}
	
	@RequestMapping("/off")
	public String off() {
	    myFirstSwitch.low();
		return "Relay off";
	}
	
}
