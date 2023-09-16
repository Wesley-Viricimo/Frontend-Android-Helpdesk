package com.example.helpdesk.ui.cliente;

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
import com.example.helpdesk.api.ApiService;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.util.MaskEditUtil;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesDeleteFragmentUI extends Fragment {

    private EditText edtDeleteCliNome;
    private EditText edtDeleteCliCpf;
    private EditText edtDeleteCliEmail;
    private EditText edtDeleteCliSenha;
    private Button btnDeleteCliDeletar;
    private Button btnDeleteCliCancelar;
    private ProgressBar pbDeleteCli;
    private ApiService apiService;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View clientesDeleteFragment = inflater.inflate(R.layout.fragment_clientes_delete, container, false);

        String idCliente = getArguments().getString("idCliente");
        edtDeleteCliNome = clientesDeleteFragment.findViewById(R.id.edtDeleteCliNome);
        edtDeleteCliCpf = clientesDeleteFragment.findViewById(R.id.edtDeleteCliCpf);
        edtDeleteCliEmail = clientesDeleteFragment.findViewById(R.id.edtDeleteCliEmail);
        edtDeleteCliSenha = clientesDeleteFragment.findViewById(R.id.edtDeleteCliSenha);
        btnDeleteCliDeletar = clientesDeleteFragment.findViewById(R.id.btnDeleteCliDeletar);
        btnDeleteCliCancelar = clientesDeleteFragment.findViewById(R.id.btnDeleteCliCancelar);
        pbDeleteCli = clientesDeleteFragment.findViewById(R.id.pbDeleteCli);

        edtDeleteCliCpf.addTextChangedListener(MaskEditUtil.mask(edtDeleteCliCpf, MaskEditUtil.FORMAT_CPF));

        inativarEditsButtons();
        iniciarProgressBar();

        this.carregarCliente(idCliente);

        btnDeleteCliDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarCliente(idCliente);
            }
        });

        btnDeleteCliCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornarClienteList();
            }
        });

        return clientesDeleteFragment;
    }

    private void carregarCliente(String idCliente) {
        apiService = ApiClient.getClient(getToken()).create(ApiService.class);
        Call<Cliente> call = apiService.getCliente(idCliente);

        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Cliente cli = response.body();
                    edtDeleteCliNome.setText(cli.getNome());
                    edtDeleteCliCpf.setText(cli.getCpf());
                    edtDeleteCliEmail.setText(cli.getEmail());
                    edtDeleteCliSenha.setText(cli.getSenha());

                    sleepThread();
                    encerrarProgressBar();
                    ativarButtons();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void deletarCliente(String idCliente) {
        iniciarProgressBar();

        apiService = ApiClient.getClient(getToken()).create(ApiService.class);
        Call<Void> call = apiService.deleteCliente(idCliente);

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
    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Cliente deletado com sucesso!", Toast.LENGTH_SHORT).show();
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

    private String getToken() {
        preferences = getActivity().getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);
        return token;
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        pbDeleteCli.clearAnimation();
        pbDeleteCli.setVisibility(View.GONE);
    }


    private void inativarEditsButtons() {
        edtDeleteCliNome.setEnabled(false);
        edtDeleteCliCpf.setEnabled(false);
        edtDeleteCliEmail.setEnabled(false);
        edtDeleteCliSenha.setEnabled(false);
        btnDeleteCliDeletar.setEnabled(false);
        btnDeleteCliCancelar.setEnabled(false);
    }

    private void ativarButtons() {
        btnDeleteCliDeletar.setEnabled(true);
        btnDeleteCliCancelar.setEnabled(true);
    }

    private void iniciarProgressBar() {
        pbDeleteCli.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbDeleteCli, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}

