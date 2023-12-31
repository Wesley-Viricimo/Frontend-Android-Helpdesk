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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpdesk.R;
import com.example.helpdesk.adapter.ClienteListAdapter;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.util.TokenUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesListFragmentUI extends Fragment {
    private Button btnCadastrarCliente;
    private Button btnBuscarCliente;
    private EditText edtBuscarCliente;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Cliente> listClientes = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesListFragment = inflater.inflate(R.layout.fragment_clientes_list, container, false);

        btnCadastrarCliente = clientesListFragment.findViewById(R.id.btnCadastrarCliente);
        btnBuscarCliente = clientesListFragment.findViewById(R.id.btnBuscarCliente);
        edtBuscarCliente = clientesListFragment.findViewById(R.id.edtBuscarCliente);
        recyclerView = clientesListFragment.findViewById(R.id.rvClientesList);
        progressBar = clientesListFragment.findViewById(R.id.pbClientesList);

        btnCadastrarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentCadastroCliente();
            }
        });

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();
        this.carregarClientes(token);

        return clientesListFragment;
    }

    private void carregarClientes(String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<List<Cliente>> call = apiService.getClientes();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if(response.isSuccessful()) {
                    List<Cliente> listaClientesResponse = response.body();

                    for (Cliente cli : listaClientesResponse) {
                        Integer id = cli.getId();
                        String nome = cli.getNome();
                        String cpf = cli.getCpf();
                        String email = cli.getEmail();
                        String senha = cli.getSenha();
                        List<String> perfis = cli.getPerfis();
                        String dataCriacao = cli.getDataCriacao();
                        String fotoPerfil = cli.getFotoPerfil();
                        Cliente cliente = new Cliente(id, nome, cpf, email, senha, perfis, dataCriacao, fotoPerfil);
                        listClientes.add(cliente);
                    }
                    popularRecyclerView();
                    sleepThread();
                    encerrarProgressBar();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void abrirFragmentCadastroCliente() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ClientesCreateFragmentUI()).commit();
    }

    private void popularRecyclerView(){
        ClienteListAdapter adapter = new ClienteListAdapter(listClientes, this.getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void iniciarProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void encerrarProgressBar() {
        progressBar.clearAnimation();
        progressBar.setVisibility(View.GONE);
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}