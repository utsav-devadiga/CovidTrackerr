package com.example.covidtracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.covidtracker.R.layout;
import com.example.covidtracker.adapter.NewsAdapter;
import com.example.covidtracker.api.globalData.GlobalData;
import com.example.covidtracker.api.newsData.NewsData;
import com.example.covidtracker.api.stateWiseData.Cordinates;
import com.example.covidtracker.ui.BottomSheetDonateDialog;
import com.example.covidtracker.ui.BottomSheetPreventionDialog;
import com.example.covidtracker.ui.BottomSheetPrixaDialog;
import com.example.covidtracker.util.LoadLocale;
import com.example.covidtracker.util.SetLanguage;
import com.example.covidtracker.util.SpacesItemDecoration;
import com.example.covidtracker.vm.GlobalDataViewModel;
import com.example.covidtracker.vm.NewsDataViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.example.covidtracker.R.id;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.MPPointF;
import com.example.covidtracker.R.string;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.covidtracker.R.drawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.content.ContentValues.TAG;

public class HomeFragment  extends Fragment  {
    private ArrayList<String> mNewsImage = new ArrayList<>();
    private ArrayList<String> mNewsTitle = new ArrayList<>();
    private ArrayList<String> mNewsURL = new ArrayList<>();

    private BottomSheetPreventionDialog bottomSheetPreventionDialog = new BottomSheetPreventionDialog();

