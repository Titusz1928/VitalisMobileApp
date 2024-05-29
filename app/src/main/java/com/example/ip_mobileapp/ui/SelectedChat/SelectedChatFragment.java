package com.example.ip_mobileapp.ui.SelectedChat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentChatBinding;
import com.example.ip_mobileapp.databinding.FragmentSelectedChatBinding;


public class SelectedChatFragment extends Fragment {

    private FragmentSelectedChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSelectedChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String name = getArguments().getString("prenume");
        Log.d("MyTag",name);
        TextView nameText = binding.tcvNameText;
        if(name!= null){
            nameText.setText(name);
        }else{
            Toast.makeText(requireContext(), getString(R.string.ERROR), Toast.LENGTH_LONG).show();
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}