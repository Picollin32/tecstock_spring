package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {
    
    private final EmpresaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = Logger.getLogger(EmpresaServiceImpl.class);
    
    @Override
    @Transactional
    public Empresa salvar(Empresa empresa) {
        logger.info("Salvando empresa: " + empresa.getRazaoSocial());

        if (repository.existsByCnpj(empresa.getCnpj())) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ");
        }

        empresa.setCnpj(empresa.getCnpj().replaceAll("[^0-9]", ""));
        empresa.setCep(empresa.getCep().replaceAll("[^0-9]", ""));
        empresa.setUf(empresa.getUf().toUpperCase());
        
        return repository.save(empresa);
    }
    
    @Override
    public Empresa buscarPorId(Long id) {
        logger.info("Buscando empresa por ID: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada com ID: " + id));
    }
    
    @Override
    public Empresa buscarPorCnpj(String cnpj) {
        logger.info("Buscando empresa por CNPJ: " + cnpj);
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
        return repository.findByCnpj(cnpjLimpo)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada com CNPJ: " + cnpj));
    }
    
    @Override
    public List<Empresa> listarTodas() {
        logger.info("Listando todas as empresas");
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public Empresa atualizar(Long id, Empresa empresaAtualizada) {
        logger.info("Atualizando empresa ID: " + id);
        
        Empresa empresaExistente = buscarPorId(id);

        String cnpjLimpo = empresaAtualizada.getCnpj().replaceAll("[^0-9]", "");
        if (repository.existsByCnpjAndIdNot(cnpjLimpo, id)) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ");
        }

        empresaExistente.setCnpj(cnpjLimpo);
        empresaExistente.setRazaoSocial(empresaAtualizada.getRazaoSocial());
        empresaExistente.setNomeFantasia(empresaAtualizada.getNomeFantasia());
        empresaExistente.setInscricaoEstadual(empresaAtualizada.getInscricaoEstadual());
        empresaExistente.setInscricaoMunicipal(empresaAtualizada.getInscricaoMunicipal());
        empresaExistente.setTelefone(empresaAtualizada.getTelefone());
        empresaExistente.setEmail(empresaAtualizada.getEmail());
        empresaExistente.setSite(empresaAtualizada.getSite());
        empresaExistente.setCep(empresaAtualizada.getCep().replaceAll("[^0-9]", ""));
        empresaExistente.setLogradouro(empresaAtualizada.getLogradouro());
        empresaExistente.setNumero(empresaAtualizada.getNumero());
        empresaExistente.setComplemento(empresaAtualizada.getComplemento());
        empresaExistente.setBairro(empresaAtualizada.getBairro());
        empresaExistente.setCidade(empresaAtualizada.getCidade());
        empresaExistente.setUf(empresaAtualizada.getUf().toUpperCase());
        empresaExistente.setCodigoMunicipio(empresaAtualizada.getCodigoMunicipio());
        empresaExistente.setRegimeTributario(empresaAtualizada.getRegimeTributario());
        empresaExistente.setCnae(empresaAtualizada.getCnae());
        
        return repository.save(empresaExistente);
    }
    
    @Override
    @Transactional
    public void deletar(Long id) {
        logger.info("Deletando empresa ID: " + id);

        StringBuilder mensagemErro = new StringBuilder("Não é possível excluir esta empresa porque ela possui dados cadastrados:");
        boolean possuiDados = false;
        
        if (usuarioRepository.existsAdminByEmpresaId(id)) {
            mensagemErro.append("\n- Administradores");
            possuiDados = true;
        }
        
        if (repository.hasFuncionarios(id)) {
            mensagemErro.append("\n- Funcionários");
            possuiDados = true;
        }
        
        if (repository.hasClientes(id)) {
            mensagemErro.append("\n- Clientes");
            possuiDados = true;
        }
        
        if (repository.hasVeiculos(id)) {
            mensagemErro.append("\n- Veículos");
            possuiDados = true;
        }
        
        if (repository.hasPecas(id)) {
            mensagemErro.append("\n- Peças");
            possuiDados = true;
        }
        
        if (repository.hasServicos(id)) {
            mensagemErro.append("\n- Serviços");
            possuiDados = true;
        }
        
        if (repository.hasOrdensServico(id)) {
            mensagemErro.append("\n- Ordens de Serviço");
            possuiDados = true;
        }
        
        if (repository.hasOrcamentos(id)) {
            mensagemErro.append("\n- Orçamentos");
            possuiDados = true;
        }
        
        if (repository.hasAgendamentos(id)) {
            mensagemErro.append("\n- Agendamentos");
            possuiDados = true;
        }
        
        if (repository.hasChecklists(id)) {
            mensagemErro.append("\n- Checklists");
            possuiDados = true;
        }
        
        if (repository.hasMovimentacoesEstoque(id)) {
            mensagemErro.append("\n- Movimentações de Estoque");
            possuiDados = true;
        }
        
        if (repository.hasFornecedores(id)) {
            mensagemErro.append("\n- Fornecedores");
            possuiDados = true;
        }
        
        if (repository.hasFabricantes(id)) {
            mensagemErro.append("\n- Fabricantes");
            possuiDados = true;
        }
        
        if (repository.hasMarcas(id)) {
            mensagemErro.append("\n- Marcas");
            possuiDados = true;
        }
        
        if (possuiDados) {
            throw new IllegalStateException(mensagemErro.toString());
        }
        
        Empresa empresa = buscarPorId(id);
        repository.delete(empresa);
    }
    
    @Override
    @Transactional
    public void ativarDesativar(Long id, Boolean ativa) {
        logger.info("Alterando status da empresa ID: " + id + " para: " + ativa);
        Empresa empresa = buscarPorId(id);
        empresa.setAtiva(ativa);
        repository.save(empresa);
    }
}
