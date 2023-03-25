package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        try{
            user = userRepository3.findById(userId).get();
        }
        catch(Exception e) {
            throw new RuntimeException("Cannot make reservation");
        }

        ParkingLot parkingLot;
        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch(Exception e) {
            throw new RuntimeException("Cannot make reservation");
        }

        SpotType vehicleType;
        if(numberOfWheels <= 2) vehicleType = SpotType.TWO_WHEELER;
        else if(numberOfWheels <=4) vehicleType = SpotType.FOUR_WHEELER;
        else vehicleType = SpotType.OTHERS;

        List<Spot> spotList = new ArrayList<>();
        Integer minCost = null;
        Spot reservedSpot = null;
        for(Spot spot: parkingLot.getSpotList()) {
            if((minCost == null || spot.getPricePerHour()<minCost) && (spot.getOccupied() == false && spot.getSpotType().equals(vehicleType))) {
                minCost = spot.getPricePerHour();
                reservedSpot = spot;
            }
        }

        if(reservedSpot == null) throw new RuntimeException("Cannot make reservation");

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(reservedSpot);

        List<Reservation> reservations = user.getReservationList();
        reservations.add(reservation);
        user.setReservationList(reservations);
        userRepository3.save(user);

        reservations = reservedSpot.getReservationList();
        reservations.add(reservation);
        reservedSpot.setReservationList(reservations);
        spotRepository3.save(reservedSpot);

        return reservation;
    }
}
