package com.example.ip_mobileapp.ui.Chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ip_mobileapp.LoginActivity;
import com.example.ip_mobileapp.Model.Chat;
import com.example.ip_mobileapp.Model.Examination;
import com.example.ip_mobileapp.Model.MedicalRecord;
import com.example.ip_mobileapp.Model.Message;
import com.example.ip_mobileapp.Model.User;
import com.example.ip_mobileapp.Model.UserSession;
import com.example.ip_mobileapp.R;
import com.example.ip_mobileapp.databinding.FragmentChatBinding;
import com.example.ip_mobileapp.databinding.FragmentLoginBinding;
import com.example.ip_mobileapp.ui.Login.LoginFragment;
import com.example.ip_mobileapp.ui.home.HomeFragment;
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


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout parentLayout = binding.cltrLinearLayout;

        UserSession userSession = UserSession.getInstance(requireActivity());
        User user = userSession.getUser();

        if (user != null) {
            Integer userId = user.getId();
            if(userId!=null){
                try{
                    new Thread(() -> {
                        try {
                            RestTemplate restTemplate = new RestTemplate();
                            Map<String, Integer> requestParams = new HashMap<>();
                            requestParams.put("id", userId);
                            Log.d("MyTag","userId: "+String.valueOf(user.getId()));
                            String url = getString(R.string.CLOUD_SERVER) + getString(R.string.GET_CHAT_HISTORY);
                            Log.d("MyTag","url: "+url);

                            ResponseEntity<List<Chat>> response = restTemplate.exchange(url,
                                    HttpMethod.POST,
                                    new HttpEntity<>(requestParams),
                                    new ParameterizedTypeReference<List<Chat>>() {});

                            if (response.getStatusCode().is2xxSuccessful()) {

                                //MedicalRecord medicalRecord = createMedicalRecordFromServerResponse(responseEntity.getBody());
                                getActivity().runOnUiThread(() -> {
                                    List<Chat> chatList = response.getBody();
                                    for (Chat chat : chatList) {
                                        Log.d("MyTag", chat.toString());
                                        Message firstMessage = getFirstMessage(chat);

                                        Log.d("MyTag", "PREPARING TO CREATE CARDVIEW");
                                        //Log.d(TAG,firstMessage.getSendingDate().toString());
                                        CardView cardView = createCardViewExisting(chat.getOtherUser().getFirstName(), String.valueOf(firstMessage.getSendingDate()), firstMessage.getContent(), String.valueOf(chat.getId()));
                                        parentLayout.addView(cardView);
                                    }
                                });



                            } else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> Toast.makeText(getActivity(), "Error fetching medical record", Toast.LENGTH_LONG).show());
                            }
                        } catch (Exception e) {
                            Log.e("MyTag", "Error fetching medical record", e);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> Toast.makeText(getActivity(), "Eroare: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                }catch (Exception e) {
                    // Handle exceptions
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        Toast.makeText(getActivity(), "Eroare: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        }else{
            redirectToLogin();
        }

        EditText searchText = binding.strEditText;
        ImageView searchBtn = binding.strImageView;

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchText.getText().toString();
                if(searchString!=null){
                    try{
                        new Thread(() -> {
                            try {
                                RestTemplate restTemplate = new RestTemplate();
                                Map<String, String> requestParams = new HashMap<>();
                                requestParams.put("id", searchString);
                                Log.d("MyTag","userId: "+String.valueOf(user.getId()));
                                String url = getString(R.string.CLOUD_SERVER) + getString(R.string.SEARCH_USER);
                                Log.d("MyTag","url: "+url);

                                ResponseEntity<List<User>> response = restTemplate.exchange(
                                        url,
                                        HttpMethod.POST,
                                        new HttpEntity<>(requestParams),
                                        new ParameterizedTypeReference<List<User>>() {});

                                if (response.getStatusCode().is2xxSuccessful()) {

                                    //MedicalRecord medicalRecord = createMedicalRecordFromServerResponse(responseEntity.getBody());
                                    getActivity().runOnUiThread(() -> {
                                        List<User> userList = response.getBody();
                                        for (User user : userList) {
                                            Log.d("MyTag", user.toString());
                                        }
                                    });



                                } else {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(() -> Toast.makeText(getActivity(), "Nu a fost gasit niciun chat!", Toast.LENGTH_LONG).show());
                                }
                            } catch (Exception e) {
                                Log.e("MyTag", "Eroare", e);
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
            }
        });



        return root;
    }

    private Map<String, Integer> generateRequestBodyForChatHistory(final Integer userId)
    {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("id", userId);
        return requestBody;
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

    @NonNull
    private static Message getFirstMessage(Chat chat) {
        Message firstMessage = null;
        List<Message> messages = chat.getMessages();

        // Check if the messages list is not empty and contains at least one message
        if (messages != null && !messages.isEmpty()) {
            // Get the first message from the list
            firstMessage = messages.get(0);
        }

        // If firstMessage is still null, set a default message
        if (firstMessage == null) {
            // Create a default message with empty content and sendingDate
            firstMessage = new Message();
            firstMessage.setContent("");
        }
        return firstMessage;
    }



    private CardView createCardViewExisting(String prenume, String date, String lastMsg, String id_conv) {

        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                950, // Width
                300  // Height
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
                Bundle bundle = new Bundle();
                bundle.putString("prenume", prenume);


                // Navigate to the target fragment with the data
                NavHostFragment.findNavController(ChatFragment.this)
                        .navigate(R.id.to_selected_chat, bundle);

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

        // Create and add TextView for prenume to the ConstraintLayout
        TextView textPrenume = new TextView(requireContext());
        textPrenume.setId(View.generateViewId());
        textPrenume.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        textPrenume.setText(prenume);
        textPrenume.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        textPrenume.setTextColor(Color.BLACK);
        constraintLayout.addView(textPrenume);

        // Set constraints for prenume TextView
        ConstraintSet constraintSetPrenume = new ConstraintSet();
        constraintSetPrenume.clone(constraintLayout);
        constraintSetPrenume.connect(textPrenume.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSetPrenume.connect(textPrenume.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSetPrenume.applyTo(constraintLayout);


        // Create and add TextView for last msg to the ConstraintLayout
        TextView textLastMsg = new TextView(requireContext());
        textLastMsg.setId(View.generateViewId());
        textLastMsg.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        textLastMsg.setText(lastMsg);
        textLastMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textLastMsg.setTextColor(Color.BLACK);
        constraintLayout.addView(textLastMsg);

        // Set constraints for last msg TextView
        ConstraintSet constraintSetLastmsg = new ConstraintSet();
        constraintSetLastmsg.clone(constraintLayout);
        constraintSetLastmsg.connect(textLastMsg.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSetLastmsg.connect(textLastMsg.getId(), ConstraintSet.TOP, textPrenume.getId(), ConstraintSet.BOTTOM);
        constraintSetLastmsg.applyTo(constraintLayout);


        // Create and add TextView for date to the ConstraintLayout
        TextView textDate = new TextView(requireContext());
        textDate.setId(View.generateViewId());
        textDate.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        if(!date.equals("null"))
            textDate.setText(date);
        else
            textDate.setText(" ");
        textDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textDate.setTextColor(Color.GRAY);
        constraintLayout.addView(textDate);

        // Set constraints for date TextView
        ConstraintSet constraintSetDate = new ConstraintSet();
        constraintSetDate.clone(constraintLayout);
        constraintSetDate.connect(textDate.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END); // Align to the right side of the ConstraintLayout
        constraintSetDate.connect(textDate.getId(), ConstraintSet.TOP, textLastMsg.getId(), ConstraintSet.BOTTOM); // Position below textEmail
        constraintSetDate.applyTo(constraintLayout);



        return cardView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}