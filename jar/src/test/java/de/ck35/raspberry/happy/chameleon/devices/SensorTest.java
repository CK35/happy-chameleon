package de.ck35.raspberry.happy.chameleon.devices;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SensorTest {

    @Mock RetentionPolicy retentionPolicy;
    @Mock Runnable valuesChangedListener;
    private SlidingValues slidingValues;
    private double delta;

    public SensorTest() {
        this.slidingValues = new SlidingValues(3, 50);
        this.delta = 2d;
    }
    
    public Sensor sensor() {
        return new Sensor(retentionPolicy, slidingValues, valuesChangedListener, delta);
    }
    
    @Test
    public void testGet() {
        when(retentionPolicy.isValid()).thenReturn(true);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(2d);
        sensor.pushValue(3d);
        assertEquals(Optional.of(2d), sensor.getValue());
    }
    
    @Test
    public void testGetWhenNotValid() {
        when(retentionPolicy.isValid()).thenReturn(false);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        assertEquals(Optional.empty(), sensor.getValue());
    }
    
    @Test
    public void testChangeListenerIsNotCalledWhenRetentionPolicySaysFalseAfterPush() {
        when(retentionPolicy.isValid()).thenReturn(false);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        verifyZeroInteractions(valuesChangedListener);
    }
    
    @Test
    public void testChangeListenerIsCalledAfterFirstUpdate() {
        when(retentionPolicy.isValid()).thenReturn(true);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        verify(valuesChangedListener).run();
    }
    
    @Test
    public void testChangeListenerIsOnlyCalledOnceBecauseDifferenceIsTooSmal() {
        when(retentionPolicy.isValid()).thenReturn(true);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(2d);
        sensor.pushValue(2d);
        verify(valuesChangedListener).run();
    }
    
    @Test
    public void testChangeListenerIsCalled() {
        when(retentionPolicy.isValid()).thenReturn(true);
        Sensor sensor = sensor();
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(1d);
        sensor.pushValue(3d);
        sensor.pushValue(3d);
        verify(valuesChangedListener, times(2)).run();
    }

}