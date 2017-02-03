package de.ck35.raspberry.happy.chameleon.terrarium;

import java.io.Closeable;
import java.util.Optional;

public class SupervisorWorker implements Closeable {


    private final Thread thread;
    private final long maximalUpdateIntervalMillis;

    private boolean doUpdate;

    private volatile Supervisor supervisor;

    public SupervisorWorker(long maximalUpdateIntervalMillis) {
        this.maximalUpdateIntervalMillis = maximalUpdateIntervalMillis;
        this.thread = new Thread(this::doLoop);
        this.thread.setName("DeviceSupervisorThread");
        this.thread.start();
    }

    public void update() {
        synchronized (this) {
            doUpdate = true;
            notifyAll();
        }
    }

    private void doLoop() {
        while (!Thread.interrupted()) {
            synchronized (this) {
                while(!doUpdate) {
                    if(Thread.interrupted()) {
                        return;
                    }
                    try {
                        this.wait(maximalUpdateIntervalMillis);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            try {
                getSupervisor().ifPresent(Supervisor::update);
            } catch (RuntimeException e) {
                Supervisor.LOG.error("Update call failed!", e);
            }
        }
    }

    @Override
    public void close() {
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not wait for supervisor thread to join.", e);
        }
    }
    
    public Optional<Supervisor> getSupervisor() {
        return Optional.ofNullable(supervisor);
    }
    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }
}