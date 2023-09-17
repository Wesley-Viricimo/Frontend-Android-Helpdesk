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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesUpdateFragmentUI extends Fragment {
    private EditText edtUpdateCliNome;
    private EditText edtUpdateCliCpf;
    private EditText edtUpdateCliEmail;
    private EditText edtUpdateCliSenha;
    private Button btnUpdateCliAtualizar;
    private Button btnUpdateCliCancelar;
    private ProgressBar pbUpdateCli;
    private ApiService apiService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesUpdateFragment = inflater.inflate(R.layout.fragment_clientes_update, container, false);

        String idCliente = getArguments().getString("idCliente");
        edtUpdateCliNome = clientesUpdateFragment.findViewById(R.id.edtUpdateCliNome);
        edtUpdateCliCpf = clientesUpdateFragment.findViewById(R.id.edtUpdateCliCpf);
        edtUpdateCliEmail = clientesUpdateFragment.findViewById(R.id.edtUpdateCliEmail);
        edtUpdateCliSenha = clientesUpdateFragment.findViewById(R.id.edtUpdateCliSenha);
        btnUpdateCliAtualizar = clientesUpdateFragment.findViewById(R.id.btnUpdateCliAtualizar);
        btnUpdateCliCancelar = clientesUpdateFragment.findViewById(R.id.btnUpdateCliCancelar);
        pbUpdateCli = clientesUpdateFragment.findViewById(R.id.pbUpdateCli);

        edtUpdateCliCpf.addTextChangedListener(MaskEditUtil.mask(edtUpdateCliCpf, MaskEditUtil.FORMAT_CPF));

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        componentesAtivos(false);
        iniciarProgressBar();
        this.carregarCliente(idCliente, token);

        btnUpdateCliAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarCliente(idCliente, token);
            }
        });

        btnUpdateCliCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornarClienteList();
            }
        });

        return clientesUpdateFragment;
    }

    private void carregarCliente(String idCliente, String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<Cliente> call = apiService.getCliente(idCliente);

        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Cliente cli = response.body();
                    edtUpdateCliNome.setText(cli.getNome());
                    edtUpdateCliCpf.setText(cli.getCpf());
                    edtUpdateCliEmail.setText(cli.getEmail());
                    edtUpdateCliSenha.setText(cli.getSenha());

                    sleepThread();
                    encerrarProgressBar();
                    componentesAtivos(true);
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void atualizarCliente(String idCliente, String token) {
        iniciarProgressBar();

        String nome = edtUpdateCliNome.getText().toString();
        String cpf = edtUpdateCliCpf.getText().toString();
        String email = edtUpdateCliEmail.getText().toString();
        String senha = edtUpdateCliSenha.getText().toString();

        ValidaCamposUtil validaCampos = new ValidaCamposUtil(getActivity());

        if (validaCampos.validaCampos(nome, cpf, email, senha)) {
            Cliente cliente = new Cliente(nome, cpf, email, senha);
            apiService = ApiClient.getClient(token).create(ApiService.class);
            Call<Void> call = apiService.putCliente(idCliente, cliente);

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
        } else {
            encerrarProgressBar();
        }
    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        retornarClienteList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void retornarClienteList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ClientesListFragmentUI()).commit();
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        pbUpdateCli.clearAnimation();
        pbUpdateCli.setVisibility(View.GONE);
    }

    private void componentesAtivos(boolean isComponentesAtivos) {
        edtUpdateCliNome.setEnabled(isComponentesAtivos);
        edtUpdateCliCpf.setEnabled(isComponentesAtivos);
        edtUpdateCliEmail.setEnabled(isComponentesAtivos);
        edtUpdateCliSenha.setEnabled(isComponentesAtivos);
        btnUpdateCliAtualizar.setEnabled(isComponentesAtivos);
        btnUpdateCliCancelar.setEnabled(isComponentesAtivos);
    }

    private void iniciarProgressBar() {
        pbUpdateCli.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbUpdateCli, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}