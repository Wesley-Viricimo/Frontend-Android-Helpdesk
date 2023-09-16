package com.example.helpdesk.ui.tecnicos;

import android.animation.ObjectAnimator;
import android.content.Context;
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
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Tecnico;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        apiService = ApiClient.getClient(getToken()).create(ApiService.class);
        Call<List<Tecnico>> call = apiService.getTecnicos();

        call.enqueue(new Callback<List<Tecnico>>() {
            @Override
            public void onResponse(Call<List<Tecnico>> call, Response<List<Tecnico>> response) {
                if(response.isSuccessful()) {
                    List<Tecnico> listaTecnicosResponse = response.body();

                    for (Tecnico tec : listaTecnicosResponse) {
                        Integer id = tec.getId();
                        String nome = tec.getNome();
                        String cpf = tec.getCpf();
                        String email = tec.getEmail();
                        String senha = tec.getSenha();
                        List<String> perfis = tec.getPerfis();
                        String dataCriacao = tec.getDataCriacao();
                        Tecnico tecnico = new Tecnico(id, nome, cpf, email, senha, perfis, dataCriacao);
                        listaTecnicosResponse.add(tecnico);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tecnico>> call, Throwable t) {

            }
        });

    }

    private String getToken(){
        preferences = getActivity().getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN",null);
        return token;
    }

}