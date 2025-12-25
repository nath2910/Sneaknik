package backend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;
import backend.entity.User;
import backend.repository.SnkVenteRepository.BrandCount;
import backend.service.snkVenteService;

@RestController
@RequestMapping(path = "snkVente")
@CrossOrigin(origins = {
  "http://localhost:5173",
  "https://sneaknik-2.onrender.com"
})
public class snkVenteController {

  private final snkVenteService snkVenteService;

  public snkVenteController(snkVenteService snkVenteService) {
    this.snkVenteService = snkVenteService;
  }

  // === CRÉATION GLOBALE ===
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public void creer(
      @AuthenticationPrincipal User currentUser,
      @RequestBody SnkVente snkVente
  ) {
    Long userId = currentUser.getId();
    snkVenteService.creer(userId, snkVente);
  }

  // === LECTURE D'UNE VENTE PAR ID (sécurisée par user) ===
  @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
  public SnkVente lire(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Integer id
  ) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.lire(userId, id);
  }

  // 🔹 liste complète pour le user connecté
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<SnkVente> rechercher(@AuthenticationPrincipal User currentUser) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.rechercherParUser(userId);
  }

  // 🔹 10 dernières pour le user connecté
  @GetMapping("/recent")
  public List<SnkVente> getDernieresVentes(@AuthenticationPrincipal User currentUser) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.get10DernieresVentesParUser(userId);
  }

  // === AJOUT PAIRE (via ton bouton/forme AjoutPaire) ===
  @PostMapping("/add")
  public void ajouterPaire(
      @AuthenticationPrincipal User currentUser,
      @RequestBody SnkVente s
  ) {
    Long userId = currentUser.getId();
    snkVenteService.ajouterPaire(userId, s);
  }

  // === TOTAL BÉNÉFICE ===
  @GetMapping("/total")
  public BigDecimal total(
      @AuthenticationPrincipal User currentUser,
      @RequestParam(required = false) Integer year
  ) {
    Integer userId = currentUser.getId().intValue();
    if (year != null) return snkVenteService.totalBenefYear(userId, year);
    return snkVenteService.totalBenef(userId);
  }

  // === CA ===
  @GetMapping("/ca")
  public BigDecimal sumPrixResell(@AuthenticationPrincipal User currentUser) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.sumPrixResell(userId);
  }

  // === GRAPHE PAR MARQUE ===
  @GetMapping("/marque")
  public List<BrandCount> marque(@AuthenticationPrincipal User currentUser) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.graphMarque(userId);
  }

  // === DELETE SÉCURISÉE ===
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVente(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Integer id
  ) {
    Integer userId = currentUser.getId().intValue();
    snkVenteService.deleteVente(userId, id);
  }

  @GetMapping("/topVentes")
  public List<TopVenteProjection> topVentes(@AuthenticationPrincipal User currentUser) {
    Integer userId = currentUser.getId().intValue();
    return snkVenteService.getTop3VentesAnneeCourante(userId);
  }

  @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public SnkVente update(
    @AuthenticationPrincipal User currentUser,
    @PathVariable Integer id,
    @RequestBody SnkVente payload
) {
  Integer userId = currentUser.getId().intValue();
  return snkVenteService.updateVente(userId, id, payload);
}

}
