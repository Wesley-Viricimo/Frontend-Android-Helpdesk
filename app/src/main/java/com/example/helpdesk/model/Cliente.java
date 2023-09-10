package com.example.helpdesk.model;

import java.util.Date;
import java.util.List;

public class Cliente {
    Integer id;
    String nome;
    String cpf;
    String email;
    String senha;
    List<String> perfis;
    Date dataCriacao;
}
