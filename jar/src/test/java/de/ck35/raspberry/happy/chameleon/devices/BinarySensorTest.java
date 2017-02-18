package de.ck35.raspberry.happy.chameleon.devices;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BinarySensorTest {

    @Mock Runnable valuesChangedListener;
    @Mock RetentionPolicy retentionPolicy;
    
    private int minNumberOfSameValues;

    public BinarySensorTest() {
        this.minNumberOfSameValues = 3;
    }
    
    public BinarySensor binarySensor() {
        return new BinarySensor(retentionPolicy, minNumberOfSameValues, valuesChangedListener);
    }
    
    @Test
    public void testGetValues() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        verify(valuesChangedListener).run();
        assertEquals(Optional.of(true), binarySensor.getValue());
    }
    
    @Test
    public void testGetValuesWhenRetentionPolicyIsFalse() {
        when(retentionPolicy.isValid()).thenReturn(false);
        BinarySensor binarySensor = binarySensor();
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        verifyZeroInteractions(valuesChangedListener);
        assertEquals(Optional.empty(), binarySensor.getValue());
    }
    
    @Test
    public void testGetValuesInitial() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        assertEquals(Optional.empty(), binarySensor.getValue());
    }
    
    @Test
    public void testGetValuesWhenOverwrittenTrue() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        binarySensor.enableOverwirte(true);
        verify(valuesChangedListener).run();
        assertEquals(Optional.of(true), binarySensor.getValue());

        binarySensor.disableOverwrite();
        verify(valuesChangedListener, times(2)).run();
        assertEquals(Optional.empty(), binarySensor.getValue());
    }
    
    @Test
    public void testGetValuesWhenOverwrittenFalse() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        binarySensor.enableOverwirte(false);
        verify(valuesChangedListener).run();
        assertEquals(Optional.of(false), binarySensor.getValue());

        binarySensor.disableOverwrite();
        verify(valuesChangedListener, times(2)).run();
        assertEquals(Optional.empty(), binarySensor.getValue());
    }
    
    @Test
    public void testInvalidationAfterPushAndOldValueWasPresent() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        verify(valuesChangedListener).run();
        assertEquals(Optional.of(true), binarySensor.getValue());
        
        when(retentionPolicy.isValid()).thenReturn(true).thenReturn(false);
        binarySensor.pushValue(true);
        verify(valuesChangedListener, times(2)).run();
        assertEquals(Optional.empty(), binarySensor.getValue());
    }
    
    @Test
    public void testListenerIsCalledAfterChange() {
        when(retentionPolicy.isValid()).thenReturn(true);
        BinarySensor binarySensor = binarySensor();
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        binarySensor.pushValue(true);
        verify(valuesChangedListener).run();
        assertEquals(Optional.of(true), binarySensor.getValue());
        
        binarySensor.pushValue(false);
        binarySensor.pushValue(false);
        binarySensor.pushValue(false);
        verify(valuesChangedListener, times(2)).run();
        assertEquals(Optional.of(false), binarySensor.getValue());
    }
    
}