package com.example.helpdesk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helpdesk.R;
import com.example.helpdesk.api.ApiService;
import com.example.helpdesk.model.Credenciais;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivityUI extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

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

        if (validaEmail(email)) {
            if (validaSenha(senha)) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Credenciais credenciais = new Credenciais(email, senha);

                Call<Void> call = apiService.validarUsuario(credenciais);

                try {
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Usuário ou senha incorretos", Toast.LENGTH_LONG);
                            } else {
                                System.out.println(response.headers().get("Authorization"));
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            Toast.makeText(getApplicationContext(), "Informe a senha!", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), "Informe o e-mail!", Toast.LENGTH_LONG);
        }
    }

    private boolean validaEmail(String email) {
        if (!email.equals("") && !email.equals(null) && email.contains("@")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validaSenha(String senha) {
        if (!senha.equals("") && !senha.equals(null)) {
            return true;
        } else {
            return false;
        }
    }
}