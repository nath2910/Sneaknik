package backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.dto.TopVenteProjection;
import backend.entity.SnkVente;

@Repository
public interface SnkVenteRepository extends JpaRepository<SnkVente, Integer> {

    // Toutes les ventes d’un user (triées par dateAchat décroissante)
    List<SnkVente> findByUserIdOrderByDateAchatDesc(Integer userId);

    // 10 dernières par date d’achat
    List<SnkVente> findByUser_IdOrderByDateAchatDesc(Integer userId);

   
    List<SnkVente> findTop10ByUser_IdOrderByDateAchatDesc(Integer userId);

    public interface BrandCount {
        String getMarque();
        long getNb();
    }

    // Total global par user
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
      where v.user.id = :userId
    """)
    BigDecimal totalBenef(@Param("userId") Integer userId);

    // Total entre 2 dates par user
    @Query("""
      select coalesce( sum(v.prixResell - v.prixRetail), 0 )
      from SnkVente v
      where v.user.id = :userId
        and v.dateAchat between :start and :end
        and v.prixResell is not null
        and v.prixRetail is not null
    """)
    BigDecimal totalBenefBetween(
            @Param("userId") Integer userId,
            @Param("start") java.time.LocalDate start,
            @Param("end")   java.time.LocalDate end
    );

    default BigDecimal totalBenefYear(Integer userId, int year) {
        return totalBenefBetween(
                userId,
                java.time.LocalDate.of(year, 1, 1),
                java.time.LocalDate.of(year, 12, 31)
        );
    }

    // Somme des prixResell par user
    @Query("""
      select coalesce(sum(coalesce(v.prixResell, 0)), 0)
      from SnkVente v
      where v.user.id = :userId
    """)
    BigDecimal sumPrixResell(@Param("userId") Integer userId);

    // Graph par marque par user
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
          WHEN lower(v.nomItem) LIKE '%dunk%' THEN 'Nike'
          WHEN lower(v.nomItem) LIKE '%yeezy%' THEN 'Yeezy'
          WHEN lower(v.nomItem) LIKE '%samba%' THEN 'Adidas'
          WHEN lower(v.nomItem) LIKE '%spezial%' THEN 'Adidas'
          WHEN lower(v.nomItem) LIKE '%gazelle%' THEN 'Adidas'
          ELSE 'Autre'
        END AS marque,
        COUNT(v) AS nb
      FROM SnkVente v
      WHERE v.user.id = :userId
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
          WHEN lower(v.nomItem) LIKE '%dunk%' THEN 'Nike'
          WHEN lower(v.nomItem) LIKE '%yeezy%' THEN 'Yeezy'
          WHEN lower(v.nomItem) LIKE '%samba%' THEN 'Adidas'
          WHEN lower(v.nomItem) LIKE '%spezial%' THEN 'Adidas'
          WHEN lower(v.nomItem) LIKE '%gazelle%' THEN 'Adidas'
          ELSE 'Autre'
        END
      ORDER BY nb DESC
    """)
    List<BrandCount> graphMarque(@Param("userId") Integer userId);

    // Delete sécurisé : un user ne peut supprimer que ses ventes
    void deleteByIdAndUserId(Integer id, Integer userId);

    // Top ventes par nom d’item sur une période + user
@Query("""
    SELECT 
      v.nomItem AS nomItem,
      SUM( COALESCE(v.prixResell, 0) - COALESCE(v.prixRetail, 0) ) AS benefice
    FROM SnkVente v
    WHERE v.user.id = :userId
      AND v.dateVente IS NOT NULL
      AND v.dateVente BETWEEN :start AND :end
      AND v.prixResell IS NOT NULL
      AND v.prixRetail IS NOT NULL
    GROUP BY v.nomItem
    ORDER BY benefice DESC
    """)
List<TopVenteProjection> topVentesBetween(
        @Param("userId") Integer userId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
);
    
default List<TopVenteProjection> topVentesYear(Integer userId, int year) {
    return topVentesBetween(
            userId,
            LocalDate.of(year, 1, 1),
            LocalDate.of(year, 12, 31)
    );
}
 



}

