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

import java.util.*;
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

    @Value("${ai.api.model}")
    private String modelId;


    public DietAiService(FoodRepo foodRepo, DietPlanRepo dietPlanRepo, ChatMessageRepo chatMessageRepo) {
        this.foodRepo = foodRepo;
        this.dietPlanRepo = dietPlanRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.restTemplate = new RestTemplate();
    }

    public String generateDietPlan(User user, int targetCalories) {
        List<FoodItem> availableFoods = foodRepo.findAll();
        StringBuilder foodList = new StringBuilder();
        for (FoodItem item : availableFoods) {
            foodList.append(item.getName()).append(", ");
        }
        List<ChatMessage> recentChats = chatMessageRepo.findTop5ByUserIdOrderByTimestampDesc(user.getId());
        Collections.reverse(recentChats);

        StringBuilder chatContext = new StringBuilder();
        if (!recentChats.isEmpty()) {
            chatContext.append("CRUCIAL CONTEXT - The user recently mentioned the following preferences in chat:\n");
            for (ChatMessage msg : recentChats) {
                // We only care about what the USER said to extract their preferences
                if (msg.getRole().equals("user")) {
                    chatContext.append("- ").append(msg.getContent()).append("\n");
                }
            }
            chatContext.append("You MUST strictly respect any dietary restrictions, preferences, or requests mentioned in the context above when creating this new plan.\n\n");
        }
        String prompt = "Create a daily diet plan targeting " + targetCalories + " calories for a " +
                user.getAge() + " year old " + user.getGender() + " weighing " + user.getWeight() + "kg. " +
                "Their primary goal is " + user.getGoal() + ".\n\n" +

                chatContext.toString() + // <-- INJECT THE CONTEXT HERE

                "Constraint: You MUST ONLY use the following available foods: " + foodList.toString();

        String generatedPlanText = callGroqApi(prompt);


        DietPlan newPlan = new DietPlan();
        newPlan.setUser(user);
        newPlan.setTargetCalories(targetCalories);
        newPlan.setPlanDetails(generatedPlanText);
        dietPlanRepo.save(newPlan);

        return generatedPlanText;
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

    private String callGroqApiWithHistory(List<Map<String, String>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // .trim() removes any accidental spaces from application.properties
        String cleanModelId = modelId.trim();

        Map<String, Object> requestBody = Map.of(
                "model", cleanModelId,
                "messages", messages
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Using postForEntity and Map.class, exactly like your Expense project
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // Extracting the response cleanly without ObjectMapper
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI: " + e.getMessage();
        }
    }
    private String callGroqApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // .trim() removes any accidental spaces
        String cleanModelId = modelId.trim();

        Map<String, Object> requestBody = Map.of(
                "model", cleanModelId,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt) // Single prompt format
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI: " + e.getMessage();
        }
    }
}