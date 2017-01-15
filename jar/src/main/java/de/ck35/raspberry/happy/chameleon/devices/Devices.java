package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;

public class Devices {

    private final Sensor temperatureSensor;
    private final Sensor humiditySensor;

    private final BinarySensor leftDoorSensor;
    private final BinarySensor rightDoorSensor;
    
    private final Status status;

    public Devices(Sensor temperatureSensor, Sensor humiditySensor, BinarySensor leftDoorSensor, BinarySensor rightDoorSensor) {
        this.temperatureSensor = temperatureSensor;
        this.humiditySensor = humiditySensor;
        this.leftDoorSensor = leftDoorSensor;
        this.rightDoorSensor = rightDoorSensor;
        this.status = new Status();
    }

    public Sensor getTemperatureSensor() {
        return temperatureSensor;
    }
    public Sensor getHumiditySensor() {
        return humiditySensor;
    }
    public BinarySensor getLeftDoorSensor() {
        return leftDoorSensor;
    }
    public BinarySensor getRightDoorSensor() {
        return rightDoorSensor;
    }
    
    public Status getStatus() {
        return status;
    }
    
    
    public class Status {
        
        public Optional<Double> getTemperature() {
            return temperatureSensor.getValue();
        }
        public Optional<Double> getHumidity() {
            return humiditySensor.getValue();
        }
        public Optional<Boolean> getLeftDoorOpen() {
            return leftDoorSensor.getValue();
        }
        public Optional<Boolean> getRightDoorOpen() {
            return rightDoorSensor.getValue();
        }
        
    }
}