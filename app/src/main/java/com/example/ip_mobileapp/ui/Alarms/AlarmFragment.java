package com.example.ip_mobileapp.ui.Alarms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ip_mobileapp.Model.Alarm;
import com.example.ip_mobileapp.Model.Allergy;
import com.example.ip_mobileapp.Model.Examination;
import com.example.ip_mobileapp.Model.MedicalRecord;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentAlarmBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UserSession userSession = UserSession.getInstance(requireActivity());
        User user = userSession.getUser();

        if (user != null) {
            Integer userId = user.getId();
            if(userId!=null) {
                Log.d("MyTag","user id found");
                try {
                    new Thread(() -> {
                        try{
                        RestTemplate restTemplate = new RestTemplate();
                        Map<String, Integer> requestParams = new HashMap<>();
                        requestParams.put("id", userId);
                        Log.d("MyTag","userId: "+String.valueOf(userId));
                        String url = getString(R.string.CLOUD_SERVER) + getString(R.string.GET_ALARMS);
                        Log.d("MyTag","url: "+url);

                        ResponseEntity<List<Alarm>> responseEntity = restTemplate.exchange(
                                url,
                                HttpMethod.POST,
                                new HttpEntity<>(requestParams),

                                new ParameterizedTypeReference<List<Alarm>>() {});

                        if (responseEntity.getStatusCode().is2xxSuccessful()) {
                            Log.d("MyTag","communication succesffull");
                            //MedicalRecord medicalRecord = createMedicalRecordFromServerResponse(responseEntity.getBody());
                            getActivity().runOnUiThread(() -> {
                                ObjectMapper mapper = new ObjectMapper();
                                List<Alarm> alarmList = mapper.convertValue(
                                        responseEntity.getBody(),
                                        new TypeReference<List<Alarm>>(){}
                                );
                                for (Alarm alarm : alarmList) {
                                    Log.d("MyTag", alarm.toString());
                                }
                            });
                        }else{
                            Log.d("MyTag","eroare de la server");
                        }
                        } catch (Exception e) {
                            Log.e("MyTag", "Error fetching alarms", e);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                }catch (Exception e) {
                    // Handle exceptions
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        Log.d("MyTag",e.getMessage());
                        Toast.makeText(getActivity(), getString(R.string.ERROR),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}