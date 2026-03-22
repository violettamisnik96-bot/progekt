package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Booking {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String bookingId;
    private final Client client;
    private final Map<TourService, Integer> serviceParticipants;
    private final LocalDate bookingDate;
    private BookingStatus status;

    // Конструктор с валидацией
    public Booking(Client client, Map<TourService, Integer> serviceParticipants, LocalDate bookingDate) {
        // реализация
        this.bookingId = generateBookingId(LocalDate.now());
        this.client = client;
        this.serviceParticipants = serviceParticipants;
        this.bookingDate = bookingDate;
    }

    // Генерация ID: BK + timestamp + 4 случайные цифры
    private String generateBookingId(LocalDate timestamp) {
        String timestampStr = timestamp.format(TIMESTAMP_FORMATTER);
        int randomSuffix = (int) (Math.random() * 10000);
        return String.format("BK%s%04d", timestampStr, randomSuffix);
    }

    // Методы управления услугами
    public void addService(TourService service, int participants) {
        serviceParticipants.put(service, participants);
    }
    public void removeService(TourService service) { /* ... */ }
    public void updateParticipants(TourService service, int newParticipants) { /* ... */ }

    // Вычисление итоговой суммы
    public double calculateTotalAmount() { /* ... */
        return 0;
    }

    // Методы перехода между статусами
    public void confirm() {
        if (this.status == BookingStatus.PENDING) {
            this.status = BookingStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Невозможно подтвердить бронирование в статусе: " + this.status);
        }
    }
    public void complete() {
        if (this.status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.COMPLETED;
            // Начисляем клиенту 10% от цены баллами
            double totalAmount = calculateTotalAmount();
            double bonusPoints = totalAmount * 0.1;
            client.addBonusPoints(bonusPoints);
        } else {
            throw new IllegalStateException("Невозможно завершить бронирование в статусе: " + this.status);
        }
    }
    public void cancel() {
        if (this.status == BookingStatus.PENDING || this.status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Невозможно отменить бронирование в статусе: " + this.status);
        }
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public Client getClient() { return client; }
    public Map<TourService, Integer> getServiceParticipants() {
        return new HashMap<>(serviceParticipants);
    }
    public LocalDate getBookingDate() { return bookingDate; }
    public BookingStatus getStatus() { return status; }
}