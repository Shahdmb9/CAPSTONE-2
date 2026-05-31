package org.example.capstone2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class OpenAiService {


    private final ChatClient chatClient;
    public OpenAiService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    public Integer chat(String title) {
        String prompt = title+" comes under which category :" +
                " 1-Plumbing," +
                " 2-Electrical ," +
                " 3-HVAC Maintenance," +
                " 4-General Maintenance" +
                " answer with the number only with out the name. if you cant find the category just say 0";
        return Integer.parseInt(chatClient.prompt(prompt).call().content());
    }

    public String Ai(String prompt) {
        String prompt1 =chatClient.prompt(prompt).call().content();
        return prompt1;
    }
}
