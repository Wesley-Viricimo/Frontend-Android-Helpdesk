package com.example.helpdesk.model;

import java.util.List;

public class Cliente {
    Integer id;
    String nome;
    String cpf;
    String email;
    String senha;
    List<String> perfis;
    String dataCriacao;

    String fotoPerfil;

    public Cliente(Integer id, String nome, String cpf, String email, String senha, List<String> perfis, String dataCriacao, String fotoPerfil) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfis = perfis;
        this.dataCriacao = dataCriacao;
        this.fotoPerfil = fotoPerfil;
    }

    public Cliente(String nome, String cpf, String email, String senha, List<String> perfis) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfis = perfis;
    }

    public Cliente(String nome, String cpf, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public List<String> getPerfis() {
        return perfis;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }
}
