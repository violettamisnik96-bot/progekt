package model;

import exception.TourServiceValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Booking {
    private static final Random RANDOM = new Random();

    private String bookingId;
    private Client client;
    private Map<TourService, Integer> serviceParticipants;
    private LocalDate bookingDate;
    private BookingStatus status;

    public Booking(Client client, Map<TourService, Integer> serviceParticipants) throws TourServiceValidationException {
        validateBooking(client, serviceParticipants);

        this.bookingId = "BK" + System.currentTimeMillis() + String.format("%04d", RANDOM.nextInt(10000));
        this.client = client;
        this.serviceParticipants = new HashMap<>(serviceParticipants);
        this.bookingDate = LocalDate.now();
        this.status = BookingStatus.PENDING;
    }

    private void validateBooking(Client client, Map<TourService, Integer> serviceParticipants) throws TourServiceValidationException {
        if (client == null) {
            throw new TourServiceValidationException("client не может быть null");
        }

        if (serviceParticipants == null || serviceParticipants.isEmpty()) {
            throw new TourServiceValidationException("Некорректные параметры услуги");
        }


        for (Map.Entry<TourService, Integer> entry : serviceParticipants.entrySet()) {
            TourService service = entry.getKey();
            int participants = entry.getValue();

            if (!service.isAvailableOn(LocalDate.now())) {
                throw new TourServiceValidationException(
                        "Услуга \"" + service.getName() + "\" недоступна"
                );
            }


            if (service instanceof HotelStay hotel) {
                int max = switch (hotel.getRoomType()) {
                    case SINGLE -> 1;
                    case DOUBLE -> 2;
                    case FAMILY -> 4;
                };
                if (participants > max) {
                    throw new TourServiceValidationException(
                            "Для услуги \"" + service.getName() + "\" макс. участников: " + max
                    );
                }
            }
        }
    }

    public BigDecimal calculateTotalPrice() {
        BigDecimal total = serviceParticipants.entrySet().stream()
                .map(e -> e.getKey().calculateTotalPrice(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.multiply(BigDecimal.ONE.subtract(client.getDiscountRate()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void confirm() throws TourServiceValidationException {
        if (status != BookingStatus.PENDING)
            throw new TourServiceValidationException("Статус должен быть PENDING");
        status = BookingStatus.CONFIRMED;
    }

    public void complete() throws TourServiceValidationException {
        if (status != BookingStatus.CONFIRMED)
            throw new TourServiceValidationException("Статус должен быть CONFIRMED");

        client.addLoyaltyPoints(calculateTotalPrice().multiply(new BigDecimal("0.1")).intValue());
        status = BookingStatus.COMPLETED;
    }

    public void cancel() throws TourServiceValidationException {
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED)
            throw new TourServiceValidationException("Статус должен быть PENDING или CONFIRMED");
        status = BookingStatus.CANCELLED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Client getClient() {
        return client;
    }

    public Map<TourService, Integer> getServiceParticipants() {
        return new HashMap<>(serviceParticipants);
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=\"" + bookingId + "\"" +
                ", client=\"" + client.getFullName() + "\"" +
                ", services=" + serviceParticipants.size() +
                ", date=\"" + bookingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\"" +
                ", status=" + status +
                ", total=" + calculateTotalPrice() +
                "}";
    }
}