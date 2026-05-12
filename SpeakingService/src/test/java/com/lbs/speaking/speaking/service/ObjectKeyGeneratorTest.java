package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.lbs.speaking.config.SpeakingProperties;
import java.util.List;
import org.junit.jupiter.api.Test;

class ObjectKeyGeneratorTest {

    private final ObjectKeyGenerator generator = new ObjectKeyGenerator(new SpeakingProperties(
            "Asia/Seoul",
            10,
            20_971_520,
            180,
            List.of("audio/webm"),
            new SpeakingProperties.Rabbit("exchange", "queue", "routing", "dlx", "dlq", "dlq-routing"),
            new SpeakingProperties.Jobs("0 */5 * * * *", "0 0 * * * *", 15, 24),
            new SpeakingProperties.Gemini(false, "https://generativelanguage.googleapis.com", "", "gemini-2.5-flash", 30)
    ));

    @Test
    void createsTempObjectKey() {
        String key = generator.tempKey(10L, "upload-1", "audio/webm");

        assertThat(key).isEqualTo("speaking/temp/10/upload-1.webm");
    }

    @Test
    void createsPermanentObjectKeyWithUserMonthAndRecord() {
        String key = generator.permanentKey(10L, 99L, "audio/mp4");

        assertThat(key).startsWith("speaking/audio/10/");
        assertThat(key).endsWith("/99.mp4");
    }
}
