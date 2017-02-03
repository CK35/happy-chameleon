package de.ck35.raspberry.happy.chameleon.devices;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RetentionPolicyTest {
    
    @Mock Clock clock;
    private Duration retentionDuration;

    public RetentionPolicyTest() {
        this.retentionDuration = Duration.parse("PT2M"); //two minutes
    }

    public RetentionPolicy sensorValueRetentionPolicy() {
        return new RetentionPolicy(clock, retentionDuration);
    }
    
    @Test
    public void testIsValueValidInitial() {
        when(clock.instant()).thenReturn(Instant.parse("2016-01-22T10:00:00.00Z"));
        assertFalse(sensorValueRetentionPolicy().isValid());
    }
    
    @Test
    public void testIsValueValidTrue() {
        when(clock.instant()).thenReturn(Instant.parse("2016-01-22T10:00:00.00Z"));
        RetentionPolicy policy = sensorValueRetentionPolicy();
        policy.update();
        assertTrue(policy.isValid());
    }
    
    @Test
    public void testIsValueValidFalse() {
        when(clock.instant()).thenReturn(Instant.parse("2016-01-22T10:00:00.00Z"));
        RetentionPolicy policy = sensorValueRetentionPolicy();
        policy.update();
        when(clock.instant()).thenReturn(Instant.parse("2016-01-22T10:02:00.00Z"));
        assertFalse(policy.isValid());
    }

}