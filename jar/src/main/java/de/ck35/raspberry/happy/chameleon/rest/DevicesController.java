package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ck35.raspberry.happy.chameleon.devices.Devices;

@RestController
@RequestMapping("/devices")
public class DevicesController {

    private final Devices devices;
    
    public DevicesController(Devices devices) {
        this.devices = devices;
    }
    
    @RequestMapping("/status")
    public Devices.Status status() {
        return devices.getStatus();
    }
    
}