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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.util.MaskEditUtil;
import com.example.helpdesk.util.TokenUtil;
import com.example.helpdesk.util.ValidaCamposUtil;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnicosUpdateFragmentUI extends Fragment {
    private EditText edtUpdateTecNome;
    private EditText edtUpdateTecCpf;
    private EditText edtUpdateTecEmail;
    private EditText edtUpdateTecSenha;
    private Button btnUpdateTecAtualizar;
    private Button btnUpdateTecCancelar;
    private ProgressBar pbUpdateTec;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tecnicosUpdateFragment = inflater.inflate(R.layout.fragment_tecnicos_update, container, false);

        String idTecnico = getArguments().getString("idTecnico");
        edtUpdateTecNome = tecnicosUpdateFragment.findViewById(R.id.edtUpdateTecNome);
        edtUpdateTecCpf = tecnicosUpdateFragment.findViewById(R.id.edtUpdateTecCpf);
        edtUpdateTecEmail = tecnicosUpdateFragment.findViewById(R.id.edtUpdateTecEmail);
        edtUpdateTecSenha = tecnicosUpdateFragment.findViewById(R.id.edtUpdateTecSenha);
        btnUpdateTecAtualizar = tecnicosUpdateFragment.findViewById(R.id.btnUpdateTecAtualizar);
        btnUpdateTecCancelar = tecnicosUpdateFragment.findViewById(R.id.btnUpdateTecCancelar);
        pbUpdateTec = tecnicosUpdateFragment.findViewById(R.id.pbUpdateTec);

        edtUpdateTecCpf.addTextChangedListener(MaskEditUtil.mask(edtUpdateTecCpf, MaskEditUtil.FORMAT_CPF));

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        componentesAtivos(false);
        iniciarProgressBar();
        this.carregarTecnico(idTecnico, token);

        btnUpdateTecAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarTecnico(idTecnico, token);
            }
        });

        btnUpdateTecCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornarTecnicoList();
            }
        });

        return tecnicosUpdateFragment;
    }

    private void carregarTecnico(String idTecnico, String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<Tecnico> call = apiService.getTecnico(idTecnico);

        call.enqueue(new Callback<Tecnico>() {
            @Override
            public void onResponse(Call<Tecnico> call, Response<Tecnico> response) {
                if (response.isSuccessful()) {
                    Tecnico tec = response.body();
                    edtUpdateTecNome.setText(tec.getNome());
                    edtUpdateTecCpf.setText(tec.getCpf());
                    edtUpdateTecEmail.setText(tec.getEmail());
                    edtUpdateTecSenha.setText(tec.getSenha());

                    sleepThread();
                    encerrarProgressBar();
                    componentesAtivos(true);
                }
            }

            @Override
            public void onFailure(Call<Tecnico> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void atualizarTecnico(String idTecnico, String token) {
        iniciarProgressBar();

        String nome = edtUpdateTecNome.getText().toString();
        String cpf = edtUpdateTecCpf.getText().toString();
        String email = edtUpdateTecEmail.getText().toString();
        String senha = edtUpdateTecSenha.getText().toString();

        ValidaCamposUtil validaCampos = new ValidaCamposUtil(getActivity());

        if (validaCampos.validaCampos(nome, cpf, email, senha)) {
            Tecnico tecnico = new Tecnico(nome, cpf, email, senha);
            apiService = ApiClient.getClient(token).create(ApiService.class);
            Call<Void> call = apiService.putTecnico(idTecnico, tecnico);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
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
            encerrarProgressBar();
        }

    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Técnico atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        retornarTecnicoList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        pbUpdateTec.clearAnimation();
        pbUpdateTec.setVisibility(View.GONE);
    }

    private void componentesAtivos(boolean isComponentesAtivos) {
        edtUpdateTecNome.setEnabled(isComponentesAtivos);
        edtUpdateTecCpf.setEnabled(isComponentesAtivos);
        edtUpdateTecEmail.setEnabled(isComponentesAtivos);
        edtUpdateTecSenha.setEnabled(isComponentesAtivos);
        btnUpdateTecAtualizar.setEnabled(isComponentesAtivos);
        btnUpdateTecCancelar.setEnabled(isComponentesAtivos);
    }

    private void iniciarProgressBar() {
        pbUpdateTec.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbUpdateTec, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void retornarTecnicoList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TecnicosListFragmentUI()).commit();
    }
}