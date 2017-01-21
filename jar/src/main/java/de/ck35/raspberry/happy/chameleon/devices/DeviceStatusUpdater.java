package de.ck35.raspberry.happy.chameleon.devices;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import com.pi4j.io.gpio.GpioPinDigitalInput;

import de.ck35.raspberry.sensors.temperature.DHTSensor;

public class DeviceStatusUpdater implements ErrorHandler, RejectedExecutionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceStatusUpdater.class);
    
    private final List<SensorUpdater> tasks;

    public DeviceStatusUpdater(List<SensorUpdater> tasks) {
        this.tasks = tasks;
    }
    
    public void update() {
        tasks.forEach(task -> {
            try {                
                task.update();
            } catch(RuntimeException e) {
                LOG.warn("Error while updating sensor values with: '" + task + "'!", e);
            }
        });
    }
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LOG.warn("Scheduling sensor update task failed!");
    }
    
    @Override
    public void handleError(Throwable t) {
        LOG.warn("Error while updating sensor values!", t);
    }
    
    public interface SensorUpdater {
        
        void update();
        
    }
    
    public static class DHTSensorUpdater implements SensorUpdater {
        
        private final DHTSensor sensor;
        private final Sensor temperatureSensor;
        private final Sensor humiditySensor;
        
        public DHTSensorUpdater(DHTSensor sensor, Sensor temperatureSensor, Sensor humiditySensor) {
            this.sensor = sensor;
            this.temperatureSensor = temperatureSensor;
            this.humiditySensor = humiditySensor;
        }
        @Override
        public void update() {
            sensor.read().ifPresent(value -> {
                temperatureSensor.pushValue(value.getTemperature());
                humiditySensor.pushValue(value.getHumidity());
            });
        }
    }
    
    public static class DigitalPinStateUpdater implements SensorUpdater {
        
        private final GpioPinDigitalInput pin;
        private final BinarySensor sensor;
        
        public DigitalPinStateUpdater(GpioPinDigitalInput pin, BinarySensor sensor) {
            this.pin = pin;
            this.sensor = sensor;
        }
        @Override
        public void update() {
            sensor.pushValue(pin.getState().isHigh());
        }
    }
}