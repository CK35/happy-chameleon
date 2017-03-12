package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Interval implements Comparable<Interval> {

    private final ZonedDateTime start;
    private final ZonedDateTime end;

    public Interval(ZonedDateTime start, ZonedDateTime end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
    }

    public ZonedDateTime getStart() {
        return start;
    }
    public ZonedDateTime getEnd() {
        return end;
    }

    public Duration toDuration() {
        return Duration.between(start, end);
    }
    
    public boolean isBefore(ZonedDateTime timestamp) {
        return timestamp.isEqual(end) || timestamp.isAfter(end);
    }
    
    /**
     * Test if this interval contains the given timestamp where start is inclusive and end is exclusive.
     * 
     * @param timestamp The timestamp to test.
     * @return true if the given timestamp is contained inside this interval.
     */
    public boolean contains(ZonedDateTime timestamp) {
        return timestamp.equals(start) || (timestamp.isAfter(start) && timestamp.isBefore(end));
    }
    
    /**
     * Test if the given interval is contained inside this interval or if it is
     * the same as this interval.
     * 
     * @param other
     *            The interval to test.
     * @return <code>true</code> if the given interval is contained in this
     *         interval.
     */
    public boolean contains(Interval other) {
        if (other.end.isAfter(end) || other.start.isBefore(start)) {
            return false;
        } else {
            return true;
        }
    }
    
    public Interval withEnd(ZonedDateTime end) {
        return between(start, end);
    }

    @Override
    public int compareTo(Interval other) {
        if (start.isBefore(other.start)) {
            return -1;
        }
        if (start.isAfter(other.start)) {
            return 1;
        }
        if (end.isBefore(other.end)) {
            return -1;
        }
        if (end.isAfter(other.end)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Interval)) {
            return false;
        }
        Interval other = (Interval) obj;
        if (!start.equals(other.start)) {
            return false;
        }
        if (!end.equals(other.end)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return start.toString() + " - " + end.toString();
    }

    public static List<Interval> merge(Iterable<Interval> intervals) {
        List<Interval> sorted = StreamSupport.stream(intervals.spliterator(), false)
                                             .sorted()
                                             .collect(Collectors.toList());
        for (int index = 0; index < sorted.size(); index++) {
            Interval interval = sorted.get(index);
            for (Iterator<Interval> iter = sorted.subList(index + 1, sorted.size())
                                                 .iterator(); iter.hasNext();) {
                Interval next = iter.next();
                if(interval.contains(next)) {
                    iter.remove();
                    continue;
                }
                if(interval.getEnd().equals(next.getStart()) || interval.getEnd().isAfter(next.getStart())) {
                    interval = interval.withEnd(next.getEnd());
                    sorted.set(index, interval);
                    iter.remove();
                    continue;
                }
                break;
            }
        }
        return sorted;
    }

    public static Interval between(ZonedDateTime start, ZonedDateTime end) {
        return new Interval(start, end);
    }
}