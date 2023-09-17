package com.example.helpdesk.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helpdesk.R;
import com.example.helpdesk.model.Tecnico;
import com.example.helpdesk.ui.tecnico.TecnicosDeleteFragmentUI;
import com.example.helpdesk.ui.tecnico.TecnicosUpdateFragmentUI;
import com.example.helpdesk.util.FuncoesUtil;

import java.util.List;

public class TecnicoListAdapter extends RecyclerView.Adapter<TecnicoListAdapter.TecnicoListViewHolder> {
    private List<Tecnico> listaTecnicos;
    private Context context;

    public TecnicoListAdapter(List<Tecnico> listaTecnicos, Context context) {
        this.listaTecnicos = listaTecnicos;
        this.context = context;
    }


    @NonNull
    @Override
    public TecnicoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pessoa, parent, false);
        return new TecnicoListViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull TecnicoListViewHolder holder, int position) {
        Tecnico tecnico = listaTecnicos.get(position);
        holder.tvId.setText("Id: " + Integer.toString(tecnico.getId()));
        holder.tvNome.setText("Nome: " + tecnico.getNome());
        holder.tvCpf.setText("CPF: " + FuncoesUtil.formataCPF(tecnico.getCpf()));
        holder.tvEmail.setText("Email: " + tecnico.getEmail());
        holder.tvDataCadastro.setText("Cadastrado em: " + tecnico.getDataCriacao());
        Glide.with(context)
                .load(R.drawable.profile)
                .into(holder.ivPessoaImage);

        holder.btnAlterarTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTecnicosUpdate(view, tecnico.getId().toString());
            }
        });

        holder.btnExcluirTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTecnicosDelete(view, tecnico.getId().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTecnicos.size();
    }

    public class TecnicoListViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPessoaImage;
        public TextView tvId;
        public TextView tvNome;
        public TextView tvCpf;
        public TextView tvEmail;
        public TextView tvDataCadastro;
        private Button btnAlterarTecnico;
        private Button btnExcluirTecnico;

        public TecnicoListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvNome = (TextView) itemView.findViewById(R.id.tvNome);
            tvCpf = (TextView) itemView.findViewById(R.id.tvCpf);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvDataCadastro = (TextView) itemView.findViewById(R.id.tvDataCadastro);
            btnAlterarTecnico = (Button) itemView.findViewById(R.id.btnClienteListAlterar);
            btnExcluirTecnico = (Button) itemView.findViewById(R.id.btnClienteListExcluir);
            ivPessoaImage = (ImageView) itemView.findViewById(R.id.ivPessoa);
        }
    }

    private void abrirTecnicosUpdate(View view, String idTecnico) {
        Bundle bundle = new Bundle();
        bundle.putString("idTecnico", idTecnico);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragmentClientesUpdate = new TecnicosUpdateFragmentUI();
        fragmentClientesUpdate.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentClientesUpdate).addToBackStack(null).commit();
    }

    private void abrirTecnicosDelete(View view, String idTecnico) {
        Bundle bundle = new Bundle();
        bundle.putString("idTecnico", idTecnico);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragmentClientesDelete = new TecnicosDeleteFragmentUI();
        fragmentClientesDelete.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentClientesDelete).addToBackStack(null).commit();
    }
}
