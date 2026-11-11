package dto;

import java.util.UUID;

public record FailledAttempt(
        UUID id,
        String xParam,
        String yParam,
        String rParam
) {
    public FailledAttempt(String xParam, String yParam, String rParam){
        this(UUID.randomUUID(), xParam, yParam, rParam);
    }
}
