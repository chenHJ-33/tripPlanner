package org.example.tripplanner.agent.loader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {
    public String load(String path){
        try (InputStream in=new ClassPathResource(path).getInputStream()){
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("加载Prompt失败:"+path,e);
        }
    }
}
