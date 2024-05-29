package com.example.ip_mobileapp.ui.Visualization;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentVisualizationBinding;

public class VisualizationFragment extends Fragment {

    private FragmentVisualizationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentVisualizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] options = {"Tensiunea arterială", "Puls", "T. corporală", "Greutate", "Glicernie", "Lumină", "T. ambientală", "Umiditate", "Proximitate"};

        // Access the Spinner using the binding object
        Spinner spinner = binding.spinner;

        // Use requireContext() to get the context
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                String selectedItem = options[position];
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
