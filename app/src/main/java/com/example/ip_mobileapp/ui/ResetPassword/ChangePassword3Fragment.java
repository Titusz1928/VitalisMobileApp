package com.example.ip_mobileapp.ui.ResetPassword;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentChangePassword2Binding;
import com.example.ip_mobileapp.databinding.FragmentChangePassword3Binding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword3Fragment extends Fragment {
    private FragmentChangePassword3Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentChangePassword3Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button changePswdBtn = binding.RPA3ifcvConfirmButton;

        Bundle args = getArguments();
        String emailString;
        if (args != null) {
            emailString = args.getString("email");
            Log.d("MyTag",emailString);
        } else {
            emailString = null;
            redirectToLogin();
        }

        TextView newPasswordText = binding.tilNew;
        TextView confirmNewPasswordText = binding.tilConfirm;


        changePswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newString = newPasswordText.getText().toString();
                String confirmString =confirmNewPasswordText.getText().toString();
                Thread thread = new Thread(() -> {
                    try {
                        if(newString!=null){
                            if(newString.equals(confirmString)) {
                                RestTemplate restTemplate = new RestTemplate();
                                Map<String, String> requestParams = new HashMap<>();
                                requestParams.put("emailAddress", emailString);
                                requestParams.put("newPassword", newString);

                                String url = getString(R.string.CLOUD_SERVER) + getString(R.string.CHANGE_PASSWORD);
                                ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                        requestParams, Void.class);

                                if (response.getStatusCode().is2xxSuccessful()) {
                                    Log.d("MyTag", "password changed ");
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(() -> {
                                        Toast.makeText(getActivity(), getString(R.string.PASSWORD_CHANGE), Toast.LENGTH_LONG).show();
                                    });

                                    ((LoginActivity) getActivity()).switchFragment(new ResetPassword3Fragment());
                                } else {
                                    Log.d("MyTag", "error ");
                                    Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            Toast.makeText(getActivity(), getString(R.string.ERROR),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
                thread.start();

                //((LoginActivity) getActivity()).switchFragment(new LoginFragment());
            }
        });









        return root;
    }

    private void redirectToLogin() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(getActivity(), getString(R.string.ERROR),
                    Toast.LENGTH_SHORT).show();
        });
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}