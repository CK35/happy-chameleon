package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

public class DayTimeTest {

    public DayTime dayTime() {
        return new DayTime(1, 8, 8, 22, 22);
    }
    
    @Test
    public void testIsDay() {
        DayTime dayTime = dayTime();
        assertFalse(dayTime.isDay(LocalDateTime.parse("2016-01-28T07:59:00")));
        assertFalse(dayTime.isDay(LocalDateTime.parse("2016-01-28T08:00:00")));
        assertTrue(dayTime.isDay(LocalDateTime.parse("2016-01-28T08:08:00")));
        assertTrue(dayTime.isDay(LocalDateTime.parse("2016-01-28T09:00:00")));
        assertTrue(dayTime.isDay(LocalDateTime.parse("2016-01-28T22:22:00")));
        assertFalse(dayTime.isDay(LocalDateTime.parse("2016-01-28T22:23:00")));
        assertFalse(dayTime.isDay(LocalDateTime.parse("2016-01-28T23:00:00")));
    }

}