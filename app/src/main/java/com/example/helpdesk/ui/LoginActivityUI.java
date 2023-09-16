package com.example.helpdesk.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helpdesk.R;
import com.example.helpdesk.api.service.ApiServiceClientes;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.model.Credenciais;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityUI extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private ProgressBar progressBarLogin;

    private ApiServiceClientes apiServiceClientes;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        preferences = getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token != null) {
            abrirTelaPrincipal();
        }

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        progressBarLogin = findViewById(R.id.progressbarLogin);


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaInformacoes();
            }
        });
    }

    private void validaInformacoes() {
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        if (validaEmail(email) && validaSenha(senha)) {
            iniciarProgressBar();

            apiServiceClientes = ApiClient.clientLogin().create(ApiServiceClientes.class);
            Credenciais credenciais = new Credenciais(email, senha);

            Call<Void> call = apiServiceClientes.validarUsuario(credenciais);

            try {
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            String token = response.headers().get("Authorization");
                            salvarToken(token);
                            finalizarRequisição();
                            abrirTelaPrincipal();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                String erro = jObjError.getString("message");
                                finalizarRequisição();
                                Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void finalizarRequisição() {
        sleepThread();
        encerrarProgressBar();
    }

    private boolean validaEmail(String email) {
        if (!email.equals("") && !email.equals(null) && email.contains("@") && email.contains(".com")) {
            return true;
        }
        Toast.makeText(getApplicationContext(), "Informe um e-mail válido!", Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean validaSenha(String senha) {
        if (!senha.equals("") && !senha.equals(null)) {
            return true;
        }
        Toast.makeText(getApplicationContext(), "Informe a senha!", Toast.LENGTH_LONG).show();
        return false;
    }

    private void salvarToken(String token) {
        preferences = getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        preferences.edit().putString("TOKEN", token).apply();
    }

    private void iniciarProgressBar() {
        progressBarLogin.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBarLogin, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void encerrarProgressBar() {
        progressBarLogin.clearAnimation();
        progressBarLogin.setVisibility(View.GONE);
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(this, MainActivityUI.class);
        startActivity(intent);
    }

    private void sleepThread() {
        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}