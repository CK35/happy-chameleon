package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ck35.raspberry.happy.chameleon.configuration.JpaConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TerrariumTest.TestConfiguration.class)
public class TerrariumTest {

    @Autowired Clock clock;
    @Autowired Terrarium terrarium;
    
    @Test
    public void testGetCurrentMaxTemperature() {
        assertEquals(20d, terrarium.getCurrentMaxTemperature(), 0d);
    }

    @Test
    public void testC() {
        new CronSequenceGenerator("0 * * * * 7").next(new Date());
    }

    @Configuration
    @PropertySource("classpath:test.properties")
    @Import({JpaConfiguration.class, DataSourceAutoConfiguration.class})
    public static class TestConfiguration  {
        
        @Autowired Environment env;
        
        @Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2016-01-29T10:00:00.00Z"), ZoneOffset.UTC);
        }
        
    }
}