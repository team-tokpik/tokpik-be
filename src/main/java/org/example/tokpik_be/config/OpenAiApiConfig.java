package org.example.tokpik_be.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiApiConfig {

    @Value("${OPEN_AI_API_KEY}")
    private String apiKey;

    @Bean
    public OpenAiChatModel chatModel() {

        OpenAiApi openAiApi = new OpenAiApi(apiKey);

        return new OpenAiChatModel(openAiApi);
    }
}
