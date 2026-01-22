package backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import backend.entity.UserStatsLayout;

public interface UserStatsLayoutRepository extends JpaRepository<UserStatsLayout, Long> {
  Optional<UserStatsLayout> findByUserId(Long userId);

  @Transactional
  void deleteByUserId(Long userId);
}
