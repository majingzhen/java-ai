package com.matuto.controller;

import com.matuto.config.AiConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author Majz
 * @Date 2025/5/27 17:47
 */
@RestController
@RequestMapping("/ai")
public class TestController {

    @Autowired
    private QwenChatModel qwenChatModel;

    @Autowired
    private QwenStreamingChatModel streamingChatModel;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        return qwenChatModel.chat(message);
    }

    @GetMapping(path = "/stream",produces = "text/stream;charset=UTF-8")
    public Flux<String> streamingChat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        return Flux.create(fluxSink -> {
            streamingChatModel.chat(message, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String s) {
                    fluxSink.next(s);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    fluxSink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    fluxSink.error(throwable);
                }
            });
        });
    }

    @Autowired
    private AiConfig.Assistant assistant;

    @GetMapping("/assistantChat")
    public String assistantChat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        return assistant.chat(message);
    }

    @GetMapping(path = "/assistantStream",produces = "text/stream;charset=UTF-8")
    public Flux<String> assistantStreamingChat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        TokenStream stream = assistant.stream(message, LocalDate.now().toString());
        return Flux.create(fluxSink -> {
            stream.onPartialResponse(fluxSink::next)
                    .onCompleteResponse(chatResponse -> fluxSink.complete())
                    .onError(fluxSink::error)
                    .start();
        });
    }

    @Autowired
    AiConfig.AssistantUnique assistantUnique;

    @GetMapping("/assistantUniqueChat")
    public String assistantUniqueChat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        return assistantUnique.chat(1, message);
    }

    @GetMapping(path = "/assistantUniqueStream",produces = "text/stream;charset=UTF-8")
    public Flux<String> assistantUniqueStreamingChat(@RequestParam(value = "message",defaultValue = "你好") String message) {
        TokenStream stream = assistantUnique.stream(2, message);
        return Flux.create(fluxSink -> {
            stream.onPartialResponse(fluxSink::next)
                    .onCompleteResponse(chatResponse -> fluxSink.complete())
                    .onError(fluxSink::error)
                    .start();
        });
    }
}
