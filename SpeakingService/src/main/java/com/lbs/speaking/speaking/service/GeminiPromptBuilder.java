package com.lbs.speaking.speaking.service;

import org.springframework.stereotype.Component;

@Component
public class GeminiPromptBuilder {

    public String build(String question, String transcript) {
        return """
                You are an English speaking coach for Korean learners.

                Analyze the learner's answer against the question.
                Focus only on clarity, grammar, natural expression, repetition, filler words, and answer structure.
                Do not analyze pronunciation, intonation, speech speed, emotion, or audio quality.

                Return only valid JSON. Do not wrap it in markdown. Do not add explanations outside JSON.

                JSON schema:
                {
                  "improvedText": "string",
                  "issues": [
                    {
                      "type": "FILLER | REPETITION | AWKWARD_EXPRESSION | GRAMMAR | TOO_LONG | UNCLEAR | STRUCTURE",
                      "startIndex": 0,
                      "endIndex": 0,
                      "original": "string",
                      "suggestion": "string",
                      "explanation": "string",
                      "highlightable": true
                    }
                  ],
                  "renderBlocks": [
                    {
                      "kind": "original | improved | normal",
                      "text": "string",
                      "color": "red | blue | none",
                      "issueIndex": 0
                    }
                  ]
                }

                Rendering rule:
                - Use red blocks for the original phrase that should be improved.
                - Use blue blocks for the improved phrase.
                - Use highlightable=false if an issue should be saved but cannot be safely highlighted.

                Question:
                %s

                Learner answer:
                %s
                """.formatted(question, transcript);
    }
}
