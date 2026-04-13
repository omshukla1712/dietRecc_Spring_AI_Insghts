package com.pblproject.dietrecc.controller;

import com.pblproject.dietrecc.model.ChatMessage;
import com.pblproject.dietrecc.model.User;
import com.pblproject.dietrecc.repo.ChatMessageRepo;
import com.pblproject.dietrecc.repo.UserRepo;
import com.pblproject.dietrecc.service.DietAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final DietAiService aiService;
    private final UserRepo userRepo;
    private final ChatMessageRepo chatMessageRepo;

    public ChatController(DietAiService aiService, UserRepo userRepo, ChatMessageRepo chatMessageRepo) {
        this.aiService = aiService;
        this.userRepo = userRepo;
        this.chatMessageRepo = chatMessageRepo;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String message = payload.get("message");

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage aiMsg = aiService.chatWithAi(user, message);

        // Return BOTH the text and the database ID
        return ResponseEntity.ok(Map.of(
                "response", aiMsg.getContent(),
                "messageId", aiMsg.getId()
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // FIXED: Sort Ascending so the conversation reads chronologically!
        return ResponseEntity.ok(chatMessageRepo.findByUserIdOrderByTimestampAsc(user.getId()));
    }
}