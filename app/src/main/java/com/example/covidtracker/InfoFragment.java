package com.example.covidtracker;

import androidx.fragment.app.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidtracker.util.SetLanguage;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoFragment extends Fragment {

    @BindView(R.id.info_website_button)
    LinearLayout mWebsite;
    @BindView(R.id.info_mail_button)
    LinearLayout mMail;
    @BindView(R.id.info_language_button)
    LinearLayout mLanguage;
    @BindView(R.id.rate)
    Button rating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("passingURL", "https://utsav-dev.netlify.app/");
                intent.putExtra("passingTitle", "About Me");
                getContext().startActivity(intent);
            }
        });

        mMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"utsavdevadiga10@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CovidTracker App");
                startActivity(Intent.createChooser(intent, "Send mail..."));
            }
        });

        mLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SetLanguage(getContext(), getActivity());
            }
        });

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingDialog ratingDialog = new RatingDialog(getActivity());
                ratingDialog.rateThis();
            }
        });
        return view;
    }

}
