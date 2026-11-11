package dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record Point(
        UUID id,
        double x,
        double y,
        int r,
        boolean hit,
        LocalDateTime currentTime,
        long executionTime
) {
    public Point(double x, double y, int r, boolean hit, long executionTime){
        this(UUID.randomUUID(), x, y, r, hit, LocalDateTime.now(), executionTime);
    }
}
