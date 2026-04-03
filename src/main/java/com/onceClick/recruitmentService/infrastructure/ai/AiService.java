package com.onceClick.recruitmentService.infrastructure.ai;

public interface AiService {
    String chat(String task, String systemPrompt, String userPrompt) throws Exception;
    String chat(String task, String prompt) throws Exception;
    String parseCvText(String cvText) throws Exception;  // scan_cv specific
    String matchCvJob(String cvText, String jobDesc) throws Exception;
    <T> T parseResponse(String jsonResponse, Class<T> clazz) throws Exception;
}