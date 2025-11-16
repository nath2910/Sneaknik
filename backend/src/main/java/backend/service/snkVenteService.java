package backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import backend.entity.SnkVente;
import backend.repository.SnkVenteRepository;
import backend.repository.SnkVenteRepository.BrandCount;
import jakarta.transaction.Transactional;

@Service

public class snkVenteService {

    
    private SnkVenteRepository snkVenteRepository;

  // logique metier

    public snkVenteService(SnkVenteRepository snkVenteRepository) {
      this.snkVenteRepository = snkVenteRepository;
    }

    public void creer(SnkVente snkVente){
        this.snkVenteRepository.save(snkVente);
    }

    public List<SnkVente> rechercher(){
      return this.snkVenteRepository.findAll();
    }

    public SnkVente lire(int id) {
      Optional<SnkVente> optionalSnkVente =  this.snkVenteRepository.findById(id); // snk peut exister ou pas du tout
      return optionalSnkVente.orElse(null);
    }
      public List<SnkVente> get10DernieresVentes() {
        return snkVenteRepository.findTop10ByOrderByIdDesc();
      }

      public void ajouterPaire(SnkVente s){
         snkVenteRepository.save(s);
      }
       public BigDecimal totalBenef() {
    return snkVenteRepository.totalBenef();
  }

  public BigDecimal totalBenefYear(int year) {
    return snkVenteRepository.totalBenefYear(year); // réutilise la default method
  }

   public BigDecimal sumPrixResell() {
    return snkVenteRepository.sumPrixResell(); // réutilise la default method
  }
  public List<BrandCount> graphMarque() {
    return snkVenteRepository.graphMarque(); // réutilise la default method
  }
  @Transactional
  public void deleteVente(Long id) {
        snkVenteRepository.deleteById(id);
    }
 }



