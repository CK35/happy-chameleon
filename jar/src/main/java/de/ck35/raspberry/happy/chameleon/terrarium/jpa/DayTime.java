package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.repository.CrudRepository;

@Entity
@Table(name="day_time")
public class DayTime {

    @Id Integer month;
    
    int dayStartHour;
    int dayStartMinute;
    int dayEndHour;
    int dayEndMinute;

    public DayTime() {
    }
    
    public DayTime(Integer month, int dayStartHour, int dayStartMinute, int dayEndHour, int dayEndMinute) {
        this.month = month;
        this.dayStartHour = dayStartHour;
        this.dayStartMinute = dayStartMinute;
        this.dayEndHour = dayEndHour;
        this.dayEndMinute = dayEndMinute;
    }
    
    public boolean isDay(LocalDateTime time) {
        if(time.getHour() < dayStartHour || time.getHour() > dayEndHour) {
            return false;
        }
        if(time.getHour() > dayStartHour  && time.getHour() < dayEndHour) {
            return true;
        }
        if(time.getHour() == dayStartHour) {
            return time.getMinute() >= dayStartMinute;
        }
        return time.getMinute() <= dayEndMinute;
    }
    
    public static interface DayTimeRepository extends CrudRepository<DayTime, Integer> {
        
    }
}