package tecstock_spring.service;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.exception.ResourceNotFoundException;
import tecstock_spring.model.Empresa;
import tecstock_spring.util.TenantContext;

public abstract class BaseService<T, ID> {

    protected Long getCurrentEmpresaId() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto. Usuário não autenticado corretamente.");
        }
        return empresaId;
    }

    protected boolean belongsToCurrentEmpresa(Object entity) {
        Long currentEmpresaId = getCurrentEmpresaId();

        try {
            java.lang.reflect.Method getEmpresaMethod = entity.getClass().getMethod("getEmpresa");
            Empresa empresa = (Empresa) getEmpresaMethod.invoke(entity);
            return empresa != null && empresa.getId().equals(currentEmpresaId);
        } catch (Exception e) {
            return false;
        }
    }

    protected void validateTenantAccess(Object entity, String entityName, Object id) {
        if (!belongsToCurrentEmpresa(entity)) {
            throw new ResourceNotFoundException(
                entityName + " com ID " + id + " não encontrado(a) ou não pertence à sua empresa"
            );
        }
    }

    protected abstract JpaRepository<T, ID> getRepository();
}
