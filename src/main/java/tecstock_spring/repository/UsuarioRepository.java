package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query("SELECT u FROM Usuario u WHERE u.nomeUsuario = :nomeUsuario")
    Usuario findByNomeUsuario(@Param("nomeUsuario") String nomeUsuario);
    
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.nomeUsuario = :nomeUsuario AND u.id != :id")
    boolean existsByNomeUsuarioAndIdNot(@Param("nomeUsuario") String nomeUsuario, @Param("id") Long id);
    
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.nomeUsuario = :nomeUsuario")
    boolean existsByNomeUsuario(@Param("nomeUsuario") String nomeUsuario);
}
