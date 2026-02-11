package tecstock_spring.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Filter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tecstock_spring.util.TenantContext;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import org.springframework.lang.NonNull;

@Component
@Order(0)
@RequiredArgsConstructor
public class TenantHibernateFilter extends OncePerRequestFilter {

    private final EntityManager entityManager;

    @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        Long empresaId = TenantContext.getCurrentEmpresaId();
        Session session = entityManager.unwrap(Session.class);
        Filter filter = null;
        if (empresaId != null) {
            filter = session.enableFilter("empresaFilter");
            filter.setParameter("empresaId", empresaId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (filter != null) {
                session.disableFilter("empresaFilter");
            }
        }
    }
}
