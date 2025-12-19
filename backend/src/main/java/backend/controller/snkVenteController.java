package backend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;
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
      @RequestHeader("X-USER-ID") Integer userId,
      @RequestBody SnkVente snkVente
  ){
        this.snkVenteService.creer(userId, snkVente);
  }

  

  // === LECTURE D'UNE VENTE PAR ID (sécurisée par user) ===
  @GetMapping(path="{id}", produces = APPLICATION_JSON_VALUE)
  public SnkVente lire(
      @RequestHeader("X-USER-ID") Integer userId,
      @PathVariable Integer id
  ){
        return this.snkVenteService.lire(userId, id);
  }

  // 🔹 liste complète pour le user connecté
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<SnkVente> rechercher(@RequestHeader("X-USER-ID") Integer userId){
      return this.snkVenteService.rechercherParUser(userId);
  }

  // 🔹 10 dernières pour le user connecté
  @GetMapping("/recent")
  public List<SnkVente> getDernieresVentes(@RequestHeader("X-USER-ID") Integer userId) {
      return snkVenteService.get10DernieresVentesParUser(userId);
  }
  // === AJOUT PAIRE (via ton bouton/forme AjoutPaire) ===
  @PostMapping("/add")
  public void ajouterPaire(
      @RequestHeader("X-USER-ID") Integer userId,
      @RequestBody SnkVente s
  ){
        snkVenteService.ajouterPaire(userId, s);
  }

  // === TOTAL BÉNÉFICE ===
  @GetMapping("/total")
  public BigDecimal total(
      @RequestHeader("X-USER-ID") Integer userId,
      @RequestParam(required = false) Integer year
  ) {
    if (year != null) return snkVenteService.totalBenefYear(userId, year);
    return snkVenteService.totalBenef(userId);
  }

  // === CA ===
  @GetMapping("/ca")
  public BigDecimal sumPrixResell(@RequestHeader("X-USER-ID") Integer userId) {
     return snkVenteService.sumPrixResell(userId);
  }

  // === GRAPHE PAR MARQUE ===
  @GetMapping("/marque")
  public List<BrandCount> marque(@RequestHeader("X-USER-ID") Integer userId) {
     return snkVenteService.graphMarque(userId);
  }

  // === DELETE SÉCURISÉE ===
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVente(
      @RequestHeader("X-USER-ID") Integer userId,
      @PathVariable Integer id
  ) {
      snkVenteService.deleteVente(userId, id);
  }

 @GetMapping("/topVentes")
public List<TopVenteProjection> topVentes(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        return snkVenteService.getTop3VentesAnneeCourante(userId);
    }


}
