package com.example.helpdesk.ui.chamado;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.helpdesk.R;
import com.example.helpdesk.api.service.ApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ChamadoReadFragmentUI extends Fragment {

    private EditText edtChamadoReadTitulo;
    private EditText edtChamadoReadDescricao;
    private Spinner spnChamadoReadStatus;
    private Spinner spnChamadoReadPrioridade;
    private Spinner spnChamadoReadTecnico;
    private Spinner spnChamadoReadCliente;
    private Button btnChamadoReadAtualizar;
    private Button btnChamadoReadCancelar;
    private ProgressBar pbChamadoRead;
    private ApiService apiService;
    private Map<Integer, String> hashStatus = new HashMap<>();
    private Map<Integer, String> hashPrioridades = new HashMap<>();
    private Map<Integer, String> hashClientes = new TreeMap<>();
    private Map<Integer, String> hashTecnicos = new TreeMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chamado_read, container, false);
    }
}