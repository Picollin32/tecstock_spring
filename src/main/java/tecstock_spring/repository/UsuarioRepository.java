package tecstock_spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.empresa.id = :empresaId AND u.nivelAcesso = 1")
    boolean existsAdminByEmpresaId(@Param("empresaId") Long empresaId);

    List<Usuario> findByEmpresaId(Long empresaId);
    Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.empresa.id = :empresaId AND u.id != :usuarioId AND u.nivelAcesso = 2")
    boolean existsOtherUsuariosInEmpresa(@Param("empresaId") Long empresaId, @Param("usuarioId") Long usuarioId);
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.consultor.id = :consultorId AND u.empresa.id = :empresaId")
    boolean existsByConsultorIdAndEmpresaId(@Param("consultorId") Long consultorId, @Param("empresaId") Long empresaId);

    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId AND (LOWER(u.nomeUsuario) LIKE LOWER(CONCAT(:query, '%')) OR (u.consultor IS NOT NULL AND LOWER(u.consultor.nome) LIKE LOWER(CONCAT(:query, '%'))))")
    Page<Usuario> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    Page<Usuario> findByEmpresaId(Long empresaId, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId ORDER BY u.createdAt DESC")
    List<Usuario> findTopUsuariosByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);
}
