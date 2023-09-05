package com.example.helpdesk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
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

        if (validaEmail(email) && validaSenha(senha)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Credenciais credenciais = new Credenciais(email, senha);

            Call<Void> call = apiService.validarUsuario(credenciais);

            try {
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            String token = response.headers().get("Authorization").substring(7);
                            salvarToken(token);
                        } else {
                            Toast.makeText(getApplicationContext(), "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
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
        SharedPreferences preferences = getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        preferences.edit().putString("TOKEN",token).apply();

         /*Retrieve token wherever necessary
          SharedPreferences preferences = getActivity().getSharedPreferences("MY_APP",Context.MODE_PRIVATE);
         String retrivedToken  = preferences.getString("TOKEN",null);//second parameter default value.
        */
    }
}