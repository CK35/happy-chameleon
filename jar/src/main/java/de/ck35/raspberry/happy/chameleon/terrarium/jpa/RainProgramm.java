package de.ck35.raspberry.happy.chameleon.terrarium.jpa;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.repository.CrudRepository;

@Entity
@Table(name="rain_program")
public class RainProgramm {

	@Id Integer month;
	@Id Integer dayOfWeek;
	@Id Integer hourOfDayStart;
	@Id Integer minuteOfHourStart;
	
	Integer hourOfDayEnd;
	Integer minuteOfHourEnd;
	
	public static interface RainProgrammRepository extends CrudRepository<RainProgramm, Integer> {
        
		List<RainProgramm> findAllByMonthAndDayOfWeek(int month, int dayOfWeek);
		
    }
}