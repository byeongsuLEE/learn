package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GeminiPromptBuilderTest {

    private final GeminiPromptBuilder promptBuilder = new GeminiPromptBuilder();

    @Test
    void buildsPromptWithStrictJsonSchemaAndUserAnswer() {
        String prompt = promptBuilder.build(
                "What habit helped you grow the most this year?",
                "I go school every morning."
        );

        assertThat(prompt).contains("Return only valid JSON");
        assertThat(prompt).contains("Do not analyze pronunciation");
        assertThat(prompt).contains("\"improvedText\"");
        assertThat(prompt).contains("\"issues\"");
        assertThat(prompt).contains("\"renderBlocks\"");
        assertThat(prompt).contains("red");
        assertThat(prompt).contains("blue");
        assertThat(prompt).contains("What habit helped you grow the most this year?");
        assertThat(prompt).contains("I go school every morning.");
    }
}
