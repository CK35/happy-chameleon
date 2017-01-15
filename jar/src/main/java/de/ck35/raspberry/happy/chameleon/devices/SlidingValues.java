package de.ck35.raspberry.happy.chameleon.devices;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class SlidingValues {

    private final double[] values;
    private final double quantile;
    
    private int startIndex;
    private Double cachedResult;
    
    public SlidingValues(int numberOfValues) {
        this(numberOfValues, 50.0);
    }
    public SlidingValues(int numberOfValues, double quantile) {
        this.quantile = quantile;
        this.values = new double[numberOfValues];
    }
    
    public void push(double value) {
        values[nextStartIndex()] = value;
        cachedResult = null;
    }
    
    public double get() {
        if(cachedResult == null) {
            cachedResult = evaluate();
        }
        return cachedResult;
    }

    private int nextStartIndex() {
        startIndex++;
        if(startIndex == values.length) {
            startIndex = 0;
        }
        return startIndex;
    }
    
    private double evaluate() {
        Percentile percentile = new Percentile(quantile);
        percentile.setData(values);
        return percentile.evaluate();
    }
}