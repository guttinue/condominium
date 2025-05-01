package com.condominium.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private Long id;
    private AreaComum area;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String status;

    public Reserva() {}

    public Reserva(Long id, AreaComum area, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String status) {
        this.id = id;
        this.area = area;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public AreaComum getArea() {
        return area;
    }
    public void setArea(AreaComum area) {
        this.area = area;
    }
    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public LocalTime getHoraFim() {
        return horaFim;
    }
    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", area=" + area.getNome() +
                ", data=" + data +
                ", horaInicio=" + horaInicio +
                ", horaFim=" + horaFim +
                ", status='" + status + '\'' +
                '}';
    }
}
