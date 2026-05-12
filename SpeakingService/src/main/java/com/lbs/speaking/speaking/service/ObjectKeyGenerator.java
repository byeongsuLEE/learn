package com.lbs.speaking.speaking.service;

import com.lbs.speaking.config.SpeakingProperties;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ObjectKeyGenerator {

    private final SpeakingProperties properties;

    public ObjectKeyGenerator(SpeakingProperties properties) {
        this.properties = properties;
    }

    public String tempKey(Long userId, String uploadId, String mimeType) {
        return "speaking/temp/%d/%s.%s".formatted(userId, uploadId, extension(mimeType));
    }

    public String permanentKey(Long userId, Long recordId, String mimeType) {
        LocalDate today = LocalDate.now(properties.zoneId());
        return "speaking/audio/%d/%04d-%02d/%d.%s"
                .formatted(userId, today.getYear(), today.getMonthValue(), recordId, extension(mimeType));
    }

    private String extension(String mimeType) {
        return switch (mimeType.toLowerCase(Locale.ROOT)) {
            case "audio/webm" -> "webm";
            case "audio/mp4" -> "mp4";
            case "audio/ogg" -> "ogg";
            case "audio/mpeg" -> "mp3";
            case "audio/wav" -> "wav";
            default -> "bin";
        };
    }
}
