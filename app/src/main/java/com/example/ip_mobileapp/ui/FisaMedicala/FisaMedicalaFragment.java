package com.example.ip_mobileapp.ui.FisaMedicala;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.Model.Allergy;
import com.example.ip_mobileapp.Model.Examination;
import com.example.ip_mobileapp.Model.MedicalRecord;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentFisaMedicalaBinding;
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

public class FisaMedicalaFragment extends Fragment{

    private FragmentFisaMedicalaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFisaMedicalaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView numeText=binding.tlNumeContentText;
        TextView prenumeText=binding.tlPrenumeContentText;
        TextView cnpText=binding.tlCNPContentText;
        TextView varstaText=binding.tlVarstaContentText;
        TextView emailText=binding.tlEmailContentText;
        TextView telefonText=binding.tlTelefonContentText;
        TextView adresaText=binding.tlAdresaContentText;
        TextView profesieText=binding.tlProfesieContentText;
        TextView locDeMunText=binding.tlLocDeMuneContentText;


        UserSession userSession = UserSession.getInstance(requireActivity());
        User user = userSession.getUser();

        if (user != null) {
            Integer userId = user.getId();
            if(userId!=null){
                numeText.setText(user.getLastName());
                prenumeText.setText(user.getFirstName());
                cnpText.setText(user.getCnp());
                varstaText.setText(String.valueOf(user.getAge()));
                emailText.setText(user.getEmailAddress());
                telefonText.setText(user.getPhoneNumber());
                adresaText.setText(user.getCountry()+", "+user.getCounty()+", "+user.getCity()+", "+user.getStreet());
                profesieText.setText(user.getProfession());
                locDeMunText.setText(user.getWorkplace());
                try{
                    new Thread(() -> {
                        try {
                            RestTemplate restTemplate = new RestTemplate();
                            Map<String, Integer> requestParams = new HashMap<>();
                            requestParams.put("id", userId);
                            Log.d("MyTag","userId: "+String.valueOf(user.getId()));
                            String url = getString(R.string.CLOUD_SERVER) + getString(R.string.GET_MEDICAL_RECORD);
                            Log.d("MyTag","url: "+url);

                            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                                    url,
                                    HttpMethod.POST,
                                    new HttpEntity<>(requestParams),
                                    new ParameterizedTypeReference<Map<String, Object>>() {});

                            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                                MedicalRecord medicalRecord = createMedicalRecordFromServerResponse(responseEntity.getBody());
                                Log.d("MyTag",medicalRecord.toString());

                                getActivity().runOnUiThread(() -> {
                                    ObjectMapper mapper = new ObjectMapper();
                                    List<Examination> examinationList = mapper.convertValue(
                                            medicalRecord.getExaminations(),
                                            new TypeReference<List<Examination>>(){}
                                    );
                                    Log.d("MyTag","examinations: "+examinationList.toString());
                                    if(examinationList!=null) {
                                        Integer i=0;
                                        for (Examination examination : examinationList) {
                                            Log.d("MyTag",String.valueOf(i++));
                                            Log.d("MyTag", "examination: " + examination.toString());

                                            TableLayout tableLayout = binding.llConsultatiiTableLayout;
                                            TableRow tableRow = new TableRow(requireActivity());
                                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                                            tableRow.setLayoutParams(layoutParams);

                                            TextView dataText = new TextView(requireActivity());
                                            dataText.setText(String.valueOf(examination.getExaminationDate()));
                                            tableRow.addView(dataText);
                                            TextView diagnosticText = new TextView(requireActivity());
                                            diagnosticText.setText(examination.getDiagnostic());
                                            tableRow.addView(diagnosticText);
                                            TextView cureText = new TextView(requireActivity());
                                            cureText.setText(examination.getCure());
                                            tableRow.addView(cureText);
                                            TextView recomandareText = new TextView(requireActivity());
                                            recomandareText.setText(examination.getRecomandation());
                                            tableRow.addView(recomandareText);



                                            tableLayout.addView(tableRow);
                                        }

                                    }

                                    List<Allergy> allergyList = mapper.convertValue(
                                            medicalRecord.getAllergies(),
                                            new TypeReference<List<Allergy>>() {

                                            });

                                    if(allergyList!=null){
                                        for (Allergy allergy : allergyList) {
                                            Log.d("MyTag", allergy.getName());
                                            TableLayout tableLayout = binding.llAlergiiTableLayout;
                                            TableRow tableRow = new TableRow(requireActivity());
                                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                                            tableRow.setLayoutParams(layoutParams);

                                            TextView numeAlergieText = new TextView(requireActivity());
                                            numeAlergieText.setText(String.valueOf(allergy.getName()));
                                            tableRow.addView(numeAlergieText);

                                            tableLayout.addView(tableRow);
                                        }
                                    }

                                });


                            } else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show());
                            }
                        } catch (Exception e) {
                            Log.e("MyTag", "Error fetching medical record", e);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> Toast.makeText(getActivity(), getString(R.string.ERROR), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                }catch (Exception e) {
                    // Handle exceptions
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        Toast.makeText(getActivity(), getString(R.string.ERROR),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        }else{
            redirectToLogin();
        }

        return root;
    }

    public MedicalRecord createMedicalRecordFromServerResponse(final Map<String, Object> serverResponse)
    {
        User user = new User((Map<String, Object>)serverResponse.get("user"));
        List<Examination> examinations = (List<Examination>)
                serverResponse.get("examinations");
        List<Allergy> allergies = (List<Allergy>)
                serverResponse.get("allergies");
        return new MedicalRecord(user, allergies, examinations);
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