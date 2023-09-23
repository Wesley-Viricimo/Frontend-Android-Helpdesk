package com.example.helpdesk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.helpdesk.R;

public class HomeFragmentUI extends Fragment {
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);

        imageView = homeFragment.findViewById(R.id.ivHome);

        Glide.with(this)
                .load(R.drawable.home_image)
                .into(imageView);

        return homeFragment;
    }
}