package com.example.helpdesk.ui.tecnico;

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
import com.example.helpdesk.adapter.TecnicoListAdapter;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.TokenUtil;

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
    private List<Tecnico> listTecnicos = new ArrayList<>();
    private ApiService apiService;

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
                abrirFragmentCadastroTecnico();
            }
        });

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();
        this.carregarTecnicos(token);

        return tecnicosListFragment;
    }

    private void iniciarProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void carregarTecnicos(String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
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
                        String fotoPerfil = tec.getFotoPerfil();
                        Tecnico tecnico = new Tecnico(id, nome, cpf, email, senha, perfis, dataCriacao, fotoPerfil);
                        listTecnicos.add(tecnico);
                    }
                    popularRecyclerView();
                    sleepThread();
                    encerrarProgressBar();
                }
            }

            @Override
            public void onFailure(Call<List<Tecnico>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void abrirFragmentCadastroTecnico() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TecnicosCreateFragmentUI()).commit();
    }

    private void popularRecyclerView(){
        TecnicoListAdapter adapter = new TecnicoListAdapter(listTecnicos, this.getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        progressBar.clearAnimation();
        progressBar.setVisibility(View.GONE);
    }

}