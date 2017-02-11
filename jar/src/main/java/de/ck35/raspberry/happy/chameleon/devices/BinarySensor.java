package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BinarySensor {

    private final Lock lock;
    private final RetentionPolicy retentionPolicy;
    private final int minNumberOfSameValues;
    private boolean lastValue;
    private Boolean lastValidValue;
    private int currentNumberOfSameValues;
    private final Runnable valuesChangedListener;
    
    private boolean overwritten;
    private boolean overwrittenValue;
    
    public BinarySensor(RetentionPolicy retentionPolicy, int minNumberOfSameValues, Runnable valuesChangedListener) {
        this.retentionPolicy = retentionPolicy;
        this.minNumberOfSameValues = minNumberOfSameValues;
        this.valuesChangedListener = valuesChangedListener;
        this.lock = new ReentrantLock();
    }
    
    public void enableOverwirte(boolean value) {
        this.lock.lock();
        try {
            overwritten = true;
            overwrittenValue = value;
        } finally {
            this.lock.unlock();
        }
        valuesChangedListener.run();
    }
    
    public void disableOverwrite() {
        this.lock.lock();
        try {
            overwritten = false;
            overwrittenValue = false;
        } finally {
            this.lock.unlock();
        }
        valuesChangedListener.run();
    }
    
    public Optional<Boolean> getValue() {
        this.lock.lock();
        try {
            if(overwritten) {
                return Optional.of(overwrittenValue);
            }
            if(!retentionPolicy.isValid()) {
                return Optional.empty();
            }
            if(currentNumberOfSameValues >= minNumberOfSameValues) {
            	lastValidValue = lastValue;
            }
            return Optional.ofNullable(lastValidValue);
        } finally {
            this.lock.unlock();
        }
    }
    
    public void pushValue(boolean value) {
        Boolean oldValue;
        Boolean newValue;
        this.lock.lock();
        try {
            oldValue = getValue().orElse(null);
            if(lastValue == value) {
            	currentNumberOfSameValues++;
            } else {
            	lastValue = value;
            	currentNumberOfSameValues = 1;
            }
            retentionPolicy.update();
            newValue = getValue().orElse(null);
        } finally {
            this.lock.unlock();
        }
        if(newValue == null && oldValue != null) {
            valuesChangedListener.run();
        }
        if(newValue != null && !newValue.equals(oldValue)) {
            valuesChangedListener.run();
        }
    }

}