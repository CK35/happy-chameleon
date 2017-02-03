package de.ck35.raspberry.happy.chameleon.devices;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Invalidation helper which tracks a certain retention duration.
 * 
 * @author Christian Kaspari
 * @since 1.0.0
 */
public class RetentionPolicy {

    private final Clock clock;
    private final Duration retentionDuration;
    
    private Instant validUntil;
    
    public RetentionPolicy(Clock clock, Duration retentionDuration) {
        this.clock = clock;
        this.retentionDuration = retentionDuration;
    }
    
    public boolean isValid() {
        return validUntil != null && clock.instant().isBefore(validUntil);
    }
    
    public void update() {
        validUntil = clock.instant().plus(retentionDuration);
    }
}