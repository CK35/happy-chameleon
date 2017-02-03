package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BinarySensor {

    private final Lock lock;
    private final RetentionPolicy retentionPolicy;
    private final Runnable valuesChangedListener;
    private final SlidingValues slidingValues;
    
    private boolean overwritten;
    private boolean overwrittenValue;
    
    public BinarySensor(RetentionPolicy retentionPolicy, Runnable valuesChangedListener, SlidingValues slidingValues) {
        this.retentionPolicy = retentionPolicy;
        this.valuesChangedListener = valuesChangedListener;
        this.slidingValues = slidingValues;
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
    }
    
    public void disableOverwrite() {
        this.lock.lock();
        try {
            overwritten = false;
            overwrittenValue = false;
        } finally {
            this.lock.unlock();
        }
    }
    
    public Optional<Boolean> getValue() {
        this.lock.lock();
        try {
            if(overwritten) {
                return Optional.of(overwrittenValue);
            }
            if(retentionPolicy.isValid()) {
                return Optional.empty();
            }
            return slidingValues.get().map(value -> value == 1d ? true : false);
        } finally {
            this.lock.unlock();
        }
    }
    
    void pushValue(boolean value) {
        Boolean oldValue;
        Boolean newValue;
        this.lock.lock();
        try {
            oldValue = getValue().orElse(null);
            slidingValues.push(value ? 1d : 0d);
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