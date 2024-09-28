package org.example.tokpik_be.util.llm.client;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicDetailRequest;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicDetailResponse;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.ResponseFormat;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.ResponseFormat.Type;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GPTApiClient implements LLMApiClient {

    private final OpenAiChatModel chatModel;

    @Override
    public LLMTalkTopicsResponse searchTalkTopics(LLMTalkTopicSearchRequest request) {

        BeanOutputConverter<LLMTalkTopicsResponse> outputConverter =
            new BeanOutputConverter<>(LLMTalkTopicsResponse.class);

        OpenAiChatOptions chatOptions = generateChatOptions(outputConverter.getJsonSchema());

        String promptContent = request.toPromptContent();
        Prompt prompt = new Prompt(promptContent, chatOptions);
        ChatResponse response = chatModel.call(prompt);

        return outputConverter.convert(response.getResult().getOutput().getContent());
    }

    @Override
    public LLMTalkTopicDetailResponse generateTalkTopicDetail(LLMTalkTopicDetailRequest request) {

        BeanOutputConverter<LLMTalkTopicDetailResponse> outputConverter =
            new BeanOutputConverter<>(LLMTalkTopicDetailResponse.class);

        OpenAiChatOptions chatOptions = generateChatOptions(outputConverter.getJsonSchema());

        String promptContent = request.toPromptContent();
        Prompt prompt = new Prompt(promptContent, chatOptions);
        ChatResponse response = chatModel.call(prompt);

        return outputConverter.convert(response.getResult().getOutput().getContent());
    }

    private OpenAiChatOptions generateChatOptions(String jsonSchema) {

        return OpenAiChatOptions.builder()
            .withModel(ChatModel.GPT_4_O_MINI)
            .withResponseFormat(new ResponseFormat(Type.JSON_SCHEMA, jsonSchema))
            .build();
    }
}
