package com.example.helpdesk.ui.tecnico;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.ui.cliente.ClientesListFragmentUI;
import com.example.helpdesk.util.MaskEditUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnicosCreateFragmentUI extends Fragment {

    private EditText edtCadTecNome;
    private EditText edtCadTecCpf;
    private EditText edtCadTecEmail;
    private EditText edtCadTecSenha;
    private Button btnCadTecCadastrar;
    private Button btnCadTecCancelar;
    private ProgressBar pbCadTec;
    private ApiService apiService;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesCreateFragment = inflater.inflate(R.layout.fragment_tecnicos_create, container, false);
        edtCadTecNome = clientesCreateFragment.findViewById(R.id.edtCadTecNome);
        edtCadTecCpf = clientesCreateFragment.findViewById(R.id.edtCadTecCpf);
        edtCadTecEmail = clientesCreateFragment.findViewById(R.id.edtCadTecEmail);
        edtCadTecSenha = clientesCreateFragment.findViewById(R.id.edtCadTecSenha);
        btnCadTecCadastrar = clientesCreateFragment.findViewById(R.id.btnCadTecCadastrar);
        btnCadTecCancelar = clientesCreateFragment.findViewById(R.id.btnCadTecCancelar);
        pbCadTec = clientesCreateFragment.findViewById(R.id.pbCadTec);

        edtCadTecCpf.addTextChangedListener(MaskEditUtil.mask(edtCadTecCpf, MaskEditUtil.FORMAT_CPF));

        btnCadTecCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoCadastrarAtivo(false);
                validarCadastro();
            }
        });

        btnCadTecCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentTecnicosList();
            }
        });

        return clientesCreateFragment;
    }

    private void validarCadastro() {
        String nome = edtCadTecNome.getText().toString();
        String cpf = edtCadTecCpf.getText().toString();
        String email = edtCadTecEmail.getText().toString();
        String senha = edtCadTecSenha.getText().toString();
        List<String> perfis = new ArrayList<>();

        if (validaCampos(nome, cpf, email, senha)) {
            iniciarProgressBar();
            perfis.add("0");
            perfis.add("2");
            String cpfFormatado = cpf.replaceAll("[^0-9]", "");

            apiService = ApiClient.getClient(getToken()).create(ApiService.class);

            Tecnico tecnico = new Tecnico(nome, cpfFormatado, email, senha, perfis);

            Call<Void> call = apiService.cadastrarTecnico(tecnico);

            try {
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            requisicaoComSucesso();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                String erro = jObjError.getString("message");
                                requisicaoComErro(erro);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Tecnico cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        abrirFragmentTecnicosList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_SHORT).show();
        botaoCadastrarAtivo(true);
    }

    private void encerrarProgressBar() {
        pbCadTec.clearAnimation();
        pbCadTec.setVisibility(View.GONE);
    }

    private void sleepThread() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciarProgressBar() {
        pbCadTec.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbCadTec, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private String getToken() {
        preferences = getActivity().getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);
        return token;
    }

    private void abrirFragmentTecnicosList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TecnicosListFragmentUI()).commit();
    }

    private boolean validaCampos(String nome, String cpf, String email, String senha) {
        if (validaNome(nome) && validaCpf(cpf) && validaEmail(email) && validaSenha(senha)) {
            return true;
        }
        return false;
    }

    private boolean validaNome(String nome) {
        if (!nome.equals("") && !nome.equals(null) && nome.length() > 5) {
            return true;
        }
        Toast.makeText(getContext(), "Campo nome é requerido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean validaCpf(String cpf) {
        if (!cpf.equals("") && !cpf.equals(null) && cpf.length() >= 11) {
            return true;
        }
        Toast.makeText(getContext(), "Informe um CPF válido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean validaEmail(String email) {
        if (!email.equals("") && !email.equals(null) && email.contains("@") && email.contains(".com")) {
            return true;
        }
        Toast.makeText(getContext(), "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean validaSenha(String senha) {
        if (!senha.equals("") && !senha.equals(null) && senha.length() > 3) {
            return true;
        }
        Toast.makeText(getContext(), "Informe uma senha válida!", Toast.LENGTH_LONG).show();
        return false;
    }

    private void botaoCadastrarAtivo(Boolean isAtivo) {
        btnCadTecCadastrar.setEnabled(isAtivo);
    }
}