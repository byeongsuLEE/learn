package com.lbs.user.chat.infrastructure.repository.mongo;

import com.lbs.user.chat.infrastructure.entity.ChatMessageDocument;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MongoChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {

    List<ChatMessageDocument> findByRoomIdOrderByCreatedAtDesc(Long roomId, Limit limit);

    List<ChatMessageDocument> findByRoomIdAndIdLessThanOrderByCreatedAtDesc(Long roomId, String cursor, Limit limit);
}
