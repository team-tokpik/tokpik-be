package org.example.tokpik_be.config;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class TestConfig {
    @MockBean
    private OpenAiModerationModel openAiModerationModel;
    @MockBean
    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
    @MockBean
    private OpenAiImageModel openAiImageModel;
    @MockBean
    private OpenAiEmbeddingModel openAiEmbeddingModel;
    @MockBean
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;
}
