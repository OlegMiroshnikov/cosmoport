package com.space.service;

import com.space.model.Ship;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ShipValidator {
    public void validateShip(Ship ship) {
        validateShipIsNulle (ship);
        validateName (ship.getName ());
        validatePlanet (ship.getPlanet ());
        validateProdDate (ship.getProdDate ());
        validateSpeed (ship.getSpeed ());
        validateCrewSize (ship.getCrewSize ());
    }

    private void validateShipIsNulle(Ship ship) {
        if (ship == null) {
            throw new IllegalArgumentException ("Ship must not be null");
        }
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException ("Ship name must not be null");
        }
        if (name.isEmpty ()) {
            throw new IllegalArgumentException ("Ship name must not be empty");
        }
        if (name.length () > 50) {
            throw new IllegalArgumentException ("Ship name length must not be greater then 50");
        }
    }

    private void validatePlanet(String planet) {
        if (planet == null) {
            throw new IllegalArgumentException ("Ship plane must not be null");
        }
        if (planet.isEmpty ()) {
            throw new IllegalArgumentException ("Ship planet must not be empty");
        }
        if (planet.length () > 50) {
            throw new IllegalArgumentException ("Ship planet length must not be greater then 50");
        }
    }

    private void validateProdDate(Date prodDate) {
        if (prodDate == null) {
            throw new IllegalArgumentException ("Ship prodDate must not be null");
        }
        if (prodDate.getTime () < 0) {
            throw new IllegalArgumentException ("Ship prodDate must not be negative");
        }
        SimpleDateFormat spf = new SimpleDateFormat ("dd.MM.yyyy");
        try {
            Date year2800Date = spf.parse ("01.01.2800");
            Date year3019Date = spf.parse ("31.12.3019");
            if (prodDate.getTime () < year2800Date.getTime () || prodDate.getTime () > year3019Date.getTime ()) {
                throw new IllegalArgumentException ("Ship prodDate must be between (2800..3019)");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }
    }

    private void validateSpeed(Double speed) {
        if (speed == null) {
            throw new IllegalArgumentException ("Ship speed must not be null");
        }
        Double roundValue = new BigDecimal (Double.toString (speed)).
                setScale (2, RoundingMode.HALF_EVEN).
                doubleValue ();
        if (roundValue < 0.01 || roundValue > 0.99) {
            throw new IllegalArgumentException ("Ship speed must be between (0.01..0.99");
        }
    }

    private void validateCrewSize(Integer crewStze) {
        if (crewStze == null) {
            throw new IllegalArgumentException ("Ship crewSize must not be null");
        }
        if (crewStze < 1 || crewStze > 9999) {
            throw new IllegalArgumentException ("Ship crewSize must be between (1..9999)");
        }
    }


}
