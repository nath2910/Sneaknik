package backend.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;
import backend.entity.User;
import backend.repository.SnkVenteRepository;
import backend.repository.SnkVenteRepository.BrandCount;
import backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class snkVenteService {

    private final SnkVenteRepository snkVenteRepository;
    private final UserRepository userRepository;

    public snkVenteService(SnkVenteRepository snkVenteRepository,
                           UserRepository userRepository) {
        this.snkVenteRepository = snkVenteRepository;
        this.userRepository = userRepository;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public void creer(Long userId, SnkVente snkVente){
        User user = getUserOrThrow(userId);
        snkVente.setUser(user);
        snkVenteRepository.save(snkVente);
    }



  public List<SnkVente> rechercherParUser(Integer userId) {
        return snkVenteRepository.findByUser_IdOrderByDateAchatDesc(userId);
    }
    public SnkVente lire(Integer userId, Integer id) {
        return snkVenteRepository.findById(id)
                .filter(v -> v.getUser() != null && v.getUser().getId().equals(userId))
                .orElse(null);
    }

    public List<SnkVente> get10DernieresVentesParUser(Integer userId) {
        return snkVenteRepository.findTop10ByUser_IdOrderByDateAchatDesc(userId);
    }
    public void ajouterPaire(Long userId, SnkVente s){
        User user = getUserOrThrow(userId);
        s.setUser(user);
        snkVenteRepository.save(s);
    }

    public BigDecimal totalBenef(Integer userId) {
        return snkVenteRepository.totalBenef(userId);
    }

    public BigDecimal totalBenefYear(Integer userId, int year) {
        return snkVenteRepository.totalBenefYear(userId, year);
    }

    public BigDecimal sumPrixResell(Integer userId) {
        return snkVenteRepository.sumPrixResell(userId);
    }

    public List<BrandCount> graphMarque(Integer userId) {
        return snkVenteRepository.graphMarque(userId);
    }

    @Transactional
    public void deleteVente(Integer userId, Integer id) {
        snkVenteRepository.deleteByIdAndUserId(id, userId);
    }
     public List<TopVenteProjection> getTop3VentesAnneeCourante(Integer userId) {
        int currentYear = java.time.Year.now().getValue();
        
        return snkVenteRepository.topVentesYear(userId, currentYear)
                .stream()
                .limit(3)   // 👉 top 3 ici
                .toList();
    }
    

@Transactional
public SnkVente updateVente(Integer userId, Integer id, SnkVente payload) {

  SnkVente existing = snkVenteRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vente introuvable"));

  // sécurité : la vente appartient-elle à l'utilisateur ?
  if (existing.getUser() == null || existing.getUser().getId().intValue() != userId) {
  throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit");
}

  // update champs (on garde le user)
  existing.setNomItem(payload.getNomItem());
  existing.setprixRetail(payload.getPrixRetail());
  existing.setprixResell(payload.getPrixResell());
  existing.setDateAchat(payload.getDateAchat());
  existing.setDateVente(payload.getDateVente());
  existing.setDescription(payload.getDescription());
  existing.setCategorie(payload.getCategorie());

  return snkVenteRepository.save(existing);
}

    
}


