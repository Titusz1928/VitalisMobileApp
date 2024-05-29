package com.example.ip_mobileapp.ui.Recomandare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentRecomandareBinding;
import com.example.ip_mobileapp.databinding.FragmentSettingsBinding;


public class RecomandareFragment extends Fragment {

    private FragmentRecomandareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRecomandareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String date = getArguments().getString("date");
        String diagnostic = getArguments().getString("diagnostic");
        String cure = getArguments().getString("cure");
        String recom = getArguments().getString("recomandare");

        TextView dateText = binding.irDateText;
        TextView diagText = binding.irTitleText;
        TextView cureText = binding.tcvTratamentText;
        TextView recomText = binding.svDescriptionText;

        dateText.setText(date);
        diagText.setText(diagnostic);
        cureText.setText(cure);
        recomText.setText(recom);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}