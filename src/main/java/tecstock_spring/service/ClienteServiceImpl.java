package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ClienteController;
import tecstock_spring.exception.CpfDuplicadoException;
import tecstock_spring.model.Cliente;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.ClienteRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.OrcamentoRepository;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.exception.ClienteEmUsoException;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final EmpresaRepository empresaRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ChecklistRepository checklistRepository;
    Logger logger = Logger.getLogger(ClienteController.class);

    @Override
    public Cliente salvar(Cliente cliente) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }

        if (repository.existsByCpfAndEmpresaId(cliente.getCpf(), empresaId)) {
            throw new CpfDuplicadoException("CPF já cadastrado nesta empresa");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        cliente.setEmpresa(empresa);
        
        Cliente clienteSalvo = repository.save(cliente);
        logger.info("Cliente salvo com sucesso na empresa " + empresaId + ": " + clienteSalvo);
        return clienteSalvo;
    }

    @Override
    public Cliente buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Cliente> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        logger.info("=== LISTAR CLIENTES - EmpresaId do contexto: " + empresaId);
        
        if (empresaId == null) {
            logger.error("EmpresaId é NULL no contexto!");
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Cliente> clientes = repository.findByEmpresaId(empresaId);
        logger.info("=== CLIENTES ENCONTRADOS: " + clientes.size() + " para empresa " + empresaId);
        
        if (clientes.isEmpty()) {
            logger.info("Nenhum cliente cadastrado na empresa " + empresaId);
        } else {
            logger.info(clientes.size() + " clientes encontrados na empresa " + empresaId);
            for (Cliente c : clientes) {
                logger.info("  - Cliente ID: " + c.getId() + ", Nome: " + c.getNome() + ", EmpresaId: " + (c.getEmpresa() != null ? c.getEmpresa().getId() : "NULL"));
            }
        }
        return clientes;
    }

    @Override
    public Cliente atualizar(Long id, Cliente novoCliente) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Cliente clienteExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado ou não pertence à sua empresa"));

        if (repository.existsByCpfAndIdNotAndEmpresaId(novoCliente.getCpf(), id, empresaId)) {
            throw new CpfDuplicadoException("CPF já cadastrado nesta empresa");
        }
        
        BeanUtils.copyProperties(novoCliente, clienteExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(clienteExistente);
    }

    @Override
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Cliente cliente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado ou não pertence à sua empresa"));
        
        String cpf = cliente.getCpf();

        boolean temOrdem = ordemServicoRepository.existsByClienteCpf(cpf);
        boolean temOrcamento = orcamentoRepository.existsByClienteCpf(cpf);
        boolean temChecklist = checklistRepository.existsByClienteCpf(cpf);

        if (temOrdem) {
            throw new ClienteEmUsoException("Cliente não pode ser excluído pois está vinculado a uma Ordem de Serviço");
        }

        if (temChecklist) {
            throw new ClienteEmUsoException("Cliente não pode ser excluído pois está vinculado a um Checklist");
        }

        if (temOrcamento) {
            throw new ClienteEmUsoException("Cliente não pode ser excluído pois está vinculado a um Orçamento");
        }

        repository.deleteById(id);
        logger.info("Cliente deletado da empresa " + empresaId + ": " + id);
    }
}
