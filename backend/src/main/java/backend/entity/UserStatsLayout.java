package backend.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "user_stats_layouts", schema = "public")
public class UserStatsLayout {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "layout_json", columnDefinition = "text", nullable = false)
  private String layoutJson;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  public UserStatsLayout() {}

  public Long getId() { return id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getLayoutJson() { return layoutJson; }
  public void setLayoutJson(String layoutJson) { this.layoutJson = layoutJson; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
