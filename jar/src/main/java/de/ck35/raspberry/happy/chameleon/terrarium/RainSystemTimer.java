package de.ck35.raspberry.happy.chameleon.terrarium;

import java.io.Closeable;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.WorkerThread.TimedWaitStrategy;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.Interval;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.RainProgramm.RainProgramms;

public class RainSystemTimer implements Closeable {

    private final Switch rainSystemTimerSwitch;
    private final RainProgramms rainProgramms;
    private final Clock clock;
    private final TimedWaitStrategy waitStrategy;
    private final WorkerThread workerThread;

    public RainSystemTimer(Switch rainSystemTimerSwitch, RainProgramms rainProgramms, Clock clock) {
        this.rainSystemTimerSwitch = rainSystemTimerSwitch;
        this.rainProgramms = rainProgramms;
        this.clock = clock;
        this.waitStrategy = new TimedWaitStrategy();
        this.workerThread = new WorkerThread(waitStrategy, "RainSystemTimerWoker", this::doUpdate);
    }
    
    public void update() {
        workerThread.update();
    }
    
    private void doUpdate() {
        ZonedDateTime now = ZonedDateTime.now(clock);
        List<Interval> programms = rainProgramms.findProgrammsForDay(now.toLocalDate(), now.getZone());
        if(programms.isEmpty()) {
            rainSystemTimerSwitch.setOff();
        }
        for (Interval interval : programms) {
            if(interval.isBefore(now)) {
                continue;
            }
            if (interval.contains(now)) {
                rainSystemTimerSwitch.setOn();
                waitStrategy.setWaitTimeMillis(Duration.between(now, interval.getEnd()).toMillis());
                return;
            } else {
                rainSystemTimerSwitch.setOff();
                waitStrategy.setWaitTimeMillis(Duration.between(now, interval.getStart()).toMillis());
                return;
            }
            
        }
    }
    
    @Override
    public void close() {
        workerThread.close();
    }
}