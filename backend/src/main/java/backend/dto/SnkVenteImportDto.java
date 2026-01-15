package backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnkVenteImportDto {
  private String nomItem;
  private BigDecimal prixRetail;
  private BigDecimal prixResell;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateAchat;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateVente;

  private String description;
  private String categorie;

}
