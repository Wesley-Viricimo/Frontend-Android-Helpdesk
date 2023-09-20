package com.example.helpdesk.ui.chamado;

import android.animation.ObjectAnimator;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Chamado;
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.TokenUtil;
import com.example.helpdesk.util.ValidaCamposUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChamadoCreateFragmentUI extends Fragment {
    private EditText edtChamadoCreateTitulo;
    private EditText edtChamadoCreateDescricao;
    private Spinner spnChamadoCreateStatus;
    private Spinner spnChamadoCreatePrioridade;
    private Spinner spnChamadoCreateTecnico;
    private Spinner spnChamadoCreateCliente;
    private Button btnChamadoCreateAbrir;
    private Button btnChamadoCreateCancelar;
    private ProgressBar pbChamadoCreate;
    private ApiService apiService;
    private Map<Integer, String> hashStatus = new HashMap<>();
    private Map<Integer, String> hashPrioridades = new HashMap<>();
    private Map<Integer, String> hashClientes = new HashMap<>();
    private Map<Integer, String> hashTecnicos = new HashMap<>();

    private Integer prioridadeSelecionada = 0;
    private Integer statusSelecionado = 0;
    private Integer clienteSelecionado = 0;
    private Integer tecnicoSelecionado = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View chamadoCreateFragment = inflater.inflate(R.layout.fragment_chamado_create, container, false);
        edtChamadoCreateTitulo = chamadoCreateFragment.findViewById(R.id.edtChamadoCreateTitulo);
        edtChamadoCreateDescricao = chamadoCreateFragment.findViewById(R.id.edtChamadoCreateDescricao);
        spnChamadoCreateStatus = chamadoCreateFragment.findViewById(R.id.spnChamadoCreateStatus);
        spnChamadoCreatePrioridade = chamadoCreateFragment.findViewById(R.id.spnChamadoCreatePrioridade);
        spnChamadoCreateTecnico = chamadoCreateFragment.findViewById(R.id.spnChamadoCreateTecnico);
        spnChamadoCreateCliente = chamadoCreateFragment.findViewById(R.id.spnChamadoCreateCliente);
        btnChamadoCreateAbrir = chamadoCreateFragment.findViewById(R.id.btnChamadoCreateAbrir);
        btnChamadoCreateCancelar = chamadoCreateFragment.findViewById(R.id.btnChamadoCreateCancelar);
        pbChamadoCreate = chamadoCreateFragment.findViewById(R.id.pbChamadoCreate);

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        iniciarProgressBar();

        init(token);

        btnChamadoCreateAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoCadastrarAtivo(false);
                validarAberturaChamado(token);
            }
        });

        btnChamadoCreateCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirFragmentChamadosList();
            }
        });

        return chamadoCreateFragment;
    }

    private void init(String token) {
        carregarStatus();
        carregarPrioridades();
        carregarClientes(token);
        carregarTecnicos(token);
        finalizarLoadStatusPrioridade();
        validarClickStatus();
        validarClickPrioridade();
        validarClickCliente();
        validarClickTecnico();
    }

    private void validarAberturaChamado(String token) {
        String titulo = edtChamadoCreateTitulo.getText().toString();
        String observacoes = edtChamadoCreateDescricao.getText().toString();

        ValidaCamposUtil validaCamposUtil = new ValidaCamposUtil(getActivity());

        if(validaCamposUtil.validacoesChamado(titulo, observacoes, clienteSelecionado, tecnicoSelecionado)) {
            iniciarProgressBar();

            String nomeCliente = hashClientes.get(clienteSelecionado);
            String nomeTecnico = hashTecnicos.get(tecnicoSelecionado);

            Chamado chamado = new Chamado(prioridadeSelecionada, statusSelecionado, titulo, observacoes, clienteSelecionado, tecnicoSelecionado, nomeCliente, nomeTecnico);

            apiService = ApiClient.getClient(token).create(ApiService.class);
            Call<Void> call = apiService.abrirChamado(chamado);

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
                    t.printStackTrace();
                }
            });
        } else {
            botaoCadastrarAtivo(true);
        }
    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Chamado aberto com sucesso!", Toast.LENGTH_SHORT).show();
        abrirFragmentChamadosList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_SHORT).show();
        botaoCadastrarAtivo(true);
    }

    private void iniciarProgressBar() {
        pbChamadoCreate.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbChamadoCreate, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void encerrarProgressBar() {
        pbChamadoCreate.clearAnimation();
        pbChamadoCreate.setVisibility(View.GONE);
    }

    private void sleepThread() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
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
        spnChamadoCreateStatus.setAdapter(arrayStatus);
        spnChamadoCreatePrioridade.setAdapter(arrayPrioridade);
    }

    private void finalizarLoadClientes() {
        ArrayAdapter arrayCliente = new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashClientes.values().toArray());
        arrayCliente.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoCreateCliente.setAdapter(arrayCliente);
        encerrarProgressBar();
    }

    private void finalizarLoadTecnicos() {
        ArrayAdapter arrayTecnicos= new ArrayAdapter(getActivity(), R.layout.spinner_item_custom, hashTecnicos.values().toArray());
        arrayTecnicos.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        spnChamadoCreateTecnico.setAdapter(arrayTecnicos);
        encerrarProgressBar();
    }

    private void validarClickStatus() {
        spnChamadoCreateStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spnChamadoCreatePrioridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spnChamadoCreateCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spnChamadoCreateTecnico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void botaoCadastrarAtivo(Boolean isAtivo) {
        btnChamadoCreateAbrir.setEnabled(isAtivo);
    }

    private Integer getKeyByValue(final Map<Integer, String> map, final String value) {
        return map.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(value))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}