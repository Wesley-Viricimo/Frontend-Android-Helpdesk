package com.example.helpdesk.ui.tecnicos;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.helpdesk.R;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Cliente;

import java.util.ArrayList;
import java.util.List;

public class TecnicosListFragmentUI extends Fragment {
    private Button btnCadastrarTecnico;
    private Button btnBuscarTecnico;
    private EditText edtBuscarTecnico;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Cliente> listTecnicos = new ArrayList<>();
    private ApiService apiService;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tecnicosListFragment = inflater.inflate(R.layout.fragment_tecnicos_list, container, false);

        btnCadastrarTecnico = tecnicosListFragment.findViewById(R.id.btnCadastrarTecnico);
        btnBuscarTecnico = tecnicosListFragment.findViewById(R.id.btnBuscarTecnico);
        edtBuscarTecnico = tecnicosListFragment.findViewById(R.id.edtBuscarTecnico);
        recyclerView = tecnicosListFragment.findViewById(R.id.rvTecnicosList);
        progressBar = tecnicosListFragment.findViewById(R.id.pbTecnicosList);

        btnCadastrarTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        iniciarProgressBar();
        this.carregarTecnicos();


        return tecnicosListFragment;
    }

    private void iniciarProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void carregarTecnicos() {

    }

}