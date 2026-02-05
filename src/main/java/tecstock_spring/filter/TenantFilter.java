package tecstock_spring.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tecstock_spring.util.TenantContext;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(TenantFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {

            Long empresaId = (Long) request.getAttribute("empresaId");
            
            if (empresaId != null) {
                TenantContext.setCurrentEmpresaId(empresaId);
                logger.debug("TenantContext configurado com empresaId: " + empresaId);
            }
            
            filterChain.doFilter(request, response);
        } finally {

            TenantContext.clear();
        }
    }
}
