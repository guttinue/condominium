package com.condo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"placa", "condominio_id"})
})
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10) // Ex: ABC-1234 ou ABC1D23
    private String placa;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false, length = 50)
    private String cor;

    @Column(name = "tipo_veiculo", nullable = false, length = 20) // Ex: CARRO, MOTO, BICICLETA
    private String tipoVeiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador; // Morador proprietário do veículo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominium condominio; // Condomínio ao qual o veículo está associado

    public Veiculo() {
    }

    public Veiculo(String placa, String modelo, String cor, String tipoVeiculo, Morador morador, Condominium condominio) {
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
        this.tipoVeiculo = tipoVeiculo;
        this.morador = morador;
        this.condominio = condominio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }
    public Morador getMorador() { return morador; }
    public void setMorador(Morador morador) { this.morador = morador; }
    public Condominium getCondominio() { return condominio; }
    public void setCondominio(Condominium condominio) { this.condominio = condominio; }

    @Override
    public String toString() {
        return "Veiculo ID: " + id +
                ", Placa: '" + placa + '\'' +
                ", Modelo: '" + modelo + '\'' +
                ", Cor: '" + cor + '\'' +
                ", Tipo: '" + tipoVeiculo + '\'' +
                ", Morador: " + (morador != null ? morador.getNome() + " (Unid: " + morador.getUnidade() + ")" : "N/A");
    }
}