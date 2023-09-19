package com.example.helpdesk.ui.tecnico;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.MaskEditUtil;
import com.example.helpdesk.util.TokenUtil;
import com.example.helpdesk.util.ValidaCamposUtil;

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

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        btnCadTecCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoCadastrarAtivo(false);
                validarCadastro(token);
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

    private void validarCadastro(String token) {
        String nome = edtCadTecNome.getText().toString();
        String cpf = edtCadTecCpf.getText().toString();
        String email = edtCadTecEmail.getText().toString();
        String senha = edtCadTecSenha.getText().toString();
        List<String> perfis = new ArrayList<>();

        ValidaCamposUtil validaCampos = new ValidaCamposUtil(getActivity());

        if (validaCampos.validaCampos(nome, cpf, email, senha)) {
            iniciarProgressBar();
            perfis.add("0");
            perfis.add("2");
            String cpfFormatado = cpf.replaceAll("[^0-9]", "");

            apiService = ApiClient.getClient(token).create(ApiService.class);

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
                        t.printStackTrace();
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

    private void abrirFragmentTecnicosList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TecnicosListFragmentUI()).commit();
    }

    private void botaoCadastrarAtivo(Boolean isAtivo) {
        btnCadTecCadastrar.setEnabled(isAtivo);
    }
}