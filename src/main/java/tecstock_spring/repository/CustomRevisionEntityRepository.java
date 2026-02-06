package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.CustomRevisionEntity;

public interface CustomRevisionEntityRepository extends JpaRepository<CustomRevisionEntity, Long> {
    
    @Query("SELECT COUNT(r) > 0 FROM CustomRevisionEntity r WHERE r.usuario = :nomeUsuario")
    boolean existsByUsuario(@Param("nomeUsuario") String nomeUsuario);
}
