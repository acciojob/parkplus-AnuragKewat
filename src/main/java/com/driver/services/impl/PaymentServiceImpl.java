package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();

        int numberOfHours = reservation.getNumberOfHours();
        int pricePerHour = reservation.getSpot().getPricePerHour();
        int totalCost = pricePerHour * numberOfHours;

        if(amountSent < totalCost) throw new RuntimeException("Insufficient Amount");

        Payment payment = new Payment();
        if(mode.equals("cash")) payment.setPaymentMode(PaymentMode.CASH);
        else if(mode.equals("card")) payment.setPaymentMode(PaymentMode.CARD);
        else if(mode.equals("upi")) payment.setPaymentMode(PaymentMode.UPI);
        else throw new RuntimeException("Payment mode not detected");

        payment.setPaymentCompleted(true);
        reservation.getSpot().setOccupied(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);

        return payment;
    }
}
