package model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class Excursion extends TourService {
    private String guidName;
    private Duration duration;
    private Difficulty difficulty;

    @Override
    public BigDecimal calculateTotalPrice(int participants) {
        BigDecimal basePrice = getPrice()
                .multiply(BigDecimal.valueOf(participants))
                .multiply(difficulty.getMultiplier());

        if (participants > 10) {
            return basePrice.multiply(BigDecimal.valueOf(0.9));
        } else {
            return basePrice;
        }
    }

    @Override
    public String toString() {
        String durationStr = (duration != null)
                ? String.format("%d ч. %d мин.", duration.toHours(), duration.toMinutesPart())
                : "null";

        String difficultyStr = (difficulty != null) ? difficulty.name() : "null";

        return "Excursion{" +
                "guidName=\"" + guidName + "\"" +
                ", duration=\"" + durationStr + "\"" +
                ", difficulty=\"" + difficultyStr + "\"" +
                "}";
    }

    public void setGuidName(String guidName) {
        this.guidName = guidName;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
