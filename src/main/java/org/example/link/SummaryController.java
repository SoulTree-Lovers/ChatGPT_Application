package org.example.link;


//import org.example.link.entity.SummaryEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.link.entity.ChatMessage;
import org.example.link.entity.ChatRequest;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

@RestController
public class SummaryController {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public SummaryController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 엔드 포인트 (클라이언트가 request body에 url을 입력)
    @PostMapping("/api/summary")
    public ResponseEntity<String> summarizeUrl(@RequestBody Map<String, String> request) throws InterruptedException {
        String url = request.get("url"); // 입력한 url 저장
        String content = fetchWebContent(url); // url의 p tag를 문자열로 저장
        String summary = getSummaryFromOpenAI(content); // p tag 문자열을 GPT API로 요약
        System.out.println("=======================================================");
        System.out.println(summary);
        return ResponseEntity.ok(summary); // 요약 결과 반환
    }

    // 해당 링크의 p tag를 긁어오기
    private String fetchWebContent(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements allParagraphs = doc.select("p");
            int numParagraphs = Math.min(allParagraphs.size(), 100); // 최대 10개의 <p> 태그 선택
            Elements paragraphs = new Elements(allParagraphs.subList(0, numParagraphs)); // 하위 목록을 새로운 Elements 객체로 변환
            StringBuilder content = new StringBuilder();
            for (Element paragraph : paragraphs) {
                content.append(paragraph.text()).append("\n");
            }
            System.out.println(content);
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching web content.";
        }
    }

    // OPEN AI API로 p tag 내용을 요약함
    private String getSummaryFromOpenAI(String content) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // 요청의 JSON 본문을 생성하는 ChatRequest 객체 생성
        ChatRequest chatRequest = new ChatRequest();
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent(content);
        chatRequest.getMessages().add(message);
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setTemperature(1);
        chatRequest.setMax_tokens(256);
        chatRequest.setTop_p(1);
        chatRequest.setFrequency_penalty(0);
        chatRequest.setPresence_penalty(0);

        // ChatRequest 객체를 JSON으로 직렬화하여 요청 본문 생성
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(chatRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error: Failed to serialize request body";
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> messages = (Map<String, String>) choice.get("message");
            String summary = messages.get("content");
            return summary;
        } else {
            return "Error fetching summary from OpenAI.";
        }
    }
}

