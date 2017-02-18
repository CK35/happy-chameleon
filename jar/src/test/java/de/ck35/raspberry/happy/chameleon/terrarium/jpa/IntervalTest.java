package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import static de.ck35.raspberry.happy.chameleon.terrarium.jpa.Interval.between;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;

public class IntervalTest {

    @Test
    public void testContains() {
        ZonedDateTime start = ZonedDateTime.of(2017, 2, 16, 18, 0, 0, 0, ZoneOffset.UTC);
        Interval interval = between(start, start.plusMinutes(5));

        assertTrue(interval.contains(interval));
        assertTrue(interval.contains(between(start, start.plusMinutes(4))));
        assertTrue(interval.contains(between(start.plusMinutes(1), start.plusMinutes(5))));
        assertTrue(interval.contains(between(start.plusMinutes(1), start.plusMinutes(4))));

        assertFalse(interval.contains(between(start.minusMinutes(1), start.plusMinutes(5))));
        assertFalse(interval.contains(between(start, start.plusMinutes(6))));
        assertFalse(interval.contains(between(start.minusMinutes(1), start.plusMinutes(6))));
    }

    @Test
    public void testCompareTo() {
        ZonedDateTime start = ZonedDateTime.of(2017, 2, 16, 18, 0, 0, 0, ZoneOffset.UTC);
        Interval interval = between(start, start.plusMinutes(5));

        assertEquals(0, interval.compareTo(interval));

        assertEquals(-1, interval.compareTo(between(start, start.plusMinutes(6))));
        assertEquals(-1, interval.compareTo(between(start.plusMinutes(1), start.plusMinutes(5))));

        assertEquals(1, interval.compareTo(between(start, start.plusMinutes(4))));
        assertEquals(1, interval.compareTo(between(start.minusMinutes(1), start.plusMinutes(5))));
    }

}