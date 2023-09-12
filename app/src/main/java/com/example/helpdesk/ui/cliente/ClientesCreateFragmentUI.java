package com.example.helpdesk.ui.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.helpdesk.R;


public class ClientesCreateFragmentUI extends Fragment {

    private EditText edtCadCliNome;
    private EditText edtCadCliCpf;
    private EditText edtCadCliEmail;
    private EditText edtCadCliSenha;
    private Button btnCadCliCadastro;
    private Button btnCadCliCancelar;
    private ProgressBar pbCadCli;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View clientesListFragment = inflater.inflate(R.layout.fragment_clientes_create, container, false);

        return clientesListFragment;
    }
}