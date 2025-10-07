package tecstock_spring.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.AuditoriaLog;
import tecstock_spring.repository.AuditoriaLogRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class AuditListener {
    
    private static ApplicationContext applicationContext;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AuditListener.applicationContext = context;
    }
    
    @PostPersist
    public void afterCreate(Object entity) {
        try {
            String usuario = obterUsuarioLogado();
            String nomeEntidade = entity.getClass().getSimpleName();
            Long entidadeId = obterIdEntidade(entity);
            
            if (entidadeId == null) return;
            
            String valoresNovos = converterParaJson(entity);
            String descricao = String.format("Usuário %s criou %s (ID: %d)", usuario, nomeEntidade, entidadeId);
            
            salvarLog(nomeEntidade, entidadeId, "CREATE", usuario, null, valoresNovos, descricao);
        } catch (Exception e) {
            System.err.println("Erro ao criar log de auditoria: " + e.getMessage());
        }
    }
    
    @PreUpdate
    public void beforeUpdate(Object entity) {
        try {
            Long entidadeId = obterIdEntidade(entity);
            if (entidadeId == null) return;

            AuditListener self = getSelfBean();
            if (self != null) {
                String estadoAnterior = self.buscarEstadoAnterior(entity, entidadeId);
                if (estadoAnterior != null) {
                    ThreadLocalAuditContext.setEstadoAnterior(estadoAnterior);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao capturar estado anterior: " + e.getMessage());
        }
    }
    
    @PostUpdate
    public void afterUpdate(Object entity) {
        try {
            String usuario = obterUsuarioLogado();
            String nomeEntidade = entity.getClass().getSimpleName();
            Long entidadeId = obterIdEntidade(entity);
            
            if (entidadeId == null) return;
            
            String valoresAntigos = ThreadLocalAuditContext.getEstadoAnterior();
            String valoresNovos = converterParaJson(entity);
            
            String descricao = gerarDescricaoAlteracao(usuario, nomeEntidade, entidadeId, valoresAntigos, valoresNovos);
            
            salvarLog(nomeEntidade, entidadeId, "UPDATE", usuario, valoresAntigos, valoresNovos, descricao);
            
            ThreadLocalAuditContext.clear();
        } catch (Exception e) {
            System.err.println("Erro ao criar log de auditoria: " + e.getMessage());
        }
    }
    
    @PreRemove
    public void beforeDelete(Object entity) {
        try {
            String usuario = obterUsuarioLogado();
            String nomeEntidade = entity.getClass().getSimpleName();
            Long entidadeId = obterIdEntidade(entity);
            
            if (entidadeId == null) return;
            
            String valoresAntigos = converterParaJson(entity);
            String descricao = String.format("Usuário %s deletou %s (ID: %d)", usuario, nomeEntidade, entidadeId);
            
            salvarLog(nomeEntidade, entidadeId, "DELETE", usuario, valoresAntigos, null, descricao);
        } catch (Exception e) {
            System.err.println("Erro ao criar log de auditoria: " + e.getMessage());
        }
    }
    
    private String obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "Sistema";
    }
    
    private Long obterIdEntidade(Object entity) {
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (Exception e) {
            return null;
        }
    }
    
    private String converterParaJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
    
    private String gerarDescricaoAlteracao(String usuario, String nomeEntidade, Long entidadeId, 
                                          String valoresAntigos, String valoresNovos) {
        try {
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> antigos = objectMapper.readValue(valoresAntigos, typeRef);
            Map<String, Object> novos = objectMapper.readValue(valoresNovos, typeRef);
            
            StringBuilder descricao = new StringBuilder();
            descricao.append(String.format("Usuário %s alterou %s (ID: %d): ", usuario, nomeEntidade, entidadeId));
            
            boolean primeiraAlteracao = true;
            for (String chave : novos.keySet()) {
                Object valorAntigo = antigos.get(chave);
                Object valorNovo = novos.get(chave);
                
                if (valorAntigo == null && valorNovo == null) continue;
                if (valorAntigo != null && valorAntigo.equals(valorNovo)) continue;

                if (chave.equals("createdAt") || chave.equals("updatedAt")) continue;
                
                if (!primeiraAlteracao) {
                    descricao.append(", ");
                }
                
                descricao.append(String.format("%s de \"%s\" para \"%s\"", 
                    chave, valorAntigo, valorNovo));
                primeiraAlteracao = false;
            }
            
            return descricao.toString();
        } catch (Exception e) {
            return String.format("Usuário %s alterou %s (ID: %d)", usuario, nomeEntidade, entidadeId);
        }
    }
    
    private void salvarLog(String entidade, Long entidadeId, String operacao, String usuario,
                          String valoresAntigos, String valoresNovos, String descricao) {
        try {
            AuditListener self = getSelfBean();
            if (self != null) {
                self.salvarLogTransacional(entidade, entidadeId, operacao, usuario, 
                                         valoresAntigos, valoresNovos, descricao);
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar log de auditoria: " + e.getMessage());
        }
    }
    
    private AuditListener getSelfBean() {
        if (applicationContext != null) {
            return applicationContext.getBean(AuditListener.class);
        }
        return null;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String buscarEstadoAnterior(Object entity, Long entidadeId) {
        try {

            EntityManager em = applicationContext.getBean(EntityManager.class);
            
            Object entidadeAnterior = em.find(entity.getClass(), entidadeId);
            
            if (entidadeAnterior != null) {
                return converterParaJson(entidadeAnterior);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar estado anterior: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void salvarLogTransacional(String entidade, Long entidadeId, String operacao, String usuario,
                                      String valoresAntigos, String valoresNovos, String descricao) {
        AuditoriaLogRepository repository = applicationContext.getBean(AuditoriaLogRepository.class);
        
        AuditoriaLog log = new AuditoriaLog();
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setOperacao(operacao);
        log.setUsuario(usuario);
        log.setDataHora(LocalDateTime.now());
        log.setValoresAntigos(valoresAntigos);
        log.setValoresNovos(valoresNovos);
        log.setDescricao(descricao);
        
        repository.save(log);
    }

    private static class ThreadLocalAuditContext {
        private static final ThreadLocal<String> estadoAnterior = new ThreadLocal<>();
        
        static void setEstadoAnterior(String estado) {
            estadoAnterior.set(estado);
        }
        
        static String getEstadoAnterior() {
            return estadoAnterior.get();
        }
        
        static void clear() {
            estadoAnterior.remove();
        }
    }
}
