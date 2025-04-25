package com.example.taskmanager.model;

import java.time.LocalDate;

public class Tarefa {
    private Integer id;
    private String titulo;
    private String descricao;
    private LocalDate dataEntrega;
    private String status;

    public Tarefa() {
        this.status = "A Fazer";
    }

    public Tarefa(Integer id, String titulo, String descricao, LocalDate dataEntrega, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEntrega = dataEntrega;
        this.status = status != null ? status : "A Fazer";
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.titulo;
    }
}