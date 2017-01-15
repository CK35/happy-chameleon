package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sensor {

    private final Lock lock;
    private final SlidingValues slidingValues;
    
    public Sensor(int numberOfValues, double quantile) {
        this.slidingValues = new SlidingValues(numberOfValues, quantile);
        this.lock = new ReentrantLock();
    }
    
    public Optional<Double> getValue() {
        this.lock.lock();
        try {
            return slidingValues.get();
        } finally {
            this.lock.unlock();
        }
    }
    
    void pushValue(double value) {
        this.lock.lock();
        try {
            slidingValues.push(value);
        } finally {
            this.lock.unlock();
        }
    }
    
}