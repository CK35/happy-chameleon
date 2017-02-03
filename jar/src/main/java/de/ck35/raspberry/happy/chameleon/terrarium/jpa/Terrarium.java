package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.ck35.raspberry.happy.chameleon.terrarium.jpa.DayTime.DayTimeRepository;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.Humidity.HumidityRepository;
import de.ck35.raspberry.happy.chameleon.terrarium.jpa.Temperature.TemperatureRepository;

@Component
public class Terrarium {

    @Autowired Clock clock;
    @Autowired TemperatureRepository temperatureRepository;
    @Autowired HumidityRepository humidityRepository;
    @Autowired DayTimeRepository dayTimeRepository;
    
    @PostConstruct
    public void init() {
        if(temperatureRepository.count() != 12) {
            //http://www.jemenchamaeleon.de/artgerechte_haltung_im_terrarium_zimmer.htm
            IntStream.of(11, 12, 1, 2).mapToObj(month -> new Temperature(month, 20d, 18d, 14d, 12d)).forEach(temperatureRepository::save);
            IntStream.of(3, 4, 5, 6, 7, 8, 9, 10).mapToObj(month -> new Temperature(month, 28d, 26d, 20d, 16d)).forEach(temperatureRepository::save);
        }
        if(humidityRepository.count() != 12) {
            IntStream.range(1, 13).mapToObj(month -> new Humidity(month, 60d, 40d)).forEach(humidityRepository::save);
        }
        if(dayTimeRepository.count() != 12) {
            IntStream.range(1, 13).mapToObj(month -> new DayTime(month, 8, 0, 22, 0)).forEach(dayTimeRepository::save);
        }
        
    }
    
    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
    
    public boolean isDay() {
        return isDay(now());
    }
    public boolean isNight() {
        return !isDay();
    }
    private  boolean isDay(LocalDateTime now) {
        return dayTimeRepository.findOne(now.getMonthValue()).isDay(now);
    }
    
    public double getCurrentMaxTemperature() {
        LocalDateTime now = now();
        Temperature temperature = temperatureRepository.findOne(now.getMonthValue());
        if(isDay(now)) {
            return temperature.maxDay;
        } else {
            return temperature.maxNight;
        }
    }
    

}