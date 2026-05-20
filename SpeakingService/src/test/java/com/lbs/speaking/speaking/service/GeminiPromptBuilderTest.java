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
        assertThat(prompt).contains("Do not evaluate pronunciation");
        assertThat(prompt).contains("\"improvedText\"");
        assertThat(prompt).contains("\"feedback\"");
        assertThat(prompt).contains("Give all feedback, metric comments, issue explanations, and coaching text in Korean.");
        assertThat(prompt).contains("fluency, label:");
        assertThat(prompt).contains("relevance, label:");
        assertThat(prompt).contains("\"issues\"");
        assertThat(prompt).contains("\"renderBlocks\"");
        assertThat(prompt).contains("\"sentenceIndex\"");
        assertThat(prompt).contains("\"role\"");
        assertThat(prompt).contains("\"issueIndexes\"");
        assertThat(prompt).contains("sentence-by-sentence");
        assertThat(prompt).contains("red");
        assertThat(prompt).contains("blue");
        assertThat(prompt).contains(GeminiPromptBuilder.VARIABLE_INPUT_MARKER);
        assertThat(prompt).contains("What habit helped you grow the most this year?");
        assertThat(prompt).contains("I go school every morning.");
    }

    @Test
    void keepsInstructionPrefixStableForImplicitCacheHit() {
        String firstPrompt = promptBuilder.build(
                "Describe a small routine that makes your day better.",
                "I drink coffee in the morning."
        );
        String secondPrompt = promptBuilder.build(
                "Tell me about a project you are proud of.",
                "I built a chat feature with WebSocket."
        );

        String firstPrefix = firstPrompt.substring(0, firstPrompt.indexOf(GeminiPromptBuilder.VARIABLE_INPUT_MARKER));
        String secondPrefix = secondPrompt.substring(0, secondPrompt.indexOf(GeminiPromptBuilder.VARIABLE_INPUT_MARKER));

        assertThat(firstPrefix).isEqualTo(secondPrefix);
        assertThat(firstPrefix).doesNotContain("Describe a small routine");
        assertThat(firstPrefix).doesNotContain("I drink coffee");
        assertThat(secondPrefix).doesNotContain("Tell me about a project");
        assertThat(secondPrefix).doesNotContain("I built a chat feature");
    }
}
