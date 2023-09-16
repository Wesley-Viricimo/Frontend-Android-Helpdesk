package com.example.helpdesk.ui.cliente;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.helpdesk.api.ApiService;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.util.MaskEditUtil;

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
    private SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesListFragment = inflater.inflate(R.layout.fragment_clientes_update, container, false);

        String idCliente = getArguments().getString("idCliente");
        edtUpdateCliNome = clientesListFragment.findViewById(R.id.edtUpdateCliNome);
        edtUpdateCliCpf = clientesListFragment.findViewById(R.id.edtUpdateCliCpf);
        edtUpdateCliEmail = clientesListFragment.findViewById(R.id.edtUpdateCliEmail);
        edtUpdateCliSenha = clientesListFragment.findViewById(R.id.edtUpdateCliSenha);
        btnUpdateCliAtualizar = clientesListFragment.findViewById(R.id.btnUpdateCliAtualizar);
        btnUpdateCliCancelar = clientesListFragment.findViewById(R.id.btnUpdateCliCancelar);
        pbUpdateCli = clientesListFragment.findViewById(R.id.pbUpdateCli);

        edtUpdateCliCpf.addTextChangedListener(MaskEditUtil.mask(edtUpdateCliCpf, MaskEditUtil.FORMAT_CPF));

        inativarEditsButtons();
        iniciarProgressBar();
        this.carregarCliente(idCliente);

        btnUpdateCliAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarCliente(idCliente);
            }
        });

        btnUpdateCliCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornarClienteList();
            }
        });

        return clientesListFragment;
    }

    private void carregarCliente(String idCliente) {
        apiService = ApiClient.getClient(getToken()).create(ApiService.class);
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
                    ativarEditsButtons();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void atualizarCliente(String idCliente) {
        iniciarProgressBar();

        String nome = edtUpdateCliNome.getText().toString();
        String cpf = edtUpdateCliCpf.getText().toString();
        String email = edtUpdateCliEmail.getText().toString();
        String senha = edtUpdateCliSenha.getText().toString();

        if (validaCampos(nome, cpf, email, senha)) {
            Cliente cliente = new Cliente(nome, cpf, email, senha);
            apiService = ApiClient.getClient(getToken()).create(ApiService.class);
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
        Toast.makeText(getActivity(), erro, Toast.LENGTH_SHORT).show();
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
        pbUpdateCli.clearAnimation();
        pbUpdateCli.setVisibility(View.GONE);
    }

    private void inativarEditsButtons() {
        edtUpdateCliNome.setEnabled(false);
        edtUpdateCliCpf.setEnabled(false);
        edtUpdateCliEmail.setEnabled(false);
        edtUpdateCliSenha.setEnabled(false);
        btnUpdateCliAtualizar.setEnabled(false);
        btnUpdateCliCancelar.setEnabled(false);
    }

    private void ativarEditsButtons() {
        edtUpdateCliNome.setEnabled(true);
        edtUpdateCliCpf.setEnabled(true);
        edtUpdateCliEmail.setEnabled(true);
        edtUpdateCliSenha.setEnabled(true);
        btnUpdateCliAtualizar.setEnabled(true);
        btnUpdateCliCancelar.setEnabled(true);
    }

    private void iniciarProgressBar() {
        pbUpdateCli.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbUpdateCli, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}