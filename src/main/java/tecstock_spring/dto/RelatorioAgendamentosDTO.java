package tecstock_spring.dto;

import java.time.LocalDate;
import java.util.List;

public class RelatorioAgendamentosDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalAgendamentos;
    private Integer agendamentosPorMecanico;
    private List<AgendamentoPorDiaDTO> agendamentosPorDia;
    private List<AgendamentoPorMecanicoDTO> agendamentosPorMecanicoLista;

    public RelatorioAgendamentosDTO() {
    }

    public RelatorioAgendamentosDTO(LocalDate dataInicio, LocalDate dataFim, Integer totalAgendamentos,
                                    Integer agendamentosPorMecanico, List<AgendamentoPorDiaDTO> agendamentosPorDia,
                                    List<AgendamentoPorMecanicoDTO> agendamentosPorMecanicoLista) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalAgendamentos = totalAgendamentos;
        this.agendamentosPorMecanico = agendamentosPorMecanico;
        this.agendamentosPorDia = agendamentosPorDia;
        this.agendamentosPorMecanicoLista = agendamentosPorMecanicoLista;
    }

    // Getters e Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getTotalAgendamentos() {
        return totalAgendamentos;
    }

    public void setTotalAgendamentos(Integer totalAgendamentos) {
        this.totalAgendamentos = totalAgendamentos;
    }

    public Integer getAgendamentosPorMecanico() {
        return agendamentosPorMecanico;
    }

    public void setAgendamentosPorMecanico(Integer agendamentosPorMecanico) {
        this.agendamentosPorMecanico = agendamentosPorMecanico;
    }

    public List<AgendamentoPorDiaDTO> getAgendamentosPorDia() {
        return agendamentosPorDia;
    }

    public void setAgendamentosPorDia(List<AgendamentoPorDiaDTO> agendamentosPorDia) {
        this.agendamentosPorDia = agendamentosPorDia;
    }

    public List<AgendamentoPorMecanicoDTO> getAgendamentosPorMecanicoLista() {
        return agendamentosPorMecanicoLista;
    }

    public void setAgendamentosPorMecanicoLista(List<AgendamentoPorMecanicoDTO> agendamentosPorMecanicoLista) {
        this.agendamentosPorMecanicoLista = agendamentosPorMecanicoLista;
    }

    // Classes internas para os itens
    public static class AgendamentoPorDiaDTO {
        private String data;
        private Integer quantidade;

        public AgendamentoPorDiaDTO() {
        }

        public AgendamentoPorDiaDTO(String data, Integer quantidade) {
            this.data = data;
            this.quantidade = quantidade;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }

    public static class AgendamentoPorMecanicoDTO {
        private String nomeMecanico;
        private Integer quantidade;

        public AgendamentoPorMecanicoDTO() {
        }

        public AgendamentoPorMecanicoDTO(String nomeMecanico, Integer quantidade) {
            this.nomeMecanico = nomeMecanico;
            this.quantidade = quantidade;
        }

        public String getNomeMecanico() {
            return nomeMecanico;
        }

        public void setNomeMecanico(String nomeMecanico) {
            this.nomeMecanico = nomeMecanico;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }
}
