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
import android.widget.Toast;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentChangePassword2Binding;
import com.example.ip_mobileapp.databinding.FragmentChangePasswordBinding;
import com.example.ip_mobileapp.databinding.FragmentSettingsBinding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;
import com.example.ip_mobileapp.ui.Settings.SettingsFragment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword2Fragment extends Fragment {

    private FragmentChangePassword2Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentChangePassword2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button toResetPassword3 = binding.RPA2ifcvConfirmButton;

        EditText codeText = binding.ifcvCodEdit;

        Bundle args = getArguments();
        String emailString;
        if (args != null) {
            emailString = args.getString("email");
            Log.d("MyTag",emailString);
        } else {
            emailString = null;
            redirectToLogin();
        }

        toResetPassword3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeString = codeText.getText().toString();
                Thread thread = new Thread(() -> {
                    try {
                        if(codeString!=null){
                            RestTemplate restTemplate = new RestTemplate();
                            Map<String, String> requestParams = new HashMap<>();
                            requestParams.put("emailAddress", emailString);
                            requestParams.put("confirmationCode", codeString);

                            String url = getString(R.string.CLOUD_SERVER) + getString(R.string.CHECK_CONFIRMATION_CODE);
                            ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                    requestParams, Void.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                Log.d("MyTag","code checked ");
                                Bundle bundle = new Bundle();
                                bundle.putString("email", emailString);
                                ResetPassword3Fragment fragment = new ResetPassword3Fragment();
                                fragment.setArguments(bundle);
                                ((LoginActivity) getActivity()).switchFragment(fragment);
                            }else{
                                Log.d("MyTag","error ");
                                Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show();
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
//                Bundle bundle = new Bundle();
//                bundle.putString("email", emailString);
//                ResetPassword3Fragment fragment = new ResetPassword3Fragment();
//                fragment.setArguments(bundle);
//                ((LoginActivity) getActivity()).switchFragment(fragment);
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