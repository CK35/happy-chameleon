package de.ck35.raspberry.happy.chameleon.terrarium;

import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.RainProgramm.RainProgramms;

public class RainSystemTimer {

    private final Switch rainSystemTimerSwitch;
    private final RainProgramms rainProgramms;
    
    private final Deque<Task> tasks;

    public RainSystemTimer(Switch rainSystemTimerSwitch, RainProgramms rainProgramms) {
        this.rainSystemTimerSwitch = rainSystemTimerSwitch;
        this.rainProgramms = rainProgramms;
        this.tasks = new ArrayDeque<>();
    }

    public void update() {
    }

    
    
    public static class Task {
        
        private final ZonedDateTime timestamp;
        private final Runnable runnable;

        public Task(ZonedDateTime timestamp, Runnable runnable) {
            this.timestamp = timestamp;
            this.runnable = runnable;
        }
        
        public void invoke() {
            runnable.run();
        }
        
    }
}