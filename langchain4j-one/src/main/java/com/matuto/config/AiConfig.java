package com.matuto.config;

import com.matuto.service.ToolsService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AiConfig
 * @Description TODO
 * @Author Majz
 * @Date 2025/5/27 18:07
 */
@Configuration
public class AiConfig {

    public interface Assistant {
        String chat(String message);
        TokenStream stream(String message);

        @SystemMessage("""
                # 角色
                你是一个功能强大的智能体，能够依据给定的一句话撰写内容丰富的博客文章，同时对这句话进行准确的类型和主题分析，
                并将所有结果以md格式呈现,如果客户告知我要写一遍博文请提示客户给予博文的类型和关键词。
                ## 技能
                ### 技能 1: 撰写博客文章
                1. 仔细分析给定的一句话，以此为核心展开创作。
                2. 运用丰富的知识和语言表达能力，围绕这句话生成一篇逻辑清晰、内容充实的博客文章，字数不少于[X]字（可根据需求设定）。
                ### 技能 2: 进行类型和主题分析
                1. 精准判断给定句子的类型，如陈述句、疑问句、感叹句等。
                2. 深入剖析句子所涉及的主题，明确主题范畴和关键要点。
                3. 将类型和主题分析结果清晰地表述出来。
                ### 技能 3: 生成md格式结果
                1. 将博客文章、类型分析结果、主题分析结果按照md格式的规范进行整理。
                2. 确保md格式排版美观、易读，各部分内容层次分明。
                ## 限制
                - 必须严格依据给定的一句话进行所有操作，不得偏离。
                - 生成的博客文章应符合正常语言逻辑和表达习惯。
                - 类型和主题分析结果要准确合理。
                - 输出结果必须是规范的md格式。
                """)
        TokenStream stream(@UserMessage String message, @V("current_data") String currentData);
    }


    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel,
                               StreamingChatLanguageModel streamingChatLanguageModel
            , ToolsService toolsService) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // 为Assistant动态代理对象
        return AiServices.builder(Assistant.class)
                .tools(toolsService)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(chatMemory)
                .build();
    }


    public interface AssistantUnique{
        String chat(@MemoryId int memoryId,@UserMessage String userMessage);
        TokenStream stream(@MemoryId int memoryId,@UserMessage String userMessage);
    }

    @Bean
    public AssistantUnique assistantUnique(ChatLanguageModel chatLanguageModel,
                                           StreamingChatLanguageModel streamingChatLanguageModel) {
        return AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10)
                        .id(memoryId).build())
                .build();
    }


}
