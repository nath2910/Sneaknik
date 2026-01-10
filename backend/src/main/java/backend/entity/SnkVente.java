package backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tableauventes", schema = "public")
public class SnkVente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateAchat;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateVente;

  @CreationTimestamp
@Column(name = "created_at", updatable = false, nullable = false)
private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @Column(name = "nom_item")
  private String nomItem;

  @Column(name = "prix_retail")
  private BigDecimal prixRetail;

  @Column(name = "prix_resell")
  private BigDecimal prixResell;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "categorie", length = 60)
  private String categorie;

   

}

