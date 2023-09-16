package com.example.helpdesk.ui.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpdesk.R;

public class ClientesUpdateFragmentUI extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String idCliente = getArguments().getString("idCliente");

        return inflater.inflate(R.layout.fragment_clientes_update, container, false);
    }
}