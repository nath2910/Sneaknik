package backend.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public record SnkVenteCreateDto(
  String nomItem,
  BigDecimal prixRetail,
  BigDecimal prixResell,
  LocalDate dateAchat,
  LocalDate dateVente,
  String description,
  String categorie
) {}