    @BindView(id.shimmer_layout)
    ShimmerFrameLayout mNewsShimmer;
    @BindView(id.news_recycler_view)
    RecyclerView mNewsRecyclerView;
    @BindView(id.home_call_button)
    LinearLayout mCallButton;
    @BindView(id.home_website_button)
    LinearLayout mWebsiteButton;
    @BindView(id.home_do_test_button)
    RelativeLayout mDoTest;
    @BindView(id.home_country_button)
    LinearLayout mCountry;
    @BindView(id.home_preventive_1)
    LinearLayout mPreventive1;
    @BindView(id.home_preventive_2)
    LinearLayout mPreventive2;
    @BindView(id.home_preventive_3)
    LinearLayout mPreventive3;
    @BindView(id.home_global_data_pie_chart)
    PieChart mGlobalDataPieChart;
    @BindView(id.home_confirmed)
    TextView mConfirmed;
    @BindView(id.home_death)
    TextView mDeath;
    @BindView(id.home_recovered)
    TextView mRecovered;
    @BindView(id.home_global_data_layout)
    LinearLayout mGlobalDataLayout;
    @BindView(id.home_global_data_shimmer)
    ShimmerFrameLayout mGlobalDataShimmer;
    @BindView(id.home_donate_button)
    RelativeLayout mDonate;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_home, container, false);
        Toast mToast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
        ButterKnife.bind(this, view);

        LoadLocale loadLocale = new LoadLocale(getActivity());
        Timber.d("LoadLocale%s", loadLocale.getLocale());

        // Give the language option on the fresh install app
        if (loadLocale.getLocale().equals("-1")) {
            new SetLanguage(getContext(), getActivity());
        }


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
               System.out.println(token);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1075"));
                startActivity(intent);
            }
        });

        mWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("passingURL", "https://data.gov.in/major-indicator/covid-19-india-data-source-mohfw");
                intent.putExtra("passingTitle", "The Official Portal of the Covid-19 Task Force");
                getContext().startActivity(intent);
            }
        });

        mDoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetPrixaDialog bottomSheetPrixaDialog = new BottomSheetPrixaDialog();
                bottomSheetPrixaDialog.show(getFragmentManager(), "PrixaBottomSheet");
            }
        });

        mDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDonateDialog bottomSheetDonateDialog = new BottomSheetDonateDialog();
                bottomSheetDonateDialog.show(getFragmentManager(), "DonateBottomSheet");
            }
        });

        mCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToast.setText("Other countries' data coming soon!");
                mToast.show();
            }
        });

        mPreventive1.setOnClickListener(new View.OnClickListener() {
            Bundle bundle = new Bundle();
            Integer passingTitle = string.home_preventive1;
            Integer passingHeader = drawable.social_distancing_header;
            Integer passingContent = string.social_distancing;
            Integer passingCitation = string.social_distancing_citation;
            @Override
            public void onClick(View view) {
                bundle.putInt("passingTitle", passingTitle);
                bundle.putInt("passingHeader", passingHeader);
                bundle.putInt("passingContent", passingContent);
                bundle.putInt("passingCitation", passingCitation);
                bottomSheetPreventionDialog.setArguments(bundle);
                bottomSheetPreventionDialog.show(getFragmentManager(), "BottomSheet");
            }
        });

        mPreventive2.setOnClickListener(new View.OnClickListener() {
            Bundle bundle = new Bundle();
            Integer passingTitle = string.home_preventive2;
            Integer passingHeader = drawable.wash_hand_header;
            Integer passingContent = string.wash_hand;
            Integer passingCitation = string.wash_hand_citation;
            @Override
            public void onClick(View view) {
                bundle.putInt("passingTitle", passingTitle);
                bundle.putInt("passingHeader", passingHeader);
                bundle.putInt("passingContent", passingContent);
                bundle.putInt("passingCitation", passingCitation);
                bottomSheetPreventionDialog.setArguments(bundle);
                bottomSheetPreventionDialog.show(getFragmentManager(), "BottomSheet");
            }
        });

        mPreventive3.setOnClickListener(new View.OnClickListener() {
            Bundle bundle = new Bundle();
            Integer passingTitle = string.home_preventive3;
            Integer passingHeader = drawable.wear_mask_header;
            Integer passingContent = string.wear_mask;
            Integer passingCitation = string.wear_mask_citation;
            @Override
            public void onClick(View view) {
                bundle.putInt("passingTitle", passingTitle);
                bundle.putInt("passingHeader", passingHeader);
                bundle.putInt("passingContent", passingContent);
                bundle.putInt("passingCitation", passingCitation);
                bottomSheetPreventionDialog.setArguments(bundle);
                bottomSheetPreventionDialog.show(getFragmentManager(), "BottomSheet");
            }
        });

        setPieChartGlobalData();

        // GLOBAL DATA FETCHING

        GlobalDataViewModel globalDataViewModel;
        globalDataViewModel = ViewModelProviders.of(this).get(GlobalDataViewModel.class);
        globalDataViewModel.init();

        globalDataViewModel.getLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showLoadingGlobalData();
            } else {
                hideLoadingGlobalData();
            }
        });

        globalDataViewModel.getGlobalData().observe(this, globalData -> {
            try{
                Timber.d(String.valueOf(globalData.getConfirmed().getTheValue()));
                Timber.d(String.valueOf(globalData.getDeath().getTheValue()));
                Timber.d(String.valueOf(globalData.getRecovered().getTheValue()));
                Timber.d(globalData.getLastUpdate());
                setDataGlobalDataPieChart(globalData);
            }catch (Exception e){
                e.printStackTrace();
            }

        });

        // THE DATA FETCHING PROCESS
        NewsDataViewModel newsDataViewModel;

        newsDataViewModel = ViewModelProviders.of(this).get(NewsDataViewModel.class);
        newsDataViewModel.init();

        newsDataViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    showLoading();
                } else {
                    hideLoading();
                }
            }
        });

        LiveData<NewsData> newsDataLiveData;

        newsDataLiveData = newsDataViewModel.getNewsDataEn();

        newsDataLiveData.observe(this, new Observer<NewsData>() {
            @Override
            public void onChanged(NewsData newsData) {
                List<NewsData.Articles> articles = newsData.getArticles();
                for (NewsData.Articles theArticle : articles) {
                    mNewsTitle.add(theArticle.getTitle());
                    mNewsImage.add(theArticle.getUrltoimage());
                    mNewsURL.add(theArticle.getUrl());
                }

                NewsAdapter newsAdapter = new NewsAdapter(mNewsImage, mNewsTitle, mNewsURL, getContext());
                mNewsRecyclerView.setAdapter(newsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                mNewsRecyclerView.setLayoutManager(linearLayoutManager);
                mNewsRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
                SnapHelper snapHelper = new PagerSnapHelper();
                if (mNewsRecyclerView.getOnFlingListener() == null)
                    snapHelper.attachToRecyclerView(mNewsRecyclerView);
            }
        });

        return view;

    }

    private void showLoadingGlobalData() {

        mGlobalDataLayout.setVisibility(View.GONE);
        mGlobalDataShimmer.setVisibility(View.VISIBLE);
        mGlobalDataShimmer.startShimmer();

    }

    private void hideLoadingGlobalData() {

        mGlobalDataLayout.setVisibility(View.VISIBLE);
        mGlobalDataShimmer.setVisibility(View.GONE);
        mGlobalDataShimmer.stopShimmer();

    }

    private void setPieChartGlobalData() {

        mGlobalDataPieChart.getDescription().setEnabled(false);
        mGlobalDataPieChart.setDragDecelerationFrictionCoef(0.95f);
//        mGlobalDataPieChart.setDrawHoleEnabled(true);

        mGlobalDataPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mGlobalDataPieChart.setRotationEnabled(true);
        mGlobalDataPieChart.setHighlightPerTapEnabled(true);

        mGlobalDataPieChart.animateY(1500, Easing.EaseInOutQuad);
        mGlobalDataPieChart.getLegend().setEnabled(false);
        // entry label styling
    }

    @SuppressLint("ResourceType")
    private void setDataGlobalDataPieChart(GlobalData globalData) {

        int confirmed = globalData.getConfirmed().getTheValue();
        int death = globalData.getDeath().getTheValue();
        int recovered = globalData.getRecovered().getTheValue();

        mConfirmed.setText(numberSeparator(confirmed));
        mDeath.setText(numberSeparator(death));
        mRecovered.setText(numberSeparator(recovered));

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(confirmed, 1));
        entries.add(new PieEntry(death, 2));
        entries.add(new PieEntry(recovered, 3));

        PieDataSet dataSet = new PieDataSet(entries, "Global Data");

        dataSet.setDrawIcons(false);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor(getResources().getString(R.color.colorAccent)));
        colors.add(Color.parseColor(getResources().getString(R.color.themeRed)));
        colors.add(Color.parseColor(getResources().getString(R.color.themeOrange)));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        mGlobalDataPieChart.setData(data);

        // undo all highlights
        mGlobalDataPieChart.highlightValues(null);
        mGlobalDataPieChart.setCenterText("Update:\n" + globalData.getLastUpdate()
                .replace("T","\n")
                .replace(".000Z",""));

        mGlobalDataPieChart.invalidate();



    }

    private void hideLoading() {
        mNewsShimmer.stopShimmer();
        mNewsShimmer.setVisibility(View.GONE);
        mNewsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mNewsShimmer.setVisibility(View.VISIBLE);
        mNewsRecyclerView.setVisibility(View.GONE);
    }

    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(value));
    }

}
