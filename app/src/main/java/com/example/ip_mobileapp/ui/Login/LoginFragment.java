package com.example.ip_mobileapp.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;


import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.MainActivity;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentLoginBinding;
import com.example.ip_mobileapp.ui.Registration.RegistrationFragment;
import com.example.ip_mobileapp.ui.ResetPassword.ResetPassword1Fragment;
import com.example.ip_mobileapp.ui.Sensors.SensorFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class LoginFragment extends Fragment{

    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button toRegistration = binding.LOGifcvRegisterButton;
        Button loginButton = binding.LOGifcvLoginButton;
        TextView toResetPassword1 = binding.LOGifcvForgotPasswordText;

        TextInputEditText cnpText = binding.tilCNP;
        TextInputEditText passwordText = binding.tilPassword;



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnpString = cnpText.getText().toString();
                String passwordString = passwordText.getText().toString();

                Thread thread = new Thread(() -> {
                    try {
                        if (cnpString != null && passwordString != null) {
                            User user = new User(cnpString, passwordString);
                            RestTemplate restTemplate = new RestTemplate();
                            user = restTemplate.postForObject(getString(R.string.CLOUD_SERVER)+getString(R.string.LOGIN),
                                    user, User.class);

                            if (Objects.equals(user.getId(), -1)) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> {
                                    Toast.makeText(getActivity(), getString(R.string.INCORRECT_DATA),
                                            Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                UserSession.getInstance(getActivity()).setUser(user);

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> {
                                    Toast.makeText(getActivity(), getString(R.string.WELCOME),
                                            Toast.LENGTH_SHORT).show();
                                });

                                Log.d("MyTag", user.toString());

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            Toast.makeText(getActivity(), "An error occurred: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
                thread.start();
            }
        });

        toRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).switchFragment(new RegistrationFragment());
            }
        });

        toResetPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).switchFragment(new ResetPassword1Fragment());
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
