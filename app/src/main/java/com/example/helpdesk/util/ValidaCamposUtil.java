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
        Toast.makeText(activity, "Informe uma senha válida!", Toast.LENGTH_LONG).show();
        return false;
    }
}
