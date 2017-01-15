package de.ck35.raspberry.happy.chameleon.devices;

import java.util.Optional;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * A sliding window for sensor values which includes a percentile evaluation which eliminates outliers.
 * 
 * @author Christian Kaspari
 * @since 1.0.0
 */
public class SlidingValues {

    private final double[] values;
    private final double quantile;
    
    private int startIndex;
    private boolean valuesComplete;
    private Double cachedResult;
    
    public SlidingValues(int numberOfValues) {
        this(numberOfValues, 50.0);
    }
    
    public SlidingValues(int numberOfValues, double quantile) {
        if(numberOfValues < 1) {
            throw new IllegalArgumentException("Number of values must be greater than 1. But was: '" + numberOfValues + "'!");
        }
        if(quantile <= 0d || quantile >= 100d) {
            throw new IllegalArgumentException("Selected quantile must be between 0 and 100 (exclusive). But was: '" + quantile + "'!");
        }
        this.quantile = quantile;
        this.values = new double[numberOfValues];
        this.startIndex = -1;
    }
    
    public void push(double value) {
        values[nextStartIndex()] = value;
        cachedResult = null;
    }
    
    public Optional<Double> get() {
        if(!valuesComplete) {
            return Optional.empty(); 
        }
        if(cachedResult == null) {
            cachedResult = evaluate();
        }
        return Optional.of(cachedResult);
    }

    private int nextStartIndex() {
        startIndex++;
        if(startIndex == values.length -1) {
            valuesComplete = true;            
        }
        if(startIndex == values.length) {
            startIndex = 0;
        }
        return startIndex;
    }
    
    private Double evaluate() {
        Percentile percentile = new Percentile(quantile);
        percentile.setData(values);
        return percentile.evaluate();
    }
}