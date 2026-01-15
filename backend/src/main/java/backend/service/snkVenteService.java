package backend.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.dto.SnkVenteImportDto;
import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;
import backend.entity.User;
import backend.repository.SnkVenteRepository;
import backend.repository.SnkVenteRepository.BrandCount;
import backend.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class snkVenteService {

  private final SnkVenteRepository snkVenteRepository;
  private final UserRepository userRepository;

  public snkVenteService(SnkVenteRepository snkVenteRepository, UserRepository userRepository) {
    this.snkVenteRepository = snkVenteRepository;
    this.userRepository = userRepository;
  }

  private User getUserOrThrow(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
  }

  public void creer(Long userId, SnkVente snkVente) {
    User user = getUserOrThrow(userId);
    snkVente.setUser(user);
    snkVenteRepository.save(snkVente);
  }

  public List<SnkVente> rechercherParUser(Long userId) {
    return snkVenteRepository.findByUser_IdOrderByDateAchatDesc(userId);
  }

  public SnkVente lire(Long userId, Integer id) {
    return snkVenteRepository.findById(id)
        .filter(v -> v.getUser() != null && userId.equals(v.getUser().getId()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vente introuvable"));
  }

 public List<SnkVente> getDernieresVentesParUser(Long userId, int limit) {
  int safeLimit = Math.min(Math.max(limit, 1), 50); // limite 1..50 (sécurité)
  return snkVenteRepository.findByUser_IdOrderByCreatedAtDesc(userId, PageRequest.of(0, safeLimit));
}


  public void ajouterPaire(Long userId, SnkVente s) {
    User user = getUserOrThrow(userId);
    s.setUser(user);
    snkVenteRepository.save(s);
  }

  public BigDecimal totalBenef(Long userId) {
    return snkVenteRepository.totalBenef(userId);
  }

  public BigDecimal totalBenefYear(Long userId, int year) {
    return snkVenteRepository.totalBenefYear(userId, year);
  }

  public BigDecimal sumPrixResell(Long userId) {
    return snkVenteRepository.sumPrixResell(userId);
  }

  public List<BrandCount> graphMarque(Long userId) {
    return snkVenteRepository.graphMarque(userId);
  }

  @Transactional
  public void deleteVente(Long userId, Integer id) {
    snkVenteRepository.deleteByIdAndUser_Id(id, userId);
  }

  public List<TopVenteProjection> getTop3VentesAnneeCourante(Long userId) {
    int currentYear = Year.now().getValue();
    return snkVenteRepository.topVentesYear(userId, currentYear).stream().limit(3).toList();
  }

  @Transactional
  public SnkVente updateVente(Long userId, Integer id, SnkVente payload) {
    SnkVente existing = snkVenteRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vente introuvable"));

    if (existing.getUser() == null || !userId.equals(existing.getUser().getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit");
    }

    existing.setNomItem(payload.getNomItem());
    existing.setPrixRetail(payload.getPrixRetail());
    existing.setPrixResell(payload.getPrixResell());
    existing.setDateAchat(payload.getDateAchat());
    existing.setDateVente(payload.getDateVente());
    existing.setDescription(payload.getDescription());
    existing.setCategorie(payload.getCategorie());

    return snkVenteRepository.save(existing);
  }
  // stat par categorie
  public List<SnkVenteRepository.LabelCount> topCategories(Long userId) {
  return snkVenteRepository.topCategories(userId, PageRequest.of(0, 10));
}

public List<SnkVenteRepository.LabelCount> topItemsByCategorie(Long userId, String categorie) {
  return snkVenteRepository.topItemsByCategorie(userId, categorie, PageRequest.of(0, 10));
}

public List<SnkVente> get7DernieresVentesParUser(Long userId) {
  return getDernieresVentesParUser(userId, 7);
}

@Transactional
public int importBulk(Long userId, List<SnkVenteImportDto> items) {
  User user = getUserOrThrow(userId);
  if (items == null || items.isEmpty()) return 0;

  List<SnkVente> entities = items.stream()
    .filter(dto -> dto != null && dto.getNomItem() != null && !dto.getNomItem().trim().isEmpty())
    .map(dto -> {
      SnkVente v = new SnkVente();
      v.setUser(user);                 // ✅ isolement multi-users
      v.setNomItem(dto.getNomItem().trim());
      v.setPrixRetail(dto.getPrixRetail());
      v.setPrixResell(dto.getPrixResell());
      v.setDateAchat(dto.getDateAchat());
      v.setDateVente(dto.getDateVente());
      v.setDescription(dto.getDescription());
      v.setCategorie(dto.getCategorie());
      return v;
    })
    .toList();

  snkVenteRepository.saveAll(entities);
  return entities.size();
}




}
