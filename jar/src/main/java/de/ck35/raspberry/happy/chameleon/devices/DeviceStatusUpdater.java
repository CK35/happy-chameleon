package de.ck35.raspberry.happy.chameleon.devices;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import de.ck35.raspberry.sensors.temperature.DHTSensor;

public class DeviceStatusUpdater implements Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceStatusUpdater.class);
    
    private final List<Runnable> tasks;

    private volatile boolean closed;
    
    public DeviceStatusUpdater(List<Runnable> tasks) {
        this.tasks = tasks;
    }
    
    @Override
    public void run() {
        while(!closed) {
            update();
        }
    }
    
    public void update() {
        tasks.forEach(task -> {
            try {                
                task.run();
            } catch(RuntimeException e) {
                LOG.warn("Error while updating values with: '" + task + "'!", e);
            }
        });
    }
    
    @Override
    public void close() {
        closed = true;
    }
    
    public static class DHTSensorUpdater {
        
        private final DHTSensor sensor;
        private final Sensor temperatureSensor;
        private final Sensor humiditySensor;
        
        public DHTSensorUpdater(DHTSensor sensor, Sensor temperatureSensor, Sensor humiditySensor) {
            this.sensor = sensor;
            this.temperatureSensor = temperatureSensor;
            this.humiditySensor = humiditySensor;
        }
        public void update() {
            sensor.read().ifPresent(value -> {
                temperatureSensor.pushValue(value.getTemperature());
                humiditySensor.pushValue(value.getHumidity());
            });
        }
    }
    
    public static class PinStateUpdater {
        
        private final GpioPinDigitalInput pin;
        private final BinarySensor sensor;
        
        public PinStateUpdater(GpioPinDigitalInput pin, BinarySensor sensor) {
            this.pin = pin;
            this.sensor = sensor;
        }
        public void update() {
            sensor.pushValue(pin.getState().isHigh());
        }
    }
}