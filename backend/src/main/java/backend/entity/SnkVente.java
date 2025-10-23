package backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tableauventes", schema = "public")
public class SnkVente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") //avoir le bon format de date
    private LocalDate date;

    @Column(name = "nom_item")     private String nomItem;
    @Column(name = "prix_retail")  private BigDecimal prixRetail;
    @Column(name = "prix_resell")  private BigDecimal prixResell;
    @Column(name = "argent_preleve") private String argentPreleve;
    
}
