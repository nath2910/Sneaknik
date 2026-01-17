package backend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.dto.SnkVenteCreateDto;
import backend.dto.SnkVenteImportDto;
import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;
import backend.entity.User;
import backend.repository.SnkVenteRepository.BrandCount;
import backend.service.snkVenteService;

@RestController
@RequestMapping(path = "snkVente")
public class snkVenteController {

  private final snkVenteService snkVenteService;

  public snkVenteController(snkVenteService snkVenteService) {
    this.snkVenteService = snkVenteService;
  }

  // === CRÉATION GLOBALE ===
@ResponseStatus(HttpStatus.CREATED)
@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public SnkVente creer(
    @AuthenticationPrincipal User currentUser,
    @RequestBody SnkVenteCreateDto dto
) {
  return snkVenteService.creer(currentUser.getId(), dto);
}

  // === LECTURE D'UNE VENTE PAR ID (sécurisée par user) ===
  @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
  public SnkVente lire(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Integer id
  ) {
    Long userId =  currentUser.getId();
    return snkVenteService.lire(userId, id);
  }

  // 🔹 liste complète pour le user connecté
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<SnkVente> rechercher(@AuthenticationPrincipal User currentUser) {
    Long userId =  currentUser.getId();
    return snkVenteService.rechercherParUser(userId);
  }

  // 🔹 dernières ventes pour le user connecté (par défaut 7)
@GetMapping("/recent")
public List<SnkVente> getDernieresVentes(
    @AuthenticationPrincipal User currentUser,
    @RequestParam(defaultValue = "7") int limit
) {
  Long userId = currentUser.getId(); // ✅ pas de cast bizarre
  return snkVenteService.getDernieresVentesParUser(userId, limit);
}
@PostMapping("/add")
@ResponseStatus(HttpStatus.CREATED)
public void ajouterPaire(@AuthenticationPrincipal User currentUser,
                         @RequestBody SnkVenteCreateDto dto) {
  snkVenteService.creer(currentUser.getId(), dto);
}
  // === TOTAL BÉNÉFICE ===
  @GetMapping("/total")
  public BigDecimal total(
      @AuthenticationPrincipal User currentUser,
      @RequestParam(required = false) Integer year
  ) {
    Long userId =  currentUser.getId();
    if (year != null) return snkVenteService.totalBenefYear(userId, year);
    return snkVenteService.totalBenef(userId);
  }

  // === CA ===
  @GetMapping("/ca")
  public BigDecimal sumPrixResell(@AuthenticationPrincipal User currentUser) {
    Long userId = currentUser.getId();
    return snkVenteService.sumPrixResell(userId);
  }

  // === GRAPHE PAR MARQUE ===
  @GetMapping("/marque")
  public List<BrandCount> marque(@AuthenticationPrincipal User currentUser) {
    Long userId = currentUser.getId();
    return snkVenteService.graphMarque(userId);
  }

  // === DELETE SÉCURISÉE ===
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVente(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Integer id
  ) {
    Long userId =  currentUser.getId();
    snkVenteService.deleteVente(userId, id);
  }

  @GetMapping("/topVentes")
  public List<TopVenteProjection> topVentes(@AuthenticationPrincipal User currentUser) {
    Long userId = currentUser.getId();
    return snkVenteService.getTop3VentesAnneeCourante(userId);
  }

  @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public SnkVente update(
    @AuthenticationPrincipal User currentUser,
    @PathVariable Integer id,
    @RequestBody SnkVente payload
) {
  Long userId =  currentUser.getId();
  return snkVenteService.updateVente(userId, id, payload);
}

@PostMapping(
  path = "/import",
  consumes = APPLICATION_JSON_VALUE,
  produces = APPLICATION_JSON_VALUE
)
public ResponseEntity<Map<String, Integer>> importCsv(
    @AuthenticationPrincipal User currentUser,
    @RequestBody List<SnkVenteImportDto> items
) {
  if (currentUser == null) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("created", 0));
  }

  int created = snkVenteService.importBulk(currentUser.getId(), items);
  return ResponseEntity.ok(Map.of("created", created));
}




}
