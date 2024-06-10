package org.example.link.domain.summary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.link.domain.summary.controller.dto.SummaryResponse;
import org.example.link.domain.summary.converter.SummaryConverter;
import org.example.link.domain.summary.repository.SummaryRepository;
import org.example.link.entity.ChatMessage;
import org.example.link.entity.ChatRequest;
import org.example.link.domain.summary.repository.SummaryEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final SummaryRepository summaryRepository;

    private final SummaryConverter summaryConverter;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;


    public SummaryEntity saveSummary(String url, String summary) {

        return summaryRepository.save(SummaryEntity.builder()
            .url(url)
            .summary(summary)
            .build());

    }

    public List<SummaryEntity> getAllSummaries() {
        return summaryRepository.findAll();
    }

    // 해당 링크의 p tag를 긁어오기
    public String fetchWebContent(String url) {
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
    public SummaryResponse getSummaryFromOpenAI(String url, String content) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // 요청의 JSON 본문을 생성하는 ChatRequest 객체 생성
        ChatRequest chatRequest = new ChatRequest();

        ChatMessage message1 = ChatMessage.builder()
            .role("system")
            .content("You will receive sentences in various languages, and you can answer in Korean. Please keep your answer in about 1000 characters.")
            .build();

        ChatMessage message2 = ChatMessage.builder()
            .role("user")
            .content(content)
            .build();



        chatRequest.getMessages().add(message1);
        chatRequest.getMessages().add(message2);


        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setTemperature(1);
        chatRequest.setMax_tokens(1024);
        chatRequest.setTop_p(1);
        chatRequest.setFrequency_penalty(0);
        chatRequest.setPresence_penalty(0);


        // ChatRequest 객체를 JSON으로 직렬화하여 요청 본문 생성
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(chatRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Failed to serialize request body");
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

            log.info("choices: {}", choices);

            Map<String, Object> choice = choices.get(0);
            Map<String, String> messages = (Map<String, String>) choice.get("message");

            return summaryConverter.toSummaryResponse(
                summaryRepository.save(SummaryEntity.builder()
                    .url(url)
                    .summary(messages.get("content"))
                    .build())
            );

        } else {
            throw new RuntimeException("Error fetching summary from OpenAI.");
        }
    }
}
