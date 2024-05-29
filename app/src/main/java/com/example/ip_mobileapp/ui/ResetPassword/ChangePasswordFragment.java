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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentChangePasswordBinding;
import com.example.ip_mobileapp.databinding.FragmentChatBinding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;
import com.example.ip_mobileapp.ui.Sensors.SensorFragment;
import com.example.ip_mobileapp.ui.Settings.SettingsFragment;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button sendCode = binding.CPAifcvConfirmButton;

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChangePasswordFragment.this)
                       .navigate(R.id.to_change_password2);
            }
        });


        Button toResetPassword2 = binding.CPAifcvConfirmButton;

        EditText emailText = binding.ifcvEmailEdit;


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