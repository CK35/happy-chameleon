package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.util.function.Predicate;

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

import de.ck35.raspberry.happy.chameleon.configuration.DeviceConfiguration;
import de.ck35.raspberry.happy.chameleon.configuration.JpaConfiguration;
import de.ck35.raspberry.happy.chameleon.devices.Sensor;
import de.ck35.raspberry.happy.chameleon.devices.Switch;
import de.ck35.raspberry.happy.chameleon.terrarium.RainSystemTimer;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.RainProgramm.RainProgramms;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TerrariumTest.TestConfiguration.class)
public class TerrariumTest {

    @Autowired Clock clock;
    @Autowired Terrarium terrarium;
    @Autowired RainProgramms rainProgramms;
    @Autowired RainSystemTimer rainSystemTimer;

    @Autowired Switch rainSystemSwitch;
    @Autowired Sensor temperatureSensor;
    @Autowired Sensor humiditySensor;

    @Test
    public void testGetCurrentMaxTemperature() {
        assertEquals(20d, terrarium.getCurrentMaxTemperature(), 0d);
    }

    @Test
    public void testRainProgramms() {
        
        temperatureSensor.pushValue(19d);
        humiditySensor.pushValue(50d);
        
        assertTrue(rainSystemSwitch.isOff());

        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 20), LocalTime.of(8, 30));
        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 0), LocalTime.of(8, 0));
        rainProgramms.save(Month.FEBRUARY, DayOfWeek.FRIDAY, LocalTime.of(7, 10), LocalTime.of(9, 0));

        LocalDate date = LocalDate.of(2017, 2, 17);
        ZoneId zoneId = ZoneOffset.UTC;
        ZonedDateTime start = ZonedDateTime.of(date.atTime(7, 0), zoneId);
        ZonedDateTime end = ZonedDateTime.of(date.atTime(9, 0), zoneId);
        assertEquals(Arrays.asList(Interval.between(start, end)), rainProgramms.findProgrammsForDay(date, zoneId));

        rainSystemTimer.update();
        assertState(rainSystemSwitch, Switch::isOn);
        
        rainProgramms.deleteAll(Month.FEBRUARY);
        
        rainSystemTimer.update();
        assertState(rainSystemSwitch, Switch::isOff);
    }

    @Configuration
    @PropertySource("classpath:test.properties")
    @Import({ JpaConfiguration.class, DataSourceAutoConfiguration.class, DeviceConfiguration.class })
    public static class TestConfiguration {

        @Autowired Environment env;

        @Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2017-02-17T08:30:00.00Z"), ZoneOffset.UTC);
        }

    }
    
    public static void assertState(Switch actualSwitch, Predicate<Switch> state) {
        for (int i = 0; i < 60; i++) {
            if (state.test(actualSwitch)) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail("Interrupted while waiting for switch.");
            }
        }
        fail("Switch is still off after 60 seconds waiting.");
    }
}