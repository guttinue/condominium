package com.condominium.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Assembleia {
    private Long id;
    private LocalDate data;
    private LocalTime horario;
    private String local;
    private String pauta;
    private int participantesEsperados;

    public Assembleia(Long id, LocalDate data, LocalTime horario, String local, String pauta, int participantesEsperados) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.local = local;
        this.pauta = pauta;
        this.participantesEsperados = participantesEsperados;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getPauta() {
        return pauta;
    }

    public void setPauta(String pauta) {
        this.pauta = pauta;
    }

    public int getParticipantesEsperados() {
        return participantesEsperados;
    }

    public void setParticipantesEsperados(int participantesEsperados) {
        this.participantesEsperados = participantesEsperados;
    }

    @Override
    public String toString() {
        return "Assembleia{" +
                "ID=" + id +
                ", Data=" + data +
                ", Horário=" + horario +
                ", Local='" + local + '\'' +
                ", Pauta='" + pauta + '\'' +
                ", Participantes Esperados=" + participantesEsperados +
                '}';
    }
}
