package org.example.link.domain.summary.controller;


//import org.example.link.domain.summary.repository.SummaryEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.link.domain.summary.controller.dto.SummaryRequest;
import org.example.link.domain.summary.controller.dto.SummaryResponse;
import org.example.link.domain.summary.service.SummaryService;
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
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    // 엔드 포인트 (클라이언트가 request body에 url을 입력)
    @PostMapping("/api/summary")
    public ResponseEntity<SummaryResponse> summarizeUrl(
        @RequestBody SummaryRequest request
    ) {
        String url = request.getUrl(); // 입력한 url 저장
        String content = summaryService.fetchWebContent(url); // url의 p tag를 문자열로 저장
        SummaryResponse summaryResponse = summaryService.getSummaryFromOpenAI(url, content); // p tag 문자열을 GPT API로 요약
        System.out.println("=======================================================");
        System.out.println(summaryResponse.getSummaryContent());
        return ResponseEntity.ok(summaryResponse); // 요약 결과 반환
    }


}

