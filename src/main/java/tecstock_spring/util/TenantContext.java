package tecstock_spring.util;

public class TenantContext {
    private static final ThreadLocal<Long> currentEmpresaId = new ThreadLocal<>();
    private static final ThreadLocal<Integer> currentNivelAcesso = new ThreadLocal<>();
    
    public static void setCurrentEmpresaId(Long empresaId) {
        currentEmpresaId.set(empresaId);
    }
    
    public static Long getCurrentEmpresaId() {
        return currentEmpresaId.get();
    }
    
    public static void setCurrentNivelAcesso(Integer nivelAcesso) {
        currentNivelAcesso.set(nivelAcesso);
    }
    
    public static Integer getCurrentNivelAcesso() {
        return currentNivelAcesso.get();
    }
    
    public static boolean isSuperAdmin() {
        Integer nivel = currentNivelAcesso.get();
        return nivel != null && nivel == 0;
    }
    
    public static void clear() {
        currentEmpresaId.remove();
        currentNivelAcesso.remove();
    }
}
