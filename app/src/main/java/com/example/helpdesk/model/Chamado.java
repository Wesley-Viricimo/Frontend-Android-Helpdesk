package com.example.helpdesk.model;

public class Chamado {
    Integer id;
    String dataAbertura;
    String dataFechamento;
    Integer prioridade;
    Integer status;
    String titulo;
    String observacoes;

    Integer tecnico;
    Integer cliente;
    String nomeTecnico;
    String nomeCliente;

    public Chamado(Integer id, String dataAbertura, String dataFechamento, Integer prioridade, Integer status, String titulo, String observacoes, String nomeTecnico, String nomeCliente) {
        this.id = id;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
        this.prioridade = prioridade;
        this.status = status;
        this.titulo = titulo;
        this.observacoes = observacoes;
        this.nomeTecnico = nomeTecnico;
        this.nomeCliente = nomeCliente;
    }

    public Integer getId() {
        return id;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public String getDataFechamento() {
        return dataFechamento;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public Integer getStatus() {
        return status;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
}
