package tecstock_spring.service;

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
}
