package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BinarySensor {

    private final Lock lock;
    private final SlidingValues slidingValues;
    
    public BinarySensor(int numberOfValues, double quantile) {
        this.slidingValues = new SlidingValues(numberOfValues, quantile);
        this.lock = new ReentrantLock();
    }
    
    public Optional<Boolean> getValue() {
        this.lock.lock();
        try {
            return slidingValues.get().map(value -> value == 1d ? true : false);
        } finally {
            this.lock.unlock();
        }
    }
    
    void pushValue(boolean value) {
        this.lock.lock();
        try {
            slidingValues.push(value ? 1d : 0d);
        } finally {
            this.lock.unlock();
        }
    }

}