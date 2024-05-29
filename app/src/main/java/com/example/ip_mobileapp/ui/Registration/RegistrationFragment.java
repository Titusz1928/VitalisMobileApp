package com.example.ip_mobileapp.ui.Registration;

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
import com.example.ip_mobileapp.Model.AccessType;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentLoginBinding;
import com.example.ip_mobileapp.databinding.FragmentRegistrationBinding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;
import com.example.ip_mobileapp.ui.ResetPassword.ResetPassword2Fragment;
import com.google.android.material.textfield.TextInputEditText;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button registrationBtn = binding.REGifcvRegistrationButton;
        Button backBtn = binding.REGifcvBackButton;

        TextInputEditText emailText = binding.tilEmail;
        EditText prenumeText =binding.tlPrenumeEditText;
        EditText numeText = binding.tlNumeEditText;
        EditText cnpText = binding.tlCNPEditText;
        EditText varstaText = binding.tlAgeEditText;
        EditText stradaText = binding.tlStreeetEditText;
        EditText orasText = binding.tlCityEditText;
        EditText judetText = binding.tlJudetEditText;
        EditText taraText = binding.tlCountryEditText;
        EditText telefonText = binding.tlPhoneEditText;
        EditText profesieText = binding.tlProfesieEditText;
        EditText locdemunText = binding.tlLocMunEditText;

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = emailText.getText().toString();
                String prenumeString = prenumeText.getText().toString();
                String numeString = numeText.getText().toString();
                String cnpString = cnpText.getText().toString();
                Integer varsta = Integer.parseInt(varstaText.getText().toString());
                String stradaString = stradaText.getText().toString();
                String orasString = orasText.getText().toString();
                String judetString = judetText.getText().toString();
                String taraString = taraText.getText().toString();
                String telefonString = telefonText.getText().toString();
                String profesieString = profesieText.getText().toString();
                String locdemunString = locdemunText.getText().toString();

                Thread thread = new Thread(() -> {
                    try {
                        if (cnpString != null && emailString != null && prenumeString!=null && numeString!=null&& varsta!=null&& stradaString!=null&& orasString!=null&& judetString!=null&& taraString!=null&& telefonString!=null&& profesieString!=null&& locdemunString!=null ) {
                            User user = new User(prenumeString,numeString,cnpString,varsta,stradaString,orasString,judetString,taraString,telefonString,emailString,profesieString,locdemunString);
                            RestTemplate restTemplate = new RestTemplate();
                            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(getString(R.string.CLOUD_SERVER)+getString(R.string.ADD_USER),
                                    user,  Void.class);

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                Toast.makeText(getActivity(), getString(R.string.AFTER_REGISTRATION),
                                        Toast.LENGTH_SHORT).show();
                            });
                            ((LoginActivity) getActivity()).switchFragment(new LoginFragment());
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            Log.d("MyTag",e.getMessage());
                            Toast.makeText(getActivity(),getString(R.string.REGISTRATION_FAIL),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
                thread.start();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).switchFragment(new LoginFragment());
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