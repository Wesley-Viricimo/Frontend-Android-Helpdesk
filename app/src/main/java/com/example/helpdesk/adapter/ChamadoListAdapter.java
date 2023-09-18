package com.example.helpdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helpdesk.R;
import com.example.helpdesk.model.Chamado;

import java.util.List;

public class ChamadoListAdapter extends RecyclerView.Adapter<ChamadoListAdapter.ChamadoListViewHolder> {

    private List<Chamado> listaChamados;
    private Context context;

    public ChamadoListAdapter(List<Chamado> listaChamados, Context context) {
        this.listaChamados = listaChamados;
        this.context = context;
    }

    @NonNull
    @Override
    public ChamadoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chamado, parent, false);
        return new ChamadoListViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoListViewHolder holder, int position) {
        Chamado chamado = listaChamados.get(position);
        String dataFechamento = chamado.getDataFechamento();
        String prioridade = retornaPrioridade(chamado.getPrioridade());
        String status = retornaStatus(chamado.getStatus());

        if(dataFechamento == null) {
            dataFechamento = "";
        }

        holder.tvIdChamado.setText("Id: " + Integer.toString(chamado.getId()));
        holder.tvTituloChamado.setText("Título: " + chamado.getTitulo());
        holder.tvDescricaoChamado.setText("Descrição: " + chamado.getObservacoes());
        holder.tvPrioridadeChamado.setText("Prioridade: " + prioridade);
        holder.tvStatusChamado.setText("Status: " + status);
        holder.tvClienteChamado.setText("Cliente: " + chamado.getNomeCliente());
        holder.tvTecnicoChamado.setText("Técnico: " + chamado.getNomeTecnico());
        holder.tvDataAberturaChamado.setText("Aberto em: " + chamado.getDataAbertura());
        holder.tvDataFechamentoChamado.setText("Encerrado em: " + dataFechamento);
        Glide.with(context)
                .load(R.drawable.chamado)
                .into(holder.ivChamado);
    }

    @Override
    public int getItemCount() {
        return listaChamados.size();
    }

    public class ChamadoListViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivChamado;
        public TextView tvIdChamado;
        public TextView tvTituloChamado;
        public TextView tvDescricaoChamado;
        public TextView tvPrioridadeChamado;
        public TextView tvStatusChamado;
        public TextView tvClienteChamado;
        public TextView tvTecnicoChamado;
        public TextView tvDataAberturaChamado;
        public TextView tvDataFechamentoChamado;
        private Button btnChamadoListAlterar;
        private Button btnChamadoListExcluir;

        public ChamadoListViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChamado = (ImageView) itemView.findViewById(R.id.ivChamado);
            tvIdChamado = (TextView) itemView.findViewById(R.id.tvIdChamado);
            tvTituloChamado = (TextView) itemView.findViewById(R.id.tvTituloChamado);
            tvDescricaoChamado = (TextView) itemView.findViewById(R.id.tvDescricaoChamado);
            tvPrioridadeChamado = (TextView) itemView.findViewById(R.id.tvPrioridadeChamado);
            tvStatusChamado = (TextView) itemView.findViewById(R.id.tvStatusChamado);
            tvClienteChamado = (TextView) itemView.findViewById(R.id.tvClienteChamado);
            tvTecnicoChamado = (TextView) itemView.findViewById(R.id.tvTecnicoChamado);
            tvDataAberturaChamado = (TextView) itemView.findViewById(R.id.tvDataAberturaChamado);
            tvDataFechamentoChamado = (TextView) itemView.findViewById(R.id.tvDataFechamentoChamado);
            btnChamadoListAlterar = (Button) itemView.findViewById(R.id.btnChamadoListAlterar);
            btnChamadoListExcluir = (Button) itemView.findViewById(R.id.btnChamadoListExcluir);
        }
    }

    private String retornaPrioridade(String numPrioridade) {
        String prioridade = "";
        switch (numPrioridade) {
            case "0" : prioridade = "BAIXA";
            break;
            case "1" : prioridade = "MÉDIA";
            break;
            case "2" : prioridade = "ALTA";
            default:
                break;
        }
        return prioridade;
    }

    private String retornaStatus(String numStatus) {
        String status = "";
        switch (numStatus) {
            case "0" : status = "ABERTO";
                break;
            case "1" : status = "EM ANDAMENTO";
                break;
            case "2" : status = "ENCERRADO";
            default:
                break;
        }
        return status;
    }

}
