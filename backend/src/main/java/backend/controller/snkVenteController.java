package backend.controller;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import backend.entity.SnkVente;
import backend.repository.SnkVenteRepository;
import backend.repository.SnkVenteRepository.BrandCount;
import backend.service.snkVenteService;

@RestController //on fait du CRUD ( Creat, Read, Update, Delete), rest façon unique de representer des donne http
@RequestMapping(path = "snkVente"  ) //permet de construire le path
@CrossOrigin("http://localhost:5173/")

public class snkVenteController {
    
      private snkVenteService snkVenteService;

      //faire des routes bien disctinct en focntion de ce que je veux soit avec un chemin (/recent ...)
      //soit avec variable, soit rien etc


      public snkVenteController(backend.service.snkVenteService snkVenteService) {
            this.snkVenteService = snkVenteService;
      }

      @ResponseStatus(value = HttpStatus.CREATED) // genere le code 200, 404 etc
      @PostMapping( consumes = APPLICATION_JSON_VALUE) //pas en haut car que dans le post qu'on consome 
      public void creer(@RequestBody SnkVente snkVente){
            this.snkVenteService.creer(snkVente);
      }

      @GetMapping(produces = APPLICATION_JSON_VALUE) // produit pour creation (ici liste) ou consume et le type de donne ici JSON 
      public List<SnkVente> rechercher(){
            return this.snkVenteService.rechercher();
      }

      @GetMapping(path="{id}", produces = APPLICATION_JSON_VALUE) // avec { } spring sait que c'est une varibale
      public SnkVente lire(@PathVariable int id){
            return this.snkVenteService.lire(id);
      }

      @GetMapping("/recent")
      public List<SnkVente> getDernieresVentes() {
        return snkVenteService.get10DernieresVentes();
    }
     
    @PostMapping("/add")
      public void ajouterPaire(@RequestBody SnkVente s){
            snkVenteService.ajouterPaire(s);
      }
       @GetMapping("/total")
  public BigDecimal total(@RequestParam(required = false) Integer year) {
    if (year != null) return snkVenteService.totalBenefYear(year);
    return snkVenteService.totalBenef();
  }
  @GetMapping("/ca")
  public BigDecimal sumPrixResell() {
     return snkVenteService.sumPrixResell();

  }
   @GetMapping("/marque")
  public List<BrandCount> marque() {
     return snkVenteService.graphMarque();

  }
}
    
      

