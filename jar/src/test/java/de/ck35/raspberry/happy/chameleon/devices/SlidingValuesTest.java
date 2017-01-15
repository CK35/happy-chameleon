package de.ck35.raspberry.happy.chameleon.devices;

import static org.junit.Assert.*;

import org.junit.Test;

public class SlidingValuesTest {

    private int numberOfValues;
    private double quantile;

    public SlidingValuesTest() {
        this.numberOfValues = 3;
        this.quantile = 50.0;
    }
    
    public SlidingValues slidingValues() {
        return new SlidingValues(numberOfValues, quantile);
    }
    
    @Test
    public void testPushOneValue() {
        numberOfValues = 1;
        SlidingValues slidingValues = slidingValues();
        slidingValues.push(2d);
        assertEquals(2d, slidingValues.get().orElse(-1d), 0d);
    }
    
    @Test
    public void testPush() {
        SlidingValues slidingValues = slidingValues();
        slidingValues.push(1d);
        slidingValues.push(1d);
        slidingValues.push(1d);
        slidingValues.push(2d);
        slidingValues.push(3d);
        
        assertEquals(2d, slidingValues.get().orElse(-1d), 0d);
    }
    
    @Test
    public void testGetWithNoValuesPushed() {
        assertFalse(slidingValues().get().isPresent());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithInvalidNumberOfValueNegative() {
        new SlidingValues(-1);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithInvalidNumberOfValueZero() {
        new SlidingValues(0);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithInvalidQuantileNegative() {
        new SlidingValues(1, -1d);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithInvalidQuantile() {
        new SlidingValues(1, 100d);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testConstructWithInvalidQuantileTooBig() {
        new SlidingValues(1, 101d);
    }
}