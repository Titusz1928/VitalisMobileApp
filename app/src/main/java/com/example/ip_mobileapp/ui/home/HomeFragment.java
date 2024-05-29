package com.example.ip_mobileapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.Model.Allergy;
import com.example.ip_mobileapp.Model.Examination;
import com.example.ip_mobileapp.Model.MedicalRecord;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentHomeBinding;
import com.example.ip_mobileapp.ui.Recomandare.RecomandareFragment;
import com.example.ip_mobileapp.ui.Sensors.SensorFragment;
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


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UserSession userSession = UserSession.getInstance(requireActivity());
        User user = userSession.getUser();

        if (user != null) {
            String userName = user.getFirstName();

            // Generate the welcome message based on the user's name
            String welcomeMessage = getString(R.string.HOMtcvGreeetingTextView, userName);

            // Set the welcome message to the TextView
            TextView welcomeText = binding.tcvGreetingTextView;
            welcomeText.setText(welcomeMessage);

            Integer userId = user.getId();
            if(userId!=null){
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
                                            LinearLayout parentLayout = binding.nsvLinearLayout;
                                            CardView cardView = createCardView(requireContext(),String.valueOf(examination.getExaminationDate()),examination.getDiagnostic(),examination.getCure(),examination.getRecomandation());
                                            parentLayout.addView(cardView);



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

        ImageButton openWebPage = binding.llBrowserImageButton;

        openWebPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("https://www.google.com");
            }
        });


        ImageView infoButton = binding.HOMInfoImageView;
        TextView infoText = binding.icvDescriptionTextView;

        infoButton.setOnClickListener(v -> {
            if (infoText.getVisibility() == View.GONE) {
                infoText.setVisibility(View.VISIBLE);
                infoButton.setImageResource(R.drawable.ic_minimize);
            } else {
                infoText.setVisibility(View.GONE);
                infoButton.setImageResource(R.drawable.ic_expand);
            }
        });


        return root;
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        Log.d("MYTag","enetered openWebPage function");
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where no browser is available
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Toast.makeText(requireContext(), getString(R.string.BROWSER_ERROR), Toast.LENGTH_LONG).show();
            });
        }
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

    private CardView createCardView(Context context, String date, String diagnostic, String cure, String recomandare) {
        CardView cardView = new CardView(requireContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                950, // Width
                150  // Height
        );
        cardView.setRadius(30);
        layoutParams.setMargins(16, 16, 16, 16); // Left, top, right, bottom margins
        cardView.setLayoutParams(layoutParams);

        //cardView.setCardCornerRadius(30); // Card corner radius
        cardView.setCardBackgroundColor(Color.WHITE); // Card background color
        cardView.setContentPadding(10, 10, 10, 10); // Card content padding

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Bundle to pass data
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("diagnostic", diagnostic);
                bundle.putString("cure", cure);
                bundle.putString("recomandare", recomandare);

                // Navigate to the target fragment with the data
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.to_recomandare, bundle);
            }
        });


        // Create a ConstraintLayout for the card view
        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());

        // Set constraint layout properties
        ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        constraintLayout.setLayoutParams(constraintLayoutParams);

        cardView.addView(constraintLayout);

        // Create and add TextView for diagnostic to the ConstraintLayout
        TextView textDiagnostic = new TextView(requireContext());
        textDiagnostic.setId(View.generateViewId());
        textDiagnostic.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        textDiagnostic.setText(diagnostic);
        textDiagnostic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textDiagnostic.setTextColor(Color.BLACK);
        constraintLayout.addView(textDiagnostic);

        // Set constraints for diagnostic TextView
        ConstraintSet constraintSetDiagnostic = new ConstraintSet();
        constraintSetDiagnostic.clone(constraintLayout);
        constraintSetDiagnostic.connect(textDiagnostic.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSetDiagnostic.connect(textDiagnostic.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSetDiagnostic.applyTo(constraintLayout);

        // Create and add TextView for the date to the ConstraintLayout
        TextView textDate = new TextView(requireContext());
        textDate.setId(View.generateViewId());
        textDate.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        textDate.setText(date);
        textDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textDate.setTextColor(Color.BLACK);
        constraintLayout.addView(textDate);

        // Set constraints for date TextView
        ConstraintSet constraintSetDate = new ConstraintSet();
        constraintSetDate.clone(constraintLayout);
        constraintSetDate.connect(textDate.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSetDate.connect(textDate.getId(), ConstraintSet.TOP, textDiagnostic.getId(), ConstraintSet.BOTTOM); // Set below prenume TextView
        constraintSetDate.applyTo(constraintLayout);



        return cardView;
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