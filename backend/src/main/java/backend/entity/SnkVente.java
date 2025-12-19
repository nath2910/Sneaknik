package backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import backend.entity.User;

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
    private Integer id; // clé primaire
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAchat;
    private LocalDate dateVente;

    @ManyToOne(fetch = FetchType.LAZY)  //clé étrangère
    @JoinColumn(name = "user_id")
    @JsonIgnore                
    private User user;

  
  

    // ==== TES CHAMPS EXISTANTS  ===

    @Column(name = "nom_item")     private String nomItem;
    @Column(name = "prix_retail")  private BigDecimal prixRetail;
    @Column(name = "prix_resell")  private BigDecimal prixResell;
    @Column(name ="description")   private String description;
    @Column(name ="categorie")   private String categorie;
   
    




    // ====== GETTERS / SETTERS ======

    
    public String nomItem() {
        return nomItem;
    }

      public BigDecimal prixRetail() {
        return prixRetail;
    }
    
    public BigDecimal prixResell() {
        return prixResell;
    }
     public void setprixResell(BigDecimal prixResell) {
        this.prixResell = prixResell;
    }

    public void setprixRetail(BigDecimal prixRetail) {
        this.prixRetail = prixRetail;
    }



    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}
}

