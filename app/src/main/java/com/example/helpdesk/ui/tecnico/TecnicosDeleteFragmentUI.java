package com.example.helpdesk.ui.tecnico;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helpdesk.R;
import com.example.helpdesk.api.client.ApiClient;
import com.example.helpdesk.api.service.ApiService;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.ui.cliente.ClientesListFragmentUI;
import com.example.helpdesk.util.MaskEditUtil;
import com.example.helpdesk.util.TokenUtil;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnicosDeleteFragmentUI extends Fragment {

    private EditText edtDeleteTecNome;
    private EditText edtDeleteTecCpf;
    private EditText edtDeleteTecEmail;
    private EditText edtDeleteTecSenha;
    private Button btnDeleteTecDeletar;
    private Button btnDeleteTecCancelar;
    private ProgressBar pbDeleteTec;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tecnicosDeleteFragment = inflater.inflate(R.layout.fragment_tecnicos_delete, container, false);

        String idTecnico = getArguments().getString("idTecnico");
        edtDeleteTecNome = tecnicosDeleteFragment.findViewById(R.id.edtDeleteTecNome);
        edtDeleteTecCpf = tecnicosDeleteFragment.findViewById(R.id.edtDeleteTecCpf);
        edtDeleteTecEmail = tecnicosDeleteFragment.findViewById(R.id.edtDeleteTecEmail);
        edtDeleteTecSenha = tecnicosDeleteFragment.findViewById(R.id.edtDeleteTecSenha);
        btnDeleteTecDeletar = tecnicosDeleteFragment.findViewById(R.id.btnDeleteTecDeletar);
        btnDeleteTecCancelar = tecnicosDeleteFragment.findViewById(R.id.btnDeleteTecCancelar);
        pbDeleteTec = tecnicosDeleteFragment.findViewById(R.id.pbDeleteTec);

        edtDeleteTecCpf.addTextChangedListener(MaskEditUtil.mask(edtDeleteTecCpf, MaskEditUtil.FORMAT_CPF));

        TokenUtil tokenUtil = new TokenUtil(getActivity());
        String token = tokenUtil.getAcessToken();

        componentesAtivos(false);
        iniciarProgressBar();

        this.carregarTecnico(idTecnico, token);

        btnDeleteTecDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarTecnico(idTecnico, token);
            }
        });

        btnDeleteTecCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornarTecnicoList();
            }
        });

        return tecnicosDeleteFragment;
    }

    private void carregarTecnico(String idTecnico, String token) {
        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<Tecnico> call = apiService.getTecnico(idTecnico);

        call.enqueue(new Callback<Tecnico>() {
            @Override
            public void onResponse(Call<Tecnico> call, Response<Tecnico> response) {
                if(response.isSuccessful()) {
                    Tecnico tec = response.body();
                    edtDeleteTecNome.setText(tec.getNome());
                    edtDeleteTecCpf.setText(tec.getCpf());
                    edtDeleteTecEmail.setText(tec.getEmail());
                    edtDeleteTecSenha.setText(tec.getSenha());

                    sleepThread();
                    encerrarProgressBar();
                    ativarButtons();
                }
            }

            @Override
            public void onFailure(Call<Tecnico> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void deletarTecnico(String idTecnico, String token) {
        iniciarProgressBar();

        apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<Void> call = apiService.deleteTecnico(idTecnico);

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
    }

    private void requisicaoComSucesso() {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getContext(), "Técnico deletado com sucesso!", Toast.LENGTH_SHORT).show();
        retornarTecnicoList();
    }

    private void requisicaoComErro(String erro) {
        sleepThread();
        encerrarProgressBar();
        Toast.makeText(getActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void retornarTecnicoList() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new TecnicosListFragmentUI()).commit();
    }

    private void sleepThread() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encerrarProgressBar() {
        pbDeleteTec.clearAnimation();
        pbDeleteTec.setVisibility(View.GONE);
    }


    private void componentesAtivos(boolean isComponentesAtivos) {
        edtDeleteTecNome.setEnabled(isComponentesAtivos);
        edtDeleteTecCpf.setEnabled(isComponentesAtivos);
        edtDeleteTecEmail.setEnabled(isComponentesAtivos);
        edtDeleteTecSenha.setEnabled(isComponentesAtivos);
        btnDeleteTecDeletar.setEnabled(isComponentesAtivos);
        btnDeleteTecCancelar.setEnabled(isComponentesAtivos);
    }

    private void ativarButtons() {
        btnDeleteTecDeletar.setEnabled(true);
        btnDeleteTecCancelar.setEnabled(true);
    }

    private void iniciarProgressBar() {
        pbDeleteTec.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbDeleteTec, "progress", 0, 300);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

}