package de.ck35.raspberry.happy.chameleon.rest;

import java.time.Clock;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.happy.chameleon.devices.Switch;

@RestController
@RequestMapping("/devices")
public class DevicesController {

    @Autowired Clock clock;
    
    @Autowired Sensor temperatureSensor;
    @Autowired Sensor humiditySensor;
    
    @Autowired BinarySensor leftDoorSensor;
    @Autowired BinarySensor rightDoorSensor;

    @Autowired Switch rainSystemSwitch;
    @Autowired Switch heatLampSwitch1;
    @Autowired Switch lightBulpSwitch1;
    
    @ResponseBody
    @RequestMapping("/status")
    public Status status() {
        Status status = new Status();
        status.time = clock.instant();
        status.temperature = temperatureSensor.getValue().orElse(null);
        status.humidity = humiditySensor.getValue().orElse(null);
        status.leftDoorOpen = leftDoorSensor.getValue().orElse(null);
        status.rightDoorOpen = rightDoorSensor.getValue().orElse(null);
        status.rainSystemOn = rainSystemSwitch.isOn();
        status.heatLamp1On = heatLampSwitch1.isOn();
        status.lightBulp1On = lightBulpSwitch1.isOn();
        return status;
    }
    
    public static class Status {
        
        public Instant time;
        
        public Double temperature;
        public Double humidity;
        
        public Boolean leftDoorOpen;
        public Boolean rightDoorOpen;
        
        public Boolean rainSystemOn;
        
        public Boolean heatLamp1On;
        public Boolean lightBulp1On;
        
    }
}