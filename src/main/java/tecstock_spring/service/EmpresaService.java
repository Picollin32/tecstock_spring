package tecstock_spring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.Empresa;

import java.util.List;

public interface EmpresaService {
    
    Empresa salvar(Empresa empresa);
    
    Empresa buscarPorId(Long id);
    
    Empresa buscarPorCnpj(String cnpj);
    
    List<Empresa> listarTodas();
    
    Empresa atualizar(Long id, Empresa empresa);
    
    void deletar(Long id);
    
    void ativarDesativar(Long id, Boolean ativa);

    Page<Empresa> buscarPaginado(String query, Pageable pageable);

    List<Empresa> listarUltimosParaInicio(int limit);
}
