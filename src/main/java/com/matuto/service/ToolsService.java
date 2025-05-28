package com.matuto.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

/**
 * @ClassName ToolsService
 * @Description TODO
 * @Author Majz
 * @Date 2025/5/28 20:24
 */
@Service
public class ToolsService {

    @Tool("有多少个名字的")
    public Integer getNum(@P("名字") String name) {
        System.out.println("调用工具类：" + name);
        return 10;
    }
}
