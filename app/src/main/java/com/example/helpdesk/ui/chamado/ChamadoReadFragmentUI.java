package com.example.helpdesk.ui.chamado;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Chamado;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.TokenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChamadoReadFragmentUI extends Fragment {

    private EditText edtChamadoReadTitulo;
    private EditText edtChamadoReadDescricao;
    private Spinner spnChamadoReadStatus;
    private Spinner spnChamadoReadPrioridade;
    private Spinner spnChamadoReadTecnico;
    private Spinner spnChamadoReadCliente;
    private Button btnChamadoReadVoltar;
    private ProgressBar pbChamadoRead;
    private ApiService apiService;
    private Map<Integer, String> hashStatus = new HashMap<>();
    private Map<Integer, String> hashPrioridades = new HashMap<>();
    private Map<Integer, String> hashClientes = new TreeMap<>();
    private Map<Integer, String> hashTecnicos = new TreeMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String idChamado = getArguments().getString("idChamado");
        View chamadoReadFragment = inflater.inflate(R.layout.fragment_chamado_read, container, false);
        edtChamadoReadTitulo = chamadoReadFragment.findViewById(R.id.edtChamadoReadTitulo);
        edtChamadoReadDescricao = chamadoReadFragment.findViewById(R.id.edtChamadoReadDescricao);
        spnChamadoReadStatus = chamadoReadFragment.findViewById(R.id.spnChamadoReadStatus);
        spnChamadoReadPrioridade = chamadoReadFragment.findViewById(R.id.spnChamadoReadPrioridade);
        spnChamadoReadTecnico = chamadoReadFragment.findViewById(R.id.spnChamadoReadTecnico);
        spnChamadoReadCliente = chamadoReadFragment.findViewById(R.id.spnChamadoReadCliente);
        btnChamadoReadVoltar = chamadoReadFragment.findViewById(R.id.btnChamadoReadVoltar);
        pbChamadoRead = chamadoReadFragment.findViewById(R.id.pbChamadoRead);

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();
        init(token);

        componentesAtivos(false);

        this.carregarChamado(idChamado, token);

        btnChamadoReadVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentChamadosList();
            }
        });

        return chamadoReadFragment;
    }

    private void init(String token) {
        carregarClientes(token);
        carregarTecnicos(token);
        carregarStatus();
        carregarPrioridades();
        finalizarLoadStatusPrioridade();
    }

    private void carregarChamado(String idChamado, String token) {
        sleepThread(false);
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<Chamado> call = apiService.getChamado(idChamado);

        call.enqueue(new Callback<Chamado>() {
            @Override
            public void onResponse(Call<Chamado> call, Response<Chamado> response) {
                if(response.isSuccessful()) {
                    Chamado chamado = response.body();
                    edtChamadoReadTitulo.setText(chamado.getTitulo());
                    edtChamadoReadDescricao.setText(chamado.getObservacoes());
                    spnChamadoReadStatus.setSelection(chamado.getStatus());
                    spnChamadoReadPrioridade.setSelection(chamado.getPrioridade());
                    spnChamadoReadCliente.setSelection(getPosicaoCliente(chamado.getNomeCliente()));
                    spnChamadoReadTecnico.setSelection(getPosicaoTecnico(chamado.getNomeTecnico()));

                    sleepThread(true);
                    encerrarProgressBar();
                    ativarBotaoVoltar(true);
                }
            }

            @Override
            public void onFailure(Call<Chamado> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void iniciarProgressBar() {
        pbChamadoRead.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbChamadoRead, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void encerrarProgressBar() {
        pbChamadoRead.clearAnimation();
        pbChamadoRead.setVisibility(View.GONE);
    }

    private void sleepThread(boolean maisTempo) {
        if(maisTempo) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirFragmentChamadosList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ChamadoListFragmentUI()).commit();
    }

    private void carregarStatus() {
        hashStatus.put(0 , "ABERTO");
        hashStatus.put(1, "ANDAMENTO");
        hashStatus.put(2, "ENCERRADO");
    }

    private void carregarPrioridades() {
        hashPrioridades.put(0, "BAIXA");
        hashPrioridades.put(1, "MEDIA");
        hashPrioridades.put(2, "ALTA");
    }

    private void carregarClientes(String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<List<Cliente>> call = apiService.getClientes();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if(response.isSuccessful()) {
                    List<Cliente> listaClientes = response.body();

                    for(Cliente cli : listaClientes) {
                        Integer id = cli.getId();
                        String nome = cli.getNome();
                        hashClientes.put(id, nome);
                    }
                    finalizarLoadClientes();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void carregarTecnicos(String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<List<Tecnico>> call = apiService.getTecnicos();

        call.enqueue(new Callback<List<Tecnico>>() {
            @Override
            public void onResponse(Call<List<Tecnico>> call, Response<List<Tecnico>> response) {
                if(response.isSuccessful()) {
                    List<Tecnico> listaTecnicos = response.body();

                    for(Tecnico tec : listaTecnicos) {
                        Integer id = tec.getId();
                        String nome = tec.getNome();
                        hashTecnicos.put(id, nome);
                    }
                    finalizarLoadTecnicos();
                }
            }

            @Override
            public void onFailure(Call<List<Tecnico>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void finalizarLoadStatusPrioridade() {
        ArrayAdapter arrayStatus = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom_disabled, hashStatus.values().toArray());
        ArrayAdapter arrayPrioridade = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom_disabled, hashPrioridades.values().toArray());
        arrayStatus.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        arrayPrioridade.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoReadStatus.setAdapter(arrayStatus);
        spnChamadoReadPrioridade.setAdapter(arrayPrioridade);
    }

    private void finalizarLoadClientes() {
        ArrayAdapter arrayCliente = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom_disabled, hashClientes.values().toArray());
        arrayCliente.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoReadCliente.setAdapter(arrayCliente);
    }

    private void finalizarLoadTecnicos() {
        ArrayAdapter arrayTecnicos= new ArrayAdapter(getActivity(), R.layout.spinner_item_custom_disabled, hashTecnicos.values().toArray());
        arrayTecnicos.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoReadTecnico.setAdapter(arrayTecnicos);
    }
    private Integer getKeyByValue(final Map<Integer, String> map, final String value) {
        return map.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(value))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Integer getPosicaoCliente(String nomeCliente) {
        List<String> indexesCliente = new ArrayList<>(hashClientes.values());
        return indexesCliente.indexOf(nomeCliente);
    }

    private Integer getPosicaoTecnico(String nomeTecnico) {
        List<String> indexesTecnico = new ArrayList<>(hashTecnicos.values());
        return indexesTecnico.indexOf(nomeTecnico);
    }

    private void componentesAtivos(boolean isComponentesAtivos) {
        edtChamadoReadDescricao.setEnabled(isComponentesAtivos);
        edtChamadoReadTitulo.setEnabled(isComponentesAtivos);
        spnChamadoReadStatus.setEnabled(isComponentesAtivos);
        spnChamadoReadPrioridade.setEnabled(isComponentesAtivos);
        spnChamadoReadCliente.setEnabled(isComponentesAtivos);
        spnChamadoReadTecnico.setEnabled(isComponentesAtivos);
        btnChamadoReadVoltar.setEnabled(isComponentesAtivos);
    }

    private void ativarBotaoVoltar(boolean isBotaoAtivo) {
        btnChamadoReadVoltar.setEnabled(isBotaoAtivo);
    }

}