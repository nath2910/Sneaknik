package backend.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.entity.UserStatsLayout;
import backend.repository.UserRepository;
import backend.repository.UserStatsLayoutRepository;

@Service
public class StatsLayoutService {

  private final UserStatsLayoutRepository layoutRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public StatsLayoutService(
      UserStatsLayoutRepository layoutRepository,
      UserRepository userRepository,
      ObjectMapper objectMapper
  ) {
    this.layoutRepository = layoutRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  public JsonNode getLayout(Long userId) {
    return layoutRepository.findByUserId(userId)
        .map(UserStatsLayout::getLayoutJson)
        .map(this::toJson)
        .orElse(null);
  }

  public void saveLayout(Long userId, JsonNode layout) {
    if (layout == null || layout.isNull()) {
      layoutRepository.deleteByUserId(userId);
      return;
    }

    UserStatsLayout entity = layoutRepository.findByUserId(userId)
        .orElseGet(UserStatsLayout::new);

    entity.setUser(userRepository.getReferenceById(userId));
    entity.setLayoutJson(layout.toString());
    entity.setUpdatedAt(Instant.now());
    layoutRepository.save(entity);
  }

  private JsonNode toJson(String raw) {
    try {
      return objectMapper.readTree(raw);
    } catch (Exception ex) {
      return null;
    }
  }
}
