package com.pblproject.dietrecc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblproject.dietrecc.model.ChatMessage;
import com.pblproject.dietrecc.model.DietPlan;
import com.pblproject.dietrecc.model.FoodItem;
import com.pblproject.dietrecc.model.User;
import com.pblproject.dietrecc.repo.ChatMessageRepo;
import com.pblproject.dietrecc.repo.DietPlanRepo;
import com.pblproject.dietrecc.repo.FoodRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DietAiService {

    private final FoodRepo foodRepo;
    private final DietPlanRepo dietPlanRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    public DietAiService(FoodRepo foodRepo, DietPlanRepo dietPlanRepo, ChatMessageRepo chatMessageRepo) {
        this.foodRepo = foodRepo;
        this.dietPlanRepo = dietPlanRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.restTemplate = new RestTemplate();
    }

    public String generateDietPlan(User user, int targetCalories) {
        List<FoodItem> availableFoods = foodRepo.findAll();
        String foodList = availableFoods.stream()
                .map(food -> food.getName() + " (" + food.getCalories() + " cal)")
                .collect(Collectors.joining(", "));
        String prompt = String.format(
                "You are a nutritionist. Create a 1-day meal plan for a %d-year-old %s whose goal is %s. " +
                        "Daily target: exactly %d calories. " +
                        "RESTRICTION: You MUST ONLY use these foods: [%s]. " +
                        "Format clearly with Breakfast, Lunch, Dinner, and Snacks.",
                user.getAge(), user.getGender(), user.getGoal(), targetCalories, foodList
        );

        String aiResponse = callGroqApi(prompt);

        if (aiResponse != null && !aiResponse.startsWith("Error")) {
            DietPlan newPlan = new DietPlan(user, aiResponse, targetCalories, user.getGoal().toString());
            dietPlanRepo.save(newPlan);
        }

        return aiResponse;
    }

    public String chatWithAi(User user, String userMessage) {
        ChatMessage userMsg = new ChatMessage(user, "user", userMessage);
        chatMessageRepo.save(userMsg);
        List<ChatMessage> history = chatMessageRepo.findByUserIdOrderByTimestampAsc(user.getId());
        String foodList = foodRepo.findAll().stream()
                .map(FoodItem::getName).collect(Collectors.joining(", "));

        String systemInstruction = String.format(
                "You are a helpful AI Nutritionist assisting a %d-year-old %s. Goal: %s. " +
                        "Available foods in their kitchen: [%s]. " +
                        "Keep answers concise, helpful, and motivating.",
                user.getAge(), user.getGender(), user.getGoal(), foodList
        );

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemInstruction));

        for (ChatMessage msg : history) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }
        String aiReply = callGroqApiWithHistory(messages);
        if (aiReply != null && !aiReply.startsWith("Error")) {
            ChatMessage aiMsg = new ChatMessage(user, "assistant", aiReply);
            chatMessageRepo.save(aiMsg);
        }
        return aiReply;
    }

    private String callGroqApi(String prompt) {
        List<Map<String, String>> messages = List.of(Map.of("role", "user", "content", prompt));
        return callGroqApiWithHistory(messages);
    }

    private String callGroqApiWithHistory(List<Map<String, String>> messages) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.has("choices") && root.get("choices").size() > 0) {
                return root.get("choices").get(0).get("message").get("content").asText();
            } else {
                return "Error: AI returned empty response.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI: " + e.getMessage();
        }
    }
}