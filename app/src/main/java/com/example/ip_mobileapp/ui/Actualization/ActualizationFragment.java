package com.example.ip_mobileapp.ui.Actualization;

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

import com.example.ip_mobileapp.Model.Alarm;
import com.example.ip_mobileapp.Model.AlarmType;
import com.example.ip_mobileapp.Model.SensorSettings;
import com.example.ip_mobileapp.Model.SensorsData;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentActualizationBinding;
import com.example.ip_mobileapp.ui.Settings.SettingsFragment;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class ActualizationFragment extends Fragment {

    private FragmentActualizationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentActualizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btn = binding.ACTbcvSaveButton;


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession userSession = UserSession.getInstance(requireActivity());
                User user = userSession.getUser();

                RestTemplate restTemplate = new RestTemplate();
                EditText tensArtText = binding.trTensiuneArterialaEdit;
                EditText tempCorpText = binding.trTemperaturaCorporalaEdit;
                EditText greuText = binding.trGreutateEdit;
                EditText glicText = binding.trGlicernieEdit;

                if (user != null) {
                    String cnpString = user.getCnp();
                    if (cnpString != null) {
                        try {
                            new Thread(() -> {
                                try {
                                    Integer tensArt = Integer.parseInt(tensArtText.getText().toString());
                                    Integer tempCorp = Integer.parseInt(tempCorpText.getText().toString());
                                    Integer greu = Integer.parseInt(greuText.getText().toString());
                                    Integer glic = Integer.parseInt(glicText.getText().toString());

                                    SensorSettings sensorSettings = getCurrentSensorSettingsFromRemoteServer(user.getId());
                                    boolean alarmExists=false;
                                    if(tensArt > sensorSettings.getSensorsReferences().getMaximumBloodPressure() || tensArt <sensorSettings.getSensorsReferences().getMinimumBloodPressure()){
                                        Alarm alarm = new Alarm("abnormal blood pressure", AlarmType.UNUSUAL_BODY_PARAMETERS.ordinal());
                                        Map<String, Object> requestParams = new HashMap<>();
                                        requestParams.put("alarm", alarm);
                                        requestParams.put("cnp",cnpString);
                                        String url = getString(R.string.CLOUD_SERVER)+getString(R.string.REPORT_ALARM);
                                        ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                                requestParams, Void.class);
                                        displayAlarmToast(getString(R.string.ALARM_TOAST));

                                    }
                                    if (tempCorp> sensorSettings.getSensorsReferences().getMaximumBodyTemperature() || tempCorp<sensorSettings.getSensorsReferences().getMinimumBodyTemperature()){
                                        Alarm alarm = new Alarm("abnormal body temperature", AlarmType.UNUSUAL_BODY_PARAMETERS.ordinal());
                                        Map<String, Object> requestParams = new HashMap<>();
                                        requestParams.put("alarm", alarm);
                                        requestParams.put("cnp",cnpString);
                                        String url = getString(R.string.CLOUD_SERVER)+getString(R.string.REPORT_ALARM);
                                        ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                                requestParams, Void.class);
                                        displayAlarmToast(getString(R.string.ALARM_TOAST));
                                    }
                                    if(greu > sensorSettings.getSensorsReferences().getMaximumWeight() || greu < sensorSettings.getSensorsReferences().getMinimumWeight()){
                                        Alarm alarm = new Alarm("abnormal weight", AlarmType.UNUSUAL_BODY_PARAMETERS.ordinal());
                                        Map<String, Object> requestParams = new HashMap<>();
                                        requestParams.put("alarm", alarm);
                                        requestParams.put("cnp",cnpString);
                                        String url = getString(R.string.CLOUD_SERVER)+getString(R.string.REPORT_ALARM);
                                        ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                                requestParams, Void.class);
                                        displayAlarmToast(getString(R.string.ALARM_TOAST));
                                    }
                                    if(glic > sensorSettings.getSensorsReferences().getMaximumGlucose() || glic<sensorSettings.getSensorsReferences().getMinimumGlucose()){
                                        Alarm alarm = new Alarm("abnormal glucose level", AlarmType.UNUSUAL_BODY_PARAMETERS.ordinal());
                                        Map<String, Object> requestParams = new HashMap<>();
                                        requestParams.put("alarm", alarm);
                                        requestParams.put("cnp",cnpString);
                                        String url = getString(R.string.CLOUD_SERVER)+getString(R.string.REPORT_ALARM);
                                        ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                                requestParams, Void.class);
                                        displayAlarmToast(getString(R.string.ALARM_TOAST));
                                    }



                                    Map<String, Object> requestParams = new HashMap<>();
                                    SensorsData sensorsData = new SensorsData(greu,glic,tensArt,tempCorp);
                                    requestParams.put("sensorsData", sensorsData);
                                    requestParams.put("cnp",cnpString);

                                    String url = getString(R.string.CLOUD_SERVER) + getString(R.string.SAVE_SENSORS_DATA);
                                    Log.d("MyTag","url: "+url);

                                    ResponseEntity<Void> response = restTemplate.postForEntity(url,
                                            requestParams, Void.class);

                                    getActivity().runOnUiThread(() -> {
                                        if (response.getStatusCode().is2xxSuccessful()) {
                                            Log.d("MyTag", "data sent to server");
                                            NavHostFragment.findNavController(ActualizationFragment.this)
                                                    .navigate(R.id.to_nav_sensors);
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.e("MyTag", "Eroare: "+ e.getMessage());
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(() -> Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show());
                                }
                            }).start();
                        } catch (Exception e) {
                            Log.e("MyTag", "Eroare: "+ e.getMessage());
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show());
                        }
                    }
                }
            }
        });

        return root;
    }

    private void displayAlarmToast(String Situație_de_alarmă_) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getActivity(), Situație_de_alarmă_, Toast.LENGTH_LONG).show());
    }

    private SensorSettings getCurrentSensorSettingsFromRemoteServer(final Integer userId)
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            String url = getString(R.string.CLOUD_SERVER)+getString(R.string.GET_SENSORS_SETTINGS);

            Map<String, Integer> requestParams = new HashMap<>();
            requestParams.put("id", userId);

            return restTemplate.postForObject(
                    url,
                    requestParams,
                    SensorSettings.class);
        } catch (HttpClientErrorException.NotFound exception)
        {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}