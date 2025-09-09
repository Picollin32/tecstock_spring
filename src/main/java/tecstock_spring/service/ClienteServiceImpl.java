package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ClienteController;
import tecstock_spring.exception.CpfDuplicadoException;
import tecstock_spring.model.Cliente;
import tecstock_spring.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    Logger logger = Logger.getLogger(ClienteController.class);

    @Override
    public Cliente salvar(Cliente cliente) {
        if (repository.existsByCpf(cliente.getCpf())) {
            throw new CpfDuplicadoException("CPF já cadastrado");
        }
        
        Cliente clienteSalvo = repository.save(cliente);
        logger.info("Cliente salvo com sucesso: " + clienteSalvo);
        return clienteSalvo;
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = repository.findAll();
        if (clientes.isEmpty()) {
            logger.info("Nenhum cliente cadastrado.");
        } else {
            logger.info(clientes.size() + " clientes encontrados.");
        }
        return clientes;
    }

    @Override
    public Cliente atualizar(Long id, Cliente novoCliente) {
        Cliente clienteExistente = buscarPorId(id);
        
        if (repository.existsByCpfAndIdNot(novoCliente.getCpf(), id)) {
            throw new CpfDuplicadoException("CPF já cadastrado");
        }
        
        BeanUtils.copyProperties(novoCliente, clienteExistente, "id", "createdAt", "updatedAt");
        return repository.save(clienteExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
