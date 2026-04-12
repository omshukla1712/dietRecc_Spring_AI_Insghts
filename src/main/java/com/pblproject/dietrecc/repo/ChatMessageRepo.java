package com.pblproject.dietrecc.repo;
import java.util.List;
import com.pblproject.dietrecc.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdOrderByTimestampAsc(Long userId);
    List<ChatMessage> findTop5ByUserIdOrderByTimestampDesc(Long userId);
}
