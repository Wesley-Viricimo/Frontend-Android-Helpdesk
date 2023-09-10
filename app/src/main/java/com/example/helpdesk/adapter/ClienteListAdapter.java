package com.example.helpdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpdesk.R;
import com.example.helpdesk.model.Cliente;

import java.time.LocalDate;
import java.util.List;

public class ClienteListAdapter extends RecyclerView.Adapter<ClienteListAdapter.ClienteListViewHolder> {
    private List<Cliente> listaClientes;

    public ClienteListAdapter(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public ClienteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pessoa, parent, false);
        return new ClienteListViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteListViewHolder holder, int position) {
        //TODO FALTA MANIPULAR A IMAGEM
        Cliente cliente = listaClientes.get(position);
        holder.tvId.setText("ID: " + Integer.toString(cliente.getId()));
        holder.tvNome.setText("Nome: " + cliente.getNome());
        holder.tvEmail.setText("Email: " + cliente.getEmail());
        holder.tvDataCadastro.setText("Cadastrado em: " + cliente.getDataCriacao());

    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class ClienteListViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPessoaImage;
        public TextView tvId;
        public TextView tvNome;
        public TextView tvEmail;
        public TextView tvDataCadastro;

        public ClienteListViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPessoaImage = (ImageView) itemView.findViewById(R.id.ivPessoaImage);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvNome = (TextView) itemView.findViewById(R.id.tvNome);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvDataCadastro = (TextView) itemView.findViewById(R.id.tvDataCadastro);
        }
    }
}
