package com.example.ip_mobileapp.ui.ResetPassword;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.MainActivity;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentRegistrationBinding;
import com.example.ip_mobileapp.databinding.FragmentResetPassword1Binding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ResetPassword1Fragment extends Fragment {

    private FragmentResetPassword1Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentResetPassword1Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button toLogin = binding.RPAifcvBackButton;
        Button toResetPassword2 = binding.RPAifcvConfirmButton;

        EditText emailText = binding.ifcvEmailEdit;


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).switchFragment(new LoginFragment());
            }
        });

        toResetPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = emailText.getText().toString();
                Thread thread = new Thread(() -> {
                    try {
                        if(emailString!=null){
                            RestTemplate restTemplate = new RestTemplate();
                            Map<String, String> requestParams = new HashMap<>();
                            requestParams.put("emailAddress", emailString);

                            String url = getString(R.string.CLOUD_SERVER) + getString(R.string.RESET_PASSWORD_EMAIL);
                            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                                    url,
                                    HttpMethod.POST,
                                    new HttpEntity<>(requestParams),
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
                            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                Log.d("MyTag","email sent ");
                                Bundle bundle = new Bundle();
                                bundle.putString("email", emailString);
                                ResetPassword2Fragment fragment = new ResetPassword2Fragment();
                                fragment.setArguments(bundle);
                                ((LoginActivity) getActivity()).switchFragment(fragment);
                            }else{
                                Log.d("MyTag","error ");
                                Toast.makeText(getActivity(), getString(R.string.ERROR) , Toast.LENGTH_LONG).show();
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