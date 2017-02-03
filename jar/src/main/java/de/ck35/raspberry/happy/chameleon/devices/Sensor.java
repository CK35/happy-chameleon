package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sensor {

    private final Lock lock;

    private final RetentionPolicy retentionPolicy;
    private final Runnable valuesChangedListener;
    private final SlidingValues slidingValues;

    private final double delta;

    public Sensor(RetentionPolicy retentionPolicy, Runnable valuesChangedListener, SlidingValues slidingValues, double delta) {
        this.lock = new ReentrantLock();
        this.retentionPolicy = retentionPolicy;
        this.valuesChangedListener = valuesChangedListener;
        this.slidingValues = slidingValues;
        this.delta = delta;
    }

    public Optional<Double> getValue() {
        this.lock.lock();
        try {
            if (!retentionPolicy.isValid()) {
                return Optional.empty();
            }
            return slidingValues.get();
        } finally {
            this.lock.unlock();
        }
    }

    void pushValue(double value) {
        Double oldValue;
        Double newValue;
        this.lock.lock();
        try {
            oldValue = getValue().orElse(null);
            slidingValues.push(value);
            retentionPolicy.update();
            newValue = getValue().orElse(null);
        } finally {
            this.lock.unlock();
        }
        if (oldValue == null && newValue == null) {
            return;
        }
        if (oldValue == null && newValue != null) {
            valuesChangedListener.run();
        }
        double difference = oldValue.doubleValue() - newValue.doubleValue();
        if ((difference < 0 && (difference * -1) >= delta) || (difference >= delta)) {
            valuesChangedListener.run();
        }
    }

}