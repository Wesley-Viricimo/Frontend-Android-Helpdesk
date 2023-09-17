package com.example.helpdesk.model;

public class Chamado {
    Integer id;
    String dataAbertura;
    String dataFechamento;
    String prioridade;
    String status;
    String titulo;
    String observacoes;
    Tecnico tecnico;
    Cliente cliente;
}
