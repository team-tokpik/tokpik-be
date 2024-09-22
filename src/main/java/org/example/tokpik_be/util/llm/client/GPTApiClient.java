package org.example.tokpik_be.util.llm.client;

import java.util.List;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicSearchResponse;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
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
public class GPTApiClient implements LLMApiClient {

    private final OpenAiChatModel chatModel;
    private final BeanOutputConverter<LLMTalkTopicsResponse> outputConverter;
    private final OpenAiChatOptions chatOptions;

    public GPTApiClient(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        this.outputConverter = new BeanOutputConverter<>(LLMTalkTopicsResponse.class);

        String jsonSchema = this.outputConverter.getJsonSchema();
        this.chatOptions = OpenAiChatOptions.builder()
            .withModel(ChatModel.GPT_4_O_MINI)
            .withResponseFormat(new ResponseFormat(Type.JSON_SCHEMA, jsonSchema))
            .build();
    }

    @Override
    public List<TalkTopicSearchResponse> searchTalkTopics(LLMTalkTopicSearchRequest request) {

        String promptContent = request.toPromptContent();

        Prompt prompt = new Prompt(promptContent, this.chatOptions);
        ChatResponse response = chatModel.call(prompt);
        LLMTalkTopicsResponse talkTopicsResponse = outputConverter
            .convert(response.getResult().getOutput().getContent());

        return talkTopicsResponse.responses();
    }
}
