package com.example.helpdesk.ui.cliente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.helpdesk.R;
import com.example.helpdesk.adapter.ClienteListAdapter;
import com.example.helpdesk.api.ApiService;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.model.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesListFragmentUI extends Fragment {
    private Button btnCadastrarCliente;
    private Button btnBuscarCliente;
    private SearchView svBuscarCliente;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Cliente> listClientes = new ArrayList<>();
    private ApiService apiService;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesListFragment = inflater.inflate(R.layout.fragment_clientes_list, container, false);

        btnCadastrarCliente = clientesListFragment.findViewById(R.id.btnCadastrarCliente);
        btnBuscarCliente = clientesListFragment.findViewById(R.id.btnBuscar);
        svBuscarCliente = clientesListFragment.findViewById(R.id.svBuscarCliente);
        recyclerView = clientesListFragment.findViewById(R.id.rvClientesList);
        progressBar = clientesListFragment.findViewById(R.id.pbClientesList);

        this.carregarClientes();

        return clientesListFragment;
    }

    private void carregarClientes() {
        apiService = ApiClient.getClient(getToken()).create(ApiService.class);
        Call<List<Cliente>> call = apiService.getClientes();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if(response.isSuccessful()) {
                    List<Cliente> listaClienteResponse = response.body();

                    for (Cliente cli : listaClienteResponse) {
                        Integer id = cli.getId();
                        String nome = cli.getNome();
                        String cpf = cli.getCpf();
                        String email = cli.getEmail();
                        String senha = cli.getSenha();
                        List<String> perfis = cli.getPerfis();
                        String dataCriacao = cli.getDataCriacao();
                        Cliente cliente = new Cliente(id, nome, cpf, email, senha, perfis, dataCriacao);
                        listClientes.add(cliente);
                    }
                    popularRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getToken(){
        preferences = getActivity().getSharedPreferences("HELPDESK",Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN",null);
        return token;
    }

    private void popularRecyclerView(){
        ClienteListAdapter adapter = new ClienteListAdapter(listClientes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

}