package com.example.helpdesk.ui.chamado;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpdesk.R;

public class ChamadoUpdateFragmentUI extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chamadoUpdateFragment = inflater.inflate(R.layout.fragment_chamado_update, container, false);


        return chamadoUpdateFragment;
    }
}