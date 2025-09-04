package tecstock_spring.service;

import org.springframework.stereotype.Component;
import tecstock_spring.model.Servico;

@Component
public class PrecoService {

    public Double calcularPreco(Servico servico, String categoria) {
        if (servico == null) return null;
        return servico.precoParaCategoria(categoria);
    }
}
