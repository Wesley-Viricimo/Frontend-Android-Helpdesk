package com.example.helpdesk.ui.cliente;

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
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.util.MaskEditUtil;
import com.example.helpdesk.util.TokenUtil;
import com.example.helpdesk.util.ValidaCamposUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClientesCreateFragmentUI extends Fragment {

    private EditText edtCadCliNome;
    private EditText edtCadCliCpf;
    private EditText edtCadCliEmail;
    private EditText edtCadCliSenha;
    private Button btnCadCliCadastro;
    private Button btnCadCliCancelar;
    private ProgressBar pbCadCli;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesCreateFragment = inflater.inflate(R.layout.fragment_clientes_create, container, false);

        edtCadCliNome = clientesCreateFragment.findViewById(R.id.edtCadCliNome);
        edtCadCliCpf = clientesCreateFragment.findViewById(R.id.edtCadCliCpf);
        edtCadCliEmail = clientesCreateFragment.findViewById(R.id.edtCadCliEmail);
        edtCadCliSenha = clientesCreateFragment.findViewById(R.id.edtCadCliSenha);
        btnCadCliCadastro = clientesCreateFragment.findViewById(R.id.btnCadCliCadastrar);
        btnCadCliCancelar = clientesCreateFragment.findViewById(R.id.btnCadCliCancelar);
        pbCadCli = clientesCreateFragment.findViewById(R.id.pbCadCli);

        edtCadCliCpf.addTextChangedListener(MaskEditUtil.mask(edtCadCliCpf, MaskEditUtil.FORMAT_CPF));

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        btnCadCliCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoCadastrarAtivo(false);
                validarCadastro(token);
            }
        });

        btnCadCliCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentClientesList();
            }
        });

        return clientesCreateFragment;
    }

    private void validarCadastro(String token) {
        String nome = edtCadCliNome.getText().toString();
        String cpf = edtCadCliCpf.getText().toString();
        String email = edtCadCliEmail.getText().toString();
        String senha = edtCadCliSenha.getText().toString();
        List<String> perfis = new ArrayList<>();

        ValidaCamposUtil validaCampos = new ValidaCamposUtil(getActivity());

        if(validaCampos.validaCampos(nome, cpf, email, senha)) {
            iniciarProgressBar();
            perfis.add("1");
            String cpfFormatado = cpf.replaceAll("[^0-9]", "");

            apiService = ApiClient.getClient(token).create(ApiService.class);

            Cliente cliente = new Cliente(nome, cpfFormatado, email, senha, perfis);

            Call<Void> call = apiService.cadastrarCliente(cliente);

            try {
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
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
        } else {
            botaoCadastrarAtivo(true);
        }

    }

    private void requisicaoComSucesso(){
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        abrirFragmentClientesList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_SHORT).show();
        botaoCadastrarAtivo(true);
    }

    private void sleepThread() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        pbCadCli.clearAnimation();
        pbCadCli.setVisibility(View.GONE);
    }

    private void iniciarProgressBar() {
        pbCadCli.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbCadCli, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void abrirFragmentClientesList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ClientesListFragmentUI()).commit();
    }

    private void botaoCadastrarAtivo(Boolean isAtivo) {
        btnCadCliCadastro.setEnabled(isAtivo);
    }

}