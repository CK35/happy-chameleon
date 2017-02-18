package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ck35.raspberry.happy.chameleon.configuration.JpaConfiguration;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.RainProgramm.RainProgramms;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TerrariumTest.TestConfiguration.class)
public class TerrariumTest {

    @Autowired Clock clock;
    @Autowired Terrarium terrarium;
    @Autowired RainProgramms rainProgramms;
    
    @Test
    public void testGetCurrentMaxTemperature() {
        assertEquals(20d, terrarium.getCurrentMaxTemperature(), 0d);
    }

    @Test
    public void testRainProgramms() {
        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 20), LocalTime.of(8, 30));
        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 0), LocalTime.of(8, 0));
        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 10), LocalTime.of(9, 0));
        
        LocalDate date = LocalDate.of(2017, 2, 17);
        ZoneId zoneId = ZoneOffset.UTC;
        ZonedDateTime start = ZonedDateTime.of(date.atTime(7, 0), zoneId);
        ZonedDateTime end = ZonedDateTime.of(date.atTime(9, 0), zoneId);
        assertEquals(Arrays.asList(Interval.between(start, end)), rainProgramms.findProgrammsForDay(date, zoneId));
    }

    @Configuration
    @PropertySource("classpath:test.properties")
    @Import({JpaConfiguration.class, DataSourceAutoConfiguration.class})
    public static class TestConfiguration  {
        
        @Autowired Environment env;
        
        @Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2017-02-17T10:00:00.00Z"), ZoneOffset.UTC);
        }
        
    }
}