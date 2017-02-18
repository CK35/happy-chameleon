package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

@RestController
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
    
    @RequestMapping("/toggle")
    public String toggle() {
        PinState ps = myFirstSwitch.getState();
        String fromState = ps.getName();

        myFirstSwitch.toggle();

        ps = myFirstSwitch.getState();
        String toState = ps.getName();

        return "Relay toggled from '" + fromState + "' to '" + toState + "'.";
    }

}
