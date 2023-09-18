package com.example.helpdesk.ui.chamado;

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
import com.example.helpdesk.adapter.ChamadoListAdapter;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Chamado;
import com.example.helpdesk.ui.cliente.ClientesCreateFragmentUI;
import com.example.helpdesk.util.TokenUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChamadoListFragmentUI extends Fragment {
    private Button btnAbrirChamado;
    private Button btnBuscarChamado;
    private EditText edtBuscarChamado;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Chamado> listChamados = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View chamadosListFragment = inflater.inflate(R.layout.fragment_chamado_list, container, false);

        btnAbrirChamado = chamadosListFragment.findViewById(R.id.btnAbrirChamado);
        btnBuscarChamado = chamadosListFragment.findViewById(R.id.btnBuscarChamado);
        edtBuscarChamado = chamadosListFragment.findViewById(R.id.edtBuscarChamado);
        recyclerView = chamadosListFragment.findViewById(R.id.rvChamadosList);
        progressBar = chamadosListFragment.findViewById(R.id.pbChamadosList);

        btnAbrirChamado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentChamadoCreate();
            }
        });

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();
        this.carregarChamados(token);

        return chamadosListFragment;
    }

    private void carregarChamados(String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<List<Chamado>> call = apiService.getChamados();

        call.enqueue(new Callback<List<Chamado>>() {
            @Override
            public void onResponse(Call<List<Chamado>> call, Response<List<Chamado>> response) {
                if(response.isSuccessful()) {
                    List<Chamado> chamados = response.body();

                    for(Chamado cham : chamados) {
                        Integer id = cham.getId();
                        String dataAbertura = cham.getDataAbertura();
                        String dataFechamento = cham.getDataFechamento();
                        String prioridade = cham.getPrioridade();
                        String status = cham.getStatus();
                        String titulo = cham.getTitulo();
                        String observacoes = cham.getObservacoes();
                        String tecnico = cham.getNomeTecnico();
                        String cliente = cham.getNomeCliente();
                        Chamado chamado = new Chamado(id, dataAbertura, dataFechamento, prioridade, status, titulo, observacoes, tecnico, cliente);
                        listChamados.add(chamado);
                    }
                    popularRecyclerView();
                    sleepThread();
                    encerrarProgressBar();
                }
            }

            @Override
            public void onFailure(Call<List<Chamado>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void popularRecyclerView(){
        ChamadoListAdapter adapter = new ChamadoListAdapter(listChamados, this.getContext());
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

    private void abrirFragmentChamadoCreate() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ChamadoCreateFragmentUI()).commit();
    }
}