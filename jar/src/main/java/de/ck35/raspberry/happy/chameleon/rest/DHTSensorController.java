package de.ck35.raspberry.happy.chameleon.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ck35.raspberry.sensors.temperature.DHTSensor;

@RestController
@RequestMapping("/dht")
public class DHTSensorController {

    private final DHTSensor dhtSensor;
    
    public DHTSensorController(DHTSensor dhtSensor) {
        this.dhtSensor = dhtSensor;
    }
    
    @RequestMapping("/read")
    public DHTSensor.Result read() {
        return dhtSensor.read().orElse(null);
    }
    
    
}