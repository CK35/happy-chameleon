package de.ck35.raspberry.happy.chameleon.terrarium;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDispatcher implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);
    
    private final List<Runnable> listeners;

    public EventDispatcher() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        listeners.forEach(this::invoke);
    }
    
    private void invoke(Runnable runnable) {
        try {
            runnable.run();
        } catch(RuntimeException e) {
            LOG.error("Error while invoking listener: '" + runnable + "'!", e);
        }
    }

    public void addListener(Runnable runnable) {
        this.listeners.add(runnable);
    }
}