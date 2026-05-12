package com.lbs.speaking.config;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "speaking")
public record SpeakingProperties(
        String timezone,
        long uploadSessionTtlMinutes,
        long maxAudioSizeBytes,
        int maxDurationSec,
        List<String> allowedMimeTypes,
        Rabbit rabbitmq,
        Jobs jobs,
        Gemini gemini
) {

    public ZoneId zoneId() {
        return ZoneId.of(timezone);
    }

    public Duration uploadSessionTtl() {
        return Duration.ofMinutes(uploadSessionTtlMinutes);
    }

    public record Rabbit(
            String exchange,
            String queue,
            String routingKey,
            String deadLetterExchange,
            String deadLetterQueue,
            String deadLetterRoutingKey
    ) {
    }

    public record Jobs(String recoveryCron, String cleanupCron, long staleAnalysisMinutes, long tempCleanupHours) {
    }

    public record Gemini(
            boolean enabled,
            String endpoint,
            String apiKey,
            String model,
            int timeoutSeconds
    ) {
    }
}
