package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;

@RestController
@RequestMapping("/door")
public class DoorSwitchController {

    private final GpioPinDigitalInput myDoorSwitch;

    public DoorSwitchController(GpioPinDigitalInput myDoorSwitch) {
        this.myDoorSwitch = myDoorSwitch;
    }

    @RequestMapping("/status")
    public String status() {
        PinState ps = myDoorSwitch.getState();

        if (myDoorSwitch.isHigh()) {
            // Door is open
        }

        return "Door status is: " + ps.getName();
    }

}
