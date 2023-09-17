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
import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.ui.cliente.ClientesDeleteFragmentUI;
import com.example.helpdesk.ui.cliente.ClientesUpdateFragmentUI;
import com.example.helpdesk.util.FuncoesUtil;

import java.util.List;

public class ClienteListAdapter extends RecyclerView.Adapter<ClienteListAdapter.ClienteListViewHolder> {
    private List<Cliente> listaClientes;
    private Context context;

    public ClienteListAdapter(List<Cliente> listaClientes, Context context) {
        this.listaClientes = listaClientes;
        this.context = context;
    }

    @NonNull
    @Override
    public ClienteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pessoa, parent, false);
        return new ClienteListViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteListViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        holder.tvId.setText("Id: " + Integer.toString(cliente.getId()));
        holder.tvNome.setText("Nome: " + cliente.getNome());
        holder.tvCpf.setText("CPF: " + FuncoesUtil.formataCPF(cliente.getCpf()));
        holder.tvEmail.setText("Email: " + cliente.getEmail());
        holder.tvDataCadastro.setText("Cadastrado em: " + cliente.getDataCriacao());
        Glide.with(context)
                .load(R.drawable.profile)
                .into(holder.ivPessoaImage);

        holder.btnAlterarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirClientesUpdate(view, cliente.getId().toString());
            }
        });

        holder.btnExcluirCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirClientesDelete(view, cliente.getId().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class ClienteListViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPessoaImage;
        public TextView tvId;
        public TextView tvNome;
        public TextView tvCpf;
        public TextView tvEmail;
        public TextView tvDataCadastro;
        private Button btnAlterarCliente;
        private Button btnExcluirCliente;

        public ClienteListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvNome = (TextView) itemView.findViewById(R.id.tvNome);
            tvCpf = (TextView) itemView.findViewById(R.id.tvCpf);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvDataCadastro = (TextView) itemView.findViewById(R.id.tvDataCadastro);
            btnAlterarCliente = (Button) itemView.findViewById(R.id.btnClienteListAlterar);
            btnExcluirCliente = (Button) itemView.findViewById(R.id.btnClienteListExcluir);
            ivPessoaImage = (ImageView) itemView.findViewById(R.id.ivPessoa);
        }
    }

    private void abrirClientesUpdate(View view, String idCliente) {
        Bundle bundle = new Bundle();
        bundle.putString("idCliente", idCliente);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragmentClientesUpdate = new ClientesUpdateFragmentUI();
        fragmentClientesUpdate.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentClientesUpdate).addToBackStack(null).commit();
    }

    private void abrirClientesDelete(View view, String idCliente) {
        Bundle bundle = new Bundle();
        bundle.putString("idCliente", idCliente);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragmentClientesDelete = new ClientesDeleteFragmentUI();
        fragmentClientesDelete.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentClientesDelete).addToBackStack(null).commit();
    }
}
