package com.example.helpdesk.util;

import android.app.Activity;
import android.widget.Toast;

public class ValidaCamposUtil {
    private Activity activity;

    public ValidaCamposUtil(Activity activity) {
        this.activity = activity;
    }

    public boolean validaCampos( String nome, String cpf, String email, String senha) {
        if (validaNome(activity, nome) && validaCpf(activity, cpf) && validaEmail(activity, email) && validaSenha(activity, senha)) {
            return true;
        }
        return false;
    }

    public boolean validaNome(Activity activity, String nome) {
        if (!nome.equals("") && !nome.equals(null) && nome.length() > 5) {
            return true;
        }
        Toast.makeText(activity, "Campo nome é requerido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaCpf(Activity activity, String cpf) {
        if (!cpf.equals("") && !cpf.equals(null) && cpf.length() >= 11) {
            return true;
        }
        Toast.makeText(activity, "Informe um CPF válido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaEmail(Activity activity, String email) {
        if (!email.equals("") && !email.equals(null) && email.contains("@") && email.contains(".com")) {
            return true;
        }
        Toast.makeText(activity, "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaSenha(Activity activity, String senha) {
        if (!senha.equals("") && !senha.equals(null) && senha.length() > 3) {
            return true;
        }
        Toast.makeText(activity, "Informe uma senha válida!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validacoesChamado(String titulo, String descricao, Integer cliente, Integer tecnico) {
        if (validaTitulo(activity, titulo) && validaDescricao(activity, descricao) && validaCliente(activity, cliente) && validaTecnico(activity, tecnico)) {
            return true;
        }
        return false;
    }

    public boolean validaTitulo(Activity activity, String titulo) {
        if (!titulo.equals("") && !titulo.equals(null) && titulo.length() > 3) {
            return true;
        }
        Toast.makeText(activity, "Campo título é requerido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaDescricao(Activity activity, String descricao) {
        if (!descricao.equals("") && !descricao.equals(null) && descricao.length() > 3) {
            return true;
        }
        Toast.makeText(activity, "Campo descrição é requerido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaCliente(Activity activity, Integer cliente) {
        if (cliente != 0) {
            return true;
        }
        Toast.makeText(activity, "Selecione um cliente!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean validaTecnico(Activity activity, Integer tecnico) {
        if (tecnico != 0) {
            return true;
        }
        Toast.makeText(activity, "Selecione um técnico!", Toast.LENGTH_SHORT).show();
        return false;
    }
}
