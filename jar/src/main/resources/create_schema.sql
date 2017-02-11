CREATE TABLE IF NOT EXISTS `temperature` (
  `month` INT NOT NULL,
  `maxDay` decimal(10,0) NOT NULL,
  `minDay` decimal(10,0) NOT NULL,
  `maxNight` decimal(10,0) NOT NULL,
  `minNight` decimal(10,0) NOT NULL,
  PRIMARY KEY (`month`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `humidity` (
  `month` INT NOT NULL,
  `max` decimal(10,0) NOT NULL,
  `min` decimal(10,0) NOT NULL,
  PRIMARY KEY (`month`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `day_time` (
  `month` INT NOT NULL,
  `dayStartHour` INT NOT NULL,
  `dayStartMinute` INT NOT NULL,
  `dayEndHour` INT NOT NULL,
  `dayEndMinute` INT NOT NULL,
  PRIMARY KEY (`month`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `rain_program` (
  `month` INT NOT NULL,
  `dayOfWeek`INT NOT NULL,
  `hourOfDayStart`INT NOT NULL,
  `minuteOfHourStart`INT NOT NULL,
  `hourOfDayEnd` INT NOT NULL,
  `minuteOfHourEnd` INT NOT NULL,
  PRIMARY KEY (`month`, `dayOfWeek`, `hourOfDayStart`, `minuteOfHourStart`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;