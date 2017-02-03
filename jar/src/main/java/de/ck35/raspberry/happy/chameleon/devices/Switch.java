package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class Switch {

    private final Runnable valuesChangedListener;

    private final ReadWriteLock lock;
    private boolean status;
    public Consumer<Boolean> device;

    
    public Switch(Runnable valuesChangedListener) {
        this.valuesChangedListener = valuesChangedListener;
        this.lock = new ReentrantReadWriteLock();
        this.device = x -> {};
    }

    public void connect(Consumer<Boolean> device) {
        this.lock.writeLock().lock();
        try {
            this.device = Objects.requireNonNull(device);
            device.accept(status);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public void toggle() {
        this.lock.writeLock().lock();
        try {
            status = status ? false : true;
            device.accept(status);
        } finally {
            this.lock.writeLock().unlock();
        }
        valuesChangedListener.run();
    }
    
    public void setOn() {
        boolean changed = false;
        this.lock.writeLock().lock();
        try {
            if(!status) {
                changed = true;
                status = true;
            }
            device.accept(status);
        } finally {
            this.lock.writeLock().unlock();
        }
        if(changed) {
            valuesChangedListener.run();
        }
    }
    
    public boolean isOn() {
        this.lock.readLock().lock();
        try {
            return status == true;
        } finally {
            this.lock.readLock().unlock();
        }
    }
    
    public void setOff() {
        boolean changed = false;
        this.lock.writeLock().lock();
        try {
            if(status) {
                changed = true;
                status = false;
            }
            device.accept(status);
        } finally {
            this.lock.writeLock().unlock();
        }
        if(changed) {
            valuesChangedListener.run();
        }
    }
    
    public boolean isOff() {
        this.lock.readLock().lock();
        try {
            return status == false;
        } finally {
            this.lock.readLock().unlock();
        }
    }
    
}