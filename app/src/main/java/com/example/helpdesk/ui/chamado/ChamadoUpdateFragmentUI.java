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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Chamado;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.TokenUtil;
import com.example.helpdesk.util.ValidaCamposUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChamadoUpdateFragmentUI extends Fragment {
    private EditText edtChamadoUpdateTitulo;
    private EditText edtChamadoUpdateDescricao;
    private Spinner spnChamadoUpdateStatus;
    private Spinner spnChamadoUpdatePrioridade;
    private Spinner spnChamadoUpdateTecnico;
    private Spinner spnChamadoUpdateCliente;
    private Button btnChamadoUpdateAtualizar;
    private Button btnChamadoUpdateCancelar;
    private ProgressBar pbChamadoUpdate;
    private ApiService apiService;
    private Map<Integer, String> hashStatus = new HashMap<>();
    private Map<Integer, String> hashPrioridades = new HashMap<>();
    private Map<Integer, String> hashClientes = new TreeMap<>();
    private Map<Integer, String> hashTecnicos = new TreeMap<>();
    private Integer prioridadeSelecionada = 0;
    private Integer statusSelecionado = 0;
    private Integer clienteSelecionado = 0;
    private Integer tecnicoSelecionado = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String idChamado = getArguments().getString("idChamado");
        View chamadoUpdateFragment = inflater.inflate(R.layout.fragment_chamado_update, container, false);
        edtChamadoUpdateTitulo = chamadoUpdateFragment.findViewById(R.id.edtChamadoUpdateTitulo);
        edtChamadoUpdateDescricao = chamadoUpdateFragment.findViewById(R.id.edtChamadoUpdateDescricao);
        spnChamadoUpdateStatus = chamadoUpdateFragment.findViewById(R.id.spnChamadoUpdateStatus);
        spnChamadoUpdatePrioridade = chamadoUpdateFragment.findViewById(R.id.spnChamadoUpdatePrioridade);
        spnChamadoUpdateTecnico = chamadoUpdateFragment.findViewById(R.id.spnChamadoUpdateTecnico);
        spnChamadoUpdateCliente = chamadoUpdateFragment.findViewById(R.id.spnChamadoUpdateCliente);
        btnChamadoUpdateAtualizar = chamadoUpdateFragment.findViewById(R.id.btnChamadoUpdateAtualizar);
        btnChamadoUpdateCancelar = chamadoUpdateFragment.findViewById(R.id.btnChamadoUpdateCancelar);
        pbChamadoUpdate = chamadoUpdateFragment.findViewById(R.id.pbChamadoUpdate);

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();
        init(token);

        componentesAtivos(false);

       this.carregarChamado(idChamado, token);

        btnChamadoUpdateAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarChamado(idChamado, token);
            }
        });

        btnChamadoUpdateCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentChamadosList();
            }
        });

        return chamadoUpdateFragment;
    }

    private void atualizarChamado(String idChamado, String token) {
        String titulo = edtChamadoUpdateTitulo.getText().toString();
        String observacoes = edtChamadoUpdateDescricao.getText().toString();

        ValidaCamposUtil validaCamposUtil = new ValidaCamposUtil(getActivity());
        if(validaCamposUtil.validacoesChamado(titulo, observacoes, clienteSelecionado, tecnicoSelecionado)) {
            iniciarProgressBar();

            String nomeCliente = hashClientes.get(clienteSelecionado);
            String nomeTecnico = hashTecnicos.get(tecnicoSelecionado);

            Chamado chamado = new Chamado(prioridadeSelecionada, statusSelecionado, titulo, observacoes, clienteSelecionado, tecnicoSelecionado, nomeCliente, nomeTecnico);

            apiService = ApiClient.getClient(token).create(ApiService.class);
            Call<Void> call = apiService.putChamados(idChamado, chamado);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        requisicaoComSucesso();
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String erro = jObjError.getString("message");
                            requisicaoComErro(erro);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
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
                    edtChamadoUpdateTitulo.setText(chamado.getTitulo());
                    edtChamadoUpdateDescricao.setText(chamado.getObservacoes());
                    spnChamadoUpdateStatus.setSelection(chamado.getStatus());
                    spnChamadoUpdatePrioridade.setSelection(chamado.getPrioridade());
                    spnChamadoUpdateCliente.setSelection(getPosicaoCliente(chamado.getNomeCliente()));
                    spnChamadoUpdateTecnico.setSelection(getPosicaoTecnico(chamado.getNomeTecnico()));

                    sleepThread(true);
                    encerrarProgressBar();
                    componentesAtivos(true);
                }
            }

            @Override
            public void onFailure(Call<Chamado> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void init(String token) {
        carregarClientes(token);
        carregarTecnicos(token);
        carregarStatus();
        carregarPrioridades();
        finalizarLoadStatusPrioridade();
        validarClickStatus();
        validarClickPrioridade();
        validarClickCliente();
        validarClickTecnico();
    }

    private void requisicaoComSucesso() {
        sleepThread(true);
        encerrarProgressBar();
        Toast.makeText(getContext(), "Chamado atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        abrirFragmentChamadosList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread(true);
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_SHORT).show();
    }

    private void iniciarProgressBar() {
        pbChamadoUpdate.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbChamadoUpdate, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void encerrarProgressBar() {
        pbChamadoUpdate.clearAnimation();
        pbChamadoUpdate.setVisibility(View.GONE);
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

                    hashClientes.put(0, "Selecione um cliente!");

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

                    hashTecnicos.put(0, "Selecione um técnico!");

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
        ArrayAdapter arrayStatus = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashStatus.values().toArray());
        ArrayAdapter arrayPrioridade = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashPrioridades.values().toArray());
        arrayStatus.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        arrayPrioridade.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoUpdateStatus.setAdapter(arrayStatus);
        spnChamadoUpdatePrioridade.setAdapter(arrayPrioridade);
    }

    private void finalizarLoadClientes() {
        ArrayAdapter arrayCliente = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashClientes.values().toArray());
        arrayCliente.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoUpdateCliente.setAdapter(arrayCliente);
    }

    private void finalizarLoadTecnicos() {
        ArrayAdapter arrayTecnicos= new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashTecnicos.values().toArray());
        arrayTecnicos.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoUpdateTecnico.setAdapter(arrayTecnicos);
    }

    private void validarClickStatus() {
        spnChamadoUpdateStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = adapterView.getSelectedItem().toString();
                Integer codStatus = getKeyByValue(hashStatus, status);
                statusSelecionado = codStatus;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void validarClickPrioridade() {
        spnChamadoUpdatePrioridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String prioridade = adapterView.getSelectedItem().toString();
                Integer codPrioridade = getKeyByValue(hashPrioridades, prioridade);
                prioridadeSelecionada = codPrioridade;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void validarClickCliente() {
        spnChamadoUpdateCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String cliente = adapterView.getSelectedItem().toString();
                Integer idCliente = getKeyByValue(hashClientes, cliente);
                clienteSelecionado = idCliente;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void validarClickTecnico() {
        spnChamadoUpdateTecnico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tecnico = adapterView.getSelectedItem().toString();
                Integer idTecnico = getKeyByValue(hashTecnicos, tecnico);
                tecnicoSelecionado = idTecnico;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
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
        edtChamadoUpdateDescricao.setEnabled(isComponentesAtivos);
        edtChamadoUpdateTitulo.setEnabled(isComponentesAtivos);
        spnChamadoUpdateStatus.setEnabled(isComponentesAtivos);
        spnChamadoUpdatePrioridade.setEnabled(isComponentesAtivos);
        spnChamadoUpdateCliente.setEnabled(isComponentesAtivos);
        spnChamadoUpdateTecnico.setEnabled(isComponentesAtivos);
        btnChamadoUpdateAtualizar.setEnabled(isComponentesAtivos);
        btnChamadoUpdateCancelar.setEnabled(isComponentesAtivos);
    }

}