package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.repository.CrudRepository;

@Entity
@Table(name="temperature")
public class Temperature {

    @Id Integer month;
    
    double maxDay;
    double minDay;
    
    double maxNight;
    double minNight;

    public Temperature() {
    }

    public Temperature(int month, double maxDay, double minDay, double maxNight, double minNight) {
        this.month = month;
        this.maxDay = maxDay;
        this.minDay = minDay;
        this.maxNight = maxNight;
        this.minNight = minNight;
    }
    
    public static interface TemperatureRepository extends CrudRepository<Temperature, Integer> {
        
    }
}