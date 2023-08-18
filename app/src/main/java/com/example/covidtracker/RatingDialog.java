package com.example.covidtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.covidtracker.util.LoadLocale;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hsalf.smileyrating.SmileyRating;

public class RatingDialog {
    private Activity activity;
    private AlertDialog dialog;
    LinearLayout feedBox, main;
    RelativeLayout sent;
    Button send,close;
    private LoadLocale loadLocale;
    int rating;
    EditText username, feedback;

    public RatingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void rateThis() {


        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("Please wait...");

        String databaseUrl = "https://covid-tracker-9abd7-default-rtdb.firebaseio.com/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference db = database.getReference("feedbacks");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.ratingdialog, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

        main = dialog.findViewById(R.id.mainlay);
        sent = dialog.findViewById(R.id.feedSent);
        SmileyRating smileRating = (SmileyRating) dialog.findViewById(R.id.smile_rating);
        feedBox = dialog.findViewById(R.id.feedbackBox);
        send = dialog.findViewById(R.id.sendfeedback);
        username = dialog.findViewById(R.id.UserName);
        feedback = dialog.findViewById(R.id.UserFeedBack);
        close = dialog.findViewById(R.id.closefeedback);

        main.setVisibility(View.VISIBLE);
        loadLocale = new LoadLocale(activity);
        if (loadLocale.getLocale().equals("hi")) {

            smileRating.setTitle(SmileyRating.Type.TERRIBLE, "सबसे खराब!");
            smileRating.setTitle(SmileyRating.Type.BAD, "ठीक");
            smileRating.setTitle(SmileyRating.Type.OKAY, " बहुत अच्छा!");
            smileRating.setTitle(SmileyRating.Type.GOOD, "ग़जब का ऐप!");
            smileRating.setTitle(SmileyRating.Type.GREAT, "सबसे अच्छा ऐप !");

        } else {
            smileRating.setTitle(SmileyRating.Type.TERRIBLE, "Worst!");
            smileRating.setTitle(SmileyRating.Type.BAD, "Okayish");
            smileRating.setTitle(SmileyRating.Type.OKAY, "very Good!");
            smileRating.setTitle(SmileyRating.Type.GOOD, "Awesome!");
            smileRating.setTitle(SmileyRating.Type.GREAT, "Loved It!");

        }




        smileRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                rating = type.getRating();
                feedBox.setVisibility(View.VISIBLE);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(feedback.getWindowToken(), 0);
                pd.show();
                String name = username.getText().toString().trim();
                if (name.isEmpty()) {
                    pd.dismiss();
                    Toast.makeText(activity, "How Can I recognise you? \nPlease write your name ", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference myRef = db.child(name);
                    myRef.setValue(name);
                    DatabaseReference myfeed = myRef.child("feedback");
                    String feed = feedback.getText().toString().trim();
                    if (feed.isEmpty()) {
                        feed = "nothing";
                    }
                    myfeed.setValue(feed);
                    DatabaseReference myrate = database.getReference("feedbacks").child(name).child("rating");
                    myrate.setValue(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            main.setVisibility(View.GONE);
                            sent.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


}
