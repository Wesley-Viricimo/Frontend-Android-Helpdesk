package com.example.helpdesk.ui.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.helpdesk.R;

public class ClientesListFragmentUI extends Fragment {
    private Button btnCadastrarCliente;
    private Button btnBuscarCliente;
    private SearchView svBuscarCliente;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesListFragment = inflater.inflate(R.layout.fragment_clientes_list, container, false);

        btnCadastrarCliente = clientesListFragment.findViewById(R.id.btnCadastrarCliente);
        btnBuscarCliente = clientesListFragment.findViewById(R.id.btnBuscar);
        svBuscarCliente = clientesListFragment.findViewById(R.id.svBuscarCliente);
        recyclerView = clientesListFragment.findViewById(R.id.rvClientesList);
        progressBar = clientesListFragment.findViewById(R.id.pbClientesList);

        btnCadastrarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("teste");
            }
        });

        return clientesListFragment;
    }
}