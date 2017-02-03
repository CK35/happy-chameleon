package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.repository.CrudRepository;

@Entity
@Table(name="humidity")
public class Humidity {

    @Id Integer month;
    
    double max;
    double min;
    
    public Humidity() {
    }

    public Humidity(int month, double max, double min) {
        this.month = month;
        this.max = max;
        this.min = min;
    }
    
    public static interface HumidityRepository extends CrudRepository<Humidity, Integer> {
        
    }
}