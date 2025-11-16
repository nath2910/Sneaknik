package backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entity.SnkVente;

//gere l'acces au donné avec des methodes predefinit mais on peut aussi cree les notres
// ici qu'on fait les requete SQL 
@Repository
public interface SnkVenteRepository extends JpaRepository<SnkVente, Integer>{
    List<SnkVente> findTop10ByOrderByIdDesc();

    public interface BrandCount {
  String getMarque();
  long getNb();
}

    

// Total global (sans filtre)
  @Query("""
  select coalesce(
           sum(
             case
               when v.prixResell is not null
                 then coalesce(v.prixResell,0) - coalesce(v.prixRetail,0)
               else 0
             end
           ), 0)
  from SnkVente v
""")
BigDecimal totalBenef();

  // Total entre 2 dates (incluses)
@Query("""
  select coalesce( sum(v.prixResell - v.prixRetail), 0 )
  from SnkVente v
  where v.date between :start and :end
    and v.prixResell is not null
    and v.prixRetail is not null
""")
BigDecimal totalBenefBetween(@Param("start") java.time.LocalDate start,
                             @Param("end")   java.time.LocalDate end);


  // Sucre: total pour une année (réutilise Between)
  default BigDecimal totalBenefYear(int year) {
    return totalBenefBetween(java.time.LocalDate.of(year, 1, 1),
                             java.time.LocalDate.of(year, 12, 31));
  }

   // Somme de tous les prixResell (NULL -> 0). Retourne 0 si aucune ligne.
  @Query("""
    select coalesce(sum(coalesce(v.prixResell, 0)), 0)
    from SnkVente v
  """)
  BigDecimal sumPrixResell();
  
 @Query("""
  SELECT
    CASE
      WHEN lower(v.nomItem) LIKE '%nike%' THEN 'Nike'
      WHEN lower(v.nomItem) LIKE '%adidas%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%puma%' THEN 'Puma'
      WHEN lower(v.nomItem) LIKE '%new balance%' THEN 'New Balance'
      WHEN lower(v.nomItem) LIKE '%asics%' THEN 'ASICS'
      WHEN lower(v.nomItem) LIKE '%pokemon%' THEN 'Pokemon'
      WHEN lower(v.nomItem) LIKE '%air%' THEN 'Nike'
      WHEN lower(v.nomItem) LIKE '%jordan%' THEN 'Jordan'
      WHEN lower(v.nomItem) LIKE '%dunk%' THEN 'Dunk'
      WHEN lower(v.nomItem) LIKE '%yeezy%' THEN 'Yeezy'
      WHEN lower(v.nomItem) LIKE '%samba%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%spezial%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%gazelle%' THEN 'Adidas'
      ELSE 'Autre'
    END AS marque,
    COUNT(v) AS nb
  FROM SnkVente v
  GROUP BY
    CASE
      WHEN lower(v.nomItem) LIKE '%nike%' THEN 'Nike'
      WHEN lower(v.nomItem) LIKE '%adidas%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%puma%' THEN 'Puma'
      WHEN lower(v.nomItem) LIKE '%new balance%' THEN 'New Balance'
      WHEN lower(v.nomItem) LIKE '%asics%' THEN 'ASICS'
      WHEN lower(v.nomItem) LIKE '%pokemon%' THEN 'Pokemon'
      WHEN lower(v.nomItem) LIKE '%air%' THEN 'Nike'
      WHEN lower(v.nomItem) LIKE '%jordan%' THEN 'Jordan'
      WHEN lower(v.nomItem) LIKE '%dunk%' THEN 'Dunk'
      WHEN lower(v.nomItem) LIKE '%yeezy%' THEN 'Yeezy'
      WHEN lower(v.nomItem) LIKE '%samba%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%spezial%' THEN 'Adidas'
      WHEN lower(v.nomItem) LIKE '%gazelle%' THEN 'Adidas'
      ELSE 'Autre'
    END
  ORDER BY nb DESC
""")
List<BrandCount> graphMarque();

// //supprimerPaire
void deleteById(Long id);

}