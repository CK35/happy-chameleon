package de.ck35.raspberry.happy.chameleon.terrarium;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerThread.class);

    private final Thread thread;
    private final WaitStrategy waitStrategy;
    private final AtomicBoolean invokeWorker;

    private volatile Runnable worker;

    public WorkerThread(WaitStrategy waitStrategy, String workerThreadName, Runnable worker) {
        this.waitStrategy = waitStrategy;
        this.thread = new Thread(this::doLoop);
        this.thread.setName(workerThreadName);
        this.thread.start();
        this.worker = worker;

        this.invokeWorker = new AtomicBoolean();
    }

    public void update() {
        invokeWorker.set(true);
        waitStrategy.stopWaiting();
    }

    private void doLoop() {
        while (waitStrategy.waitForNextWorkerRun()) {
            while (invokeWorker.compareAndSet(true, false)) {
                invokeWorker();
            }
        }
    }

    private void invokeWorker() {
        try {
            getWorker().ifPresent(Runnable::run);
        } catch (RuntimeException e) {
            LOG.error("Exception occured while invoking worker!", e);
        }
    }

    @Override
    public void close() {
        waitStrategy.close();
    }

    public Optional<Runnable> getWorker() {
        return Optional.ofNullable(worker);
    }
    public void setWorker(Runnable supervisor) {
        this.worker = supervisor;
    }

    public static interface WaitStrategy extends Closeable {

        boolean waitForNextWorkerRun();

        void stopWaiting();

        void close();

    }

    public static class TimedWaitStrategy implements WaitStrategy {

        private boolean closed;
        private Thread waitingThread;
        private long waitTimeMillis;

        public TimedWaitStrategy() {
            this(0);
        }
        public TimedWaitStrategy(long waitTime, TimeUnit waitTimeUnit) {
            this(TimeUnit.MILLISECONDS.convert(waitTime, waitTimeUnit));
        }
        public TimedWaitStrategy(long waitTimeMillis) {
            this.waitTimeMillis = waitTimeMillis;
        }

        @Override
        public boolean waitForNextWorkerRun() {
            synchronized (this) {
                if (waitingThread != null) {
                    throw new IllegalStateException("Currently another worker thread: '" + waitingThread.getName() + "' is waiting on this strategy!");
                }
                if (closed) {
                    return false;
                }
                waitingThread = Thread.currentThread();
                try {
                    this.wait(waitTimeMillis);
                    return !closed;
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Interrupted while waiting!", e);
                } finally {
                    waitingThread = null;
                }
            }
        }

        @Override
        public void stopWaiting() {
            synchronized (this) {
                this.notifyAll();
            }
        }

        @Override
        public void close() {
            Thread currentWaitingThread = null;
            synchronized (this) {
                closed = true;
                currentWaitingThread = waitingThread;
            }
            stopWaiting();
            if (currentWaitingThread == null) {
                return;
            }
            try {
                currentWaitingThread.join();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrupted while joining worker thread: '" + currentWaitingThread.getName() + "' on close.");
            }
        }
        
        public void setWaitTimeMillis(long waitTimeMillis) {
            synchronized (this) {                
                this.waitTimeMillis = waitTimeMillis;
            }
            stopWaiting();
        }
        public long getWaitTimeMillis() {
            synchronized (this) {                
                return waitTimeMillis;
            }
        }
    }
}