package de.ck35.raspberry.sensors.temperature;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.util.NativeLibraryLoader;

/**
 * Reads temperature and humidity from a digital temperature and humidity sensor.
 * 
 * @author Christian Kaspari
 * @since 1.0.0
 */
public class DHTSensor {
    
    private static final Logger LOG = LoggerFactory.getLogger(DHTSensor.class);
    
    private static final String DHT_DRIVER_PATH = "/Adafruit_DHT_Driver_RaspberryPi_2.so";
    
    static {
        try {
            NativeLibraryLoader.loadLibraryFromClasspath(DHT_DRIVER_PATH);
        } catch(IOException e) {
            throw new UncheckedIOException("Could not load shared library for DHT sensor: '" + DHT_DRIVER_PATH + "'!", e);
        } catch (UnsatisfiedLinkError e) {
            LOG.warn("Shared library for DHT sensor could not be loaded.", e);
        }
    }
    
    /**
     * Supported sensor types.
     */
    public enum Type {
        
        DHT11(11),
        DHT22(22),
        AM2302(22);
        
        private final int sensorId;
        
        Type(int sensorId) {
            this.sensorId = sensorId;
        }
        
    }
    
    /**
     * Sensor read result.
     */
    public static class Result {
        
        private final float temperature;
        private final float humidity;
        
        public Result(float temperature, float humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
        }
        public float getTemperature() {
            return temperature;
        }
        public float getHumidity() {
            return humidity;
        }
    }
    
    private final Type type;
    private final int gpioPin;
    
    public DHTSensor(Type type, int gpioPin) {
        this.type = type;
        this.gpioPin = gpioPin;
    }

    public Optional<Result> read() {
        return Optional.ofNullable(readNative(type.sensorId, gpioPin)).map(this::mapResult);
    }
    
    private Result mapResult(float[] nativeResult) {
        return new Result(nativeResult[0], nativeResult[1]);
    }

    private native float[] readNative(int sensorId, int gpioPin);
    
}