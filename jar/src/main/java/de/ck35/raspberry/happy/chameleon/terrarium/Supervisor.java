package de.ck35.raspberry.happy.chameleon.terrarium;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.ck35.raspberry.happy.chameleon.devices.BinarySensor;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.WorkerThread.TimedWaitStrategy;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.Terrarium;

public class Supervisor implements Closeable {

    static final Logger LOG = LoggerFactory.getLogger(Supervisor.class);
    
    private final WorkerThread workerThread;
    
    @Autowired Terrarium terrarium;
    
    @Autowired Sensor temperatureSensor;
    @Autowired Sensor humiditySensor;
    
    @Autowired BinarySensor leftDoorSensor;
    @Autowired BinarySensor rightDoorSensor;

    @Autowired Switch rainSystemTimerSwitch;
    @Autowired Switch rainSystemSwitch;
    
    @Autowired Switch heatLampSwitch1;
    
    @Autowired Switch lightBulpSwitch1;

    public Supervisor() {
        workerThread = new WorkerThread(new TimedWaitStrategy(10, TimeUnit.SECONDS), "SupervisorWorker", this::doUpdate);
    }
    
    private void doUpdate() {
        
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
    
    public void update() {
        workerThread.update();
    }
    
    @Override
    public void close() {
        workerThread.close();
    }
}