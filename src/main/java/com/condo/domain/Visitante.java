package com.condo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "visitantes")
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_visitante", nullable = false, length = 200)
    private String nomeVisitante;

    @Column(name = "data_hora_visita_prevista", nullable = false)
    private LocalDateTime dataHoraVisitaPrevista;

    @Column(name = "unidade_destino", nullable = false, length = 100)
    private String unidadeDestino;

    @ManyToOne(fetch = FetchType.EAGER) // <-- MUDE AQUI DE LAZY PARA EAGER
    @JoinColumn(name = "morador_responsavel_id", nullable = false)
    private Morador moradorResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominium condominio;

    @Column(name = "data_hora_registro", nullable = false, updatable = false)
    private LocalDateTime dataHoraRegistro;

    // Status: ESPERADO, AUTORIZADO, CHEGOU, SAIU, CANCELADO, NAO_AUTORIZADO
    @Column(name = "status_visita", nullable = false, length = 30)
    private String statusVisita;

    // Opcional: Documento do visitante (RG, CPF) - considerar LGPD
    @Column(name = "documento_visitante", length = 50)
    private String documentoVisitante;

    // Opcional: Placa do veículo
    @Column(name = "placa_veiculo", length = 15)
    private String placaVeiculo;

    // Opcional: Data e hora de entrada e saída efetivas
    @Column(name = "data_hora_entrada_efetiva")
    private LocalDateTime dataHoraEntradaEfetiva;

    @Column(name = "data_hora_saida_efetiva")
    private LocalDateTime dataHoraSaidaEfetiva;



    public Visitante() {
    }

    public Visitante(String nomeVisitante, LocalDateTime dataHoraVisitaPrevista, String unidadeDestino,
                     Morador moradorResponsavel, Condominium condominio, String documentoVisitante, String placaVeiculo) {
        this.nomeVisitante = nomeVisitante;
        this.dataHoraVisitaPrevista = dataHoraVisitaPrevista;
        this.unidadeDestino = unidadeDestino;
        this.moradorResponsavel = moradorResponsavel;
        this.condominio = condominio;
        this.documentoVisitante = documentoVisitante;
        this.placaVeiculo = placaVeiculo;
        this.dataHoraRegistro = LocalDateTime.now();
        this.statusVisita = "ESPERADO"; // Status inicial
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeVisitante() { return nomeVisitante; }
    public void setNomeVisitante(String nomeVisitante) { this.nomeVisitante = nomeVisitante; }
    public LocalDateTime getDataHoraVisitaPrevista() { return dataHoraVisitaPrevista; }
    public void setDataHoraVisitaPrevista(LocalDateTime dataHoraVisitaPrevista) { this.dataHoraVisitaPrevista = dataHoraVisitaPrevista; }
    public String getUnidadeDestino() { return unidadeDestino; }
    public void setUnidadeDestino(String unidadeDestino) { this.unidadeDestino = unidadeDestino; }
    public Morador getMoradorResponsavel() { return moradorResponsavel; }
    public void setMoradorResponsavel(Morador moradorResponsavel) { this.moradorResponsavel = moradorResponsavel; }
    public Condominium getCondominio() { return condominio; }
    public void setCondominio(Condominium condominio) { this.condominio = condominio; }
    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(LocalDateTime dataHoraRegistro) { this.dataHoraRegistro = dataHoraRegistro; }
    public String getStatusVisita() { return statusVisita; }
    public void setStatusVisita(String statusVisita) { this.statusVisita = statusVisita; }
    public String getDocumentoVisitante() { return documentoVisitante; }
    public void setDocumentoVisitante(String documentoVisitante) { this.documentoVisitante = documentoVisitante; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public LocalDateTime getDataHoraEntradaEfetiva() { return dataHoraEntradaEfetiva; }
    public void setDataHoraEntradaEfetiva(LocalDateTime dataHoraEntradaEfetiva) { this.dataHoraEntradaEfetiva = dataHoraEntradaEfetiva; }
    public LocalDateTime getDataHoraSaidaEfetiva() { return dataHoraSaidaEfetiva; }
    public void setDataHoraSaidaEfetiva(LocalDateTime dataHoraSaidaEfetiva) { this.dataHoraSaidaEfetiva = dataHoraSaidaEfetiva; }

    @Override
    public String toString() {
        return "Visitante ID: " + id +
                ", Nome: '" + nomeVisitante + '\'' +
                ", Previsto para: " + dataHoraVisitaPrevista.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                ", Unidade: '" + unidadeDestino + '\'' +
                ", Status: '" + statusVisita + '\'' +
                ", Morador: " + (moradorResponsavel != null ? moradorResponsavel.getNome() : "N/A");
    }
}