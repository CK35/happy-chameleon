package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "rain_program")
public class RainProgramm {

    @Id Integer month;
    @Id Integer dayOfWeek;
    @Id Integer hourOfDayStart;
    @Id Integer minuteOfHourStart;

    Integer hourOfDayEnd;
    Integer minuteOfHourEnd;

    public static interface RainProgrammRepository extends CrudRepository<RainProgramm, Integer> {

        List<RainProgramm> findAllByMonthAndDayOfWeek(int month, int dayOfWeek);
        
        @Transactional
        void deleteByMonth(int month);
    }

    @Component
    public static class RainProgramms {

        @Autowired RainProgrammRepository repository;

        public void save(Month month, DayOfWeek dayOfWeek, LocalTime start, LocalTime end) {
            RainProgramm programm = new RainProgramm();
            programm.month = month.getValue();
            programm.dayOfWeek = dayOfWeek.getValue();
            programm.hourOfDayStart = start.getHour();
            programm.minuteOfHourStart = start.getMinute();
            programm.hourOfDayEnd = end.getHour();
            programm.minuteOfHourEnd = end.getMinute();
            repository.save(programm);
        }
        
        public void deleteAll(Month month) {
            repository.deleteByMonth(month.getValue());
        }

        public List<Interval> findProgrammsForDay(LocalDate date, ZoneId zone) {
            return Interval.merge(repository.findAllByMonthAndDayOfWeek(date.getMonthValue(), date.getDayOfWeek()
                                                                                                  .getValue())
                                            .stream()
                                            .map(toIntervalWithDate(date, zone))
                                            .collect(Collectors.toList()));
        }

        private Function<RainProgramm, Interval> toIntervalWithDate(LocalDate date, ZoneId zoneId) {
            return input -> {
                ZonedDateTime startOfDay = date.atStartOfDay(zoneId);
                ZonedDateTime start = startOfDay.with(LocalTime.of(input.hourOfDayStart, input.minuteOfHourStart));
                ZonedDateTime end = startOfDay.with(LocalTime.of(input.hourOfDayEnd, input.minuteOfHourEnd));
                if (start.isAfter(end)) {
                    return new Interval(start, end.plusDays(1));
                } else {
                    return new Interval(start, end);
                }
            };
        }
    }
}