package de.ck35.raspberry.happy.chameleon.terrarium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.Terrarium;

public class Supervisor {

    static final Logger LOG = LoggerFactory.getLogger(Supervisor.class);
    
    @Autowired Terrarium terrarium;
    
    @Autowired Sensor temperatureSensor;
    @Autowired Sensor humiditySensor;
    
    @Autowired BinarySensor leftDoorSensor;
    @Autowired BinarySensor rightDoorSensor;

    @Autowired Switch rainSystemTimerSwitch;
    @Autowired Switch rainSystemSwitch;
    
    @Autowired Switch heatLampSwitch1;
    
    @Autowired Switch lightBulpSwitch1;
    
    public void update() {
    	
    	if(rainSystemTimerSwitch.isOff()) {
    		rainSystemSwitch.setOff();
    	}
    	
        if(isAnyDoorOpen()) {
            rainSystemSwitch.setOff();
            heatLampSwitch1.setOff();
            if(terrarium.isNight()) {                
                lightBulpSwitch1.setOn();
            }
            return;
        }

        Double temperature = temperatureSensor.getValue().orElse(null);
        Double humidity = humiditySensor.getValue().orElse(null);
        if(temperature == null || humidity == null) {
            heatLampSwitch1.setOff();
            return;
        }

        if(temperature.doubleValue() > terrarium.getCurrentMaxTemperature()) {
            heatLampSwitch1.setOff();
            
        } else if(humidity.doubleValue() > terrarium.getCurrentMaxHumidity()) {
        	heatLampSwitch1.setOn();
        }
        
        if(rainSystemTimerSwitch.isOn()) {
        	rainSystemSwitch.setOn();
        }
        
    }
    
    public boolean isAnyDoorOpen() {
        return leftDoorSensor.getValue().orElse(false).booleanValue() || rightDoorSensor.getValue().orElse(false).booleanValue();
    }
    
}