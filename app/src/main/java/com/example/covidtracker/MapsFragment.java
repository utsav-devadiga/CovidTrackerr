package com.example.covidtracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidtracker.adapter.StateAdapter;
import com.example.covidtracker.api.stateWiseData.Cordinates;
import com.example.covidtracker.api.timeseries.CasesTimeSeries;
import com.example.covidtracker.util.LoadLocale;
import com.example.covidtracker.vm.StateData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

public class MapsFragment extends Fragment implements OnMapReadyCallback,StateAdapter.ListClickedListener {
    private ArrayList<Cordinates> CoordinateListData = new ArrayList<>();
    private ArrayList<CasesTimeSeries.Statewise> provListData = new ArrayList<>();

    public ArrayList<CasesTimeSeries.Statewise> filteredList = new ArrayList<>();

    private GoogleMap googleMap;
    double lnglist;
    double latlist;
    final float ZOOM = 7;
    int confirmed,death,recovered,active;
    String Percentage;

    private StateAdapter provAdapter;

    private SlidingUpPanelLayout mSlideUpLayout;

    private SearchView mFilter;

    private TextView mProvCollectedData;

    private ArrayList<Marker> markerArrayList = new ArrayList<>();

    private LinearLayout mProvDetailedCaseButton;

    private Marker marker;
    private ArrayList<CasesTimeSeries.Statewise> StateList = new ArrayList<>();

    private int arraySizeBefore;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mProvCollectedData = view.findViewById(R.id.prov_collected_data);

        mProvDetailedCaseButton = view.findViewById(R.id.prov_detailed_case);

        mSlideUpLayout = view.findViewById(R.id.sliding_layout);

        mFilter = view.findViewById(R.id.prov_filter);

        addData();

        mFilter.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mSlideUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

        return view;
    }

    private void filter(String toString) {

        arraySizeBefore = markerArrayList.size();

        filteredList.clear();
        googleMap.clear();
        marker.remove();
        Percentage = provListData.get(0).getConfirmed();



        for (CasesTimeSeries.Statewise theProvData : provListData) {

            String NAME = theProvData.getState();
            String ConfirmCases = theProvData.getConfirmed();
            String DeathCases = theProvData.getDeaths();
            String RecoveredCases = theProvData.getRecovered();
            String ActiveCases = theProvData.getActive();
            if (NAME.toLowerCase().equals("toltal") || NAME.toLowerCase().equals("state unassigned") ){
                provListData.remove(theProvData);
            }
            double observer = Double.parseDouble(ConfirmCases);
            double total  = Double.parseDouble(Percentage);
            double truePercentage = (observer/total) * 100;

            DecimalFormat form = new DecimalFormat("0.00");
            String FinalPercentage = form.format(truePercentage);
            if (theProvData.getState().toLowerCase().contains(toString.toLowerCase())) {

                filteredList.add(theProvData);

                for (int i = 0; i < CoordinateListData.size(); i++) {
                    if (CoordinateListData.get(i).getTitle().toLowerCase().contains(toString.toLowerCase())) {
                        double lng = CoordinateListData.get(i).getLng();
                        double lat = CoordinateListData.get(i).getLat();

                        LatLng latLng = new LatLng(lat, lng);
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot32))
                                );
                        markerArrayList.add(marker);
                    }
                }

            }
        }
        provAdapter.filterList(filteredList);
    }


    private void setupRecyclerView(RecyclerView mProvRecyclerView, CasesTimeSeries casesTimeSeries) {

        List<CasesTimeSeries.Statewise> provListData = casesTimeSeries.getStatewise();
        for(int  i=0;i<provListData.size();i++){
            if(provListData.get(i).getState().toLowerCase().equals("total") || provListData.get(i).getState().toLowerCase().equals("state unassigned") ){
               provListData.remove(i);
            }
        }
        this.provListData.addAll(provListData);
        this.filteredList.addAll(provListData);


        provAdapter = new StateAdapter(this.provListData, this);
        mProvRecyclerView.setAdapter(provAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mProvRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void hideLoading(ShimmerFrameLayout mProvCardShimmer, RecyclerView mProvRecyclerView) {

        mProvCardShimmer.setVisibility(View.GONE);

        mProvRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showLoading(ShimmerFrameLayout mProvCardShimmer, RecyclerView mProvRecyclerView) {

        mProvCardShimmer.setVisibility(View.VISIBLE);

        mProvRecyclerView.setVisibility(View.GONE);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.prov_map_view);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    public void addData(){

        Cordinates cordinatesMah = new Cordinates(19.690982, 75.550248,"Maharashtra");
        CoordinateListData.add(cordinatesMah);
        Cordinates cordinatesTn = new Cordinates(11.796636, 78.759948,"Tamil Nadu");
        CoordinateListData.add(cordinatesTn);
        Cordinates cordinatesDl = new Cordinates(28.942164, 77.183190,"Delhi");
        CoordinateListData.add(cordinatesDl);
        Cordinates cordinatesGJ = new Cordinates(23.653630, 70.819204,"Gujarat");
        CoordinateListData.add(cordinatesGJ);
        Cordinates cordinatesUP = new Cordinates(28.349123, 79.660682,"Uttar Pradesh");
        CoordinateListData.add(cordinatesUP);
        Cordinates cordinatesRJ = new Cordinates(27.181908, 73.526740,"Rajasthan");
        CoordinateListData.add(cordinatesRJ);
        Cordinates cordinatesWB = new Cordinates(23.416063, 87.055758,"West Bengal");
        CoordinateListData.add(cordinatesWB);
        Cordinates cordinatesMP = new Cordinates(24.012386, 77.060779,"Madhya Pradesh");
        CoordinateListData.add(cordinatesMP);
        Cordinates cordinatesHR = new Cordinates(29.527890,76.061989,"Haryana");
        CoordinateListData.add(cordinatesHR);
        Cordinates cordinatesKA = new Cordinates(15.014315, 75.617181,"Karnataka");
        CoordinateListData.add(cordinatesKA);
        Cordinates cordinatesAP = new Cordinates(15.328894, 78.990191,"Andhra Pradesh");
        CoordinateListData.add(cordinatesAP);
        Cordinates cordinatesB = new Cordinates(25.883254, 86.101776,"Bihar");
        CoordinateListData.add(cordinatesB);
        Cordinates cordinatesTL = new Cordinates(18.141666, 79.151323,"Telangana");
        CoordinateListData.add(cordinatesTL);
        Cordinates cordinatesJK = new Cordinates(34.428210, 75.980964,"Jammu and Kashmir");
        CoordinateListData.add(cordinatesJK);
        Cordinates cordinatesAS = new Cordinates(26.736529, 92.592061,"Assam");
        CoordinateListData.add(cordinatesAS);
        Cordinates cordinatesOD = new Cordinates(20.713258, 84.287324,"Odisha");
        CoordinateListData.add(cordinatesOD);
        Cordinates cordinatesPN = new Cordinates(30.529209, 74.988129,"Punjab");
        CoordinateListData.add(cordinatesPN);
        Cordinates cordinatesKL = new Cordinates(9.601549, 76.690775,"Kerala");
        CoordinateListData.add(cordinatesKL);
        Cordinates cordinatesUK = new Cordinates(30.280571, 78.983632,"Uttarakhand");
        CoordinateListData.add(cordinatesUK);
        Cordinates cordinatesCH = new Cordinates(22.391532, 82.031522,"Chhattisgarh");
        CoordinateListData.add(cordinatesCH);
        Cordinates cordinatesJD = new Cordinates(23.915636, 85.121640,"Jharkhand");
        CoordinateListData.add(cordinatesJD);
        Cordinates cordinatesTP = new Cordinates(23.598034, 91.406396,"Tripura");
        CoordinateListData.add(cordinatesTP);
        Cordinates cordinatesLD = new Cordinates(34.269310, 77.933121,"Ladakh");
        CoordinateListData.add(cordinatesLD);
        Cordinates cordinatesGA = new Cordinates(15.448160, 73.804391,"Goa");
        CoordinateListData.add(cordinatesGA);
        Cordinates cordinatesHP = new Cordinates(32.398268, 76.796711,"Himachal Pradesh");
        CoordinateListData.add(cordinatesHP);
        Cordinates cordinatesMN = new Cordinates(24.845692, 93.764391,"Manipur");
        CoordinateListData.add(cordinatesMN);
        Cordinates cordinatesCG = new Cordinates(30.964404, 76.676763,"Chandigarh");
        CoordinateListData.add(cordinatesCG);
        Cordinates cordinatesPC = new Cordinates(12.265900, 79.906640,"Puducherry");
        CoordinateListData.add(cordinatesPC);
        Cordinates cordinatesNL = new Cordinates(26.331914, 94.538687,"Nagaland");
        CoordinateListData.add(cordinatesNL);
        Cordinates cordinatesMZ = new Cordinates(23.655367, 92.814876,"Mizoram");
        CoordinateListData.add(cordinatesMZ);
        Cordinates cordinatesARP = new Cordinates(28.793459, 94.988276,"Arunachal Pradesh");
        CoordinateListData.add(cordinatesARP);
        Cordinates cordinatesSK = new Cordinates(27.614248, 88.441966,"Sikkim");
        CoordinateListData.add(cordinatesSK);
        Cordinates cordinatesDD = new Cordinates(20.207920, 73.053498,"Dadra and Nagar Haveli and Daman and Diu");
        CoordinateListData.add(cordinatesDD);
        Cordinates cordinatesAN = new Cordinates(12.186606, 92.701620,"Andaman and Nicobar Islands");
        CoordinateListData.add(cordinatesAN);
        Cordinates cordinatesMG = new Cordinates(25.820801, 90.995636,"Meghalaya");
        CoordinateListData.add(cordinatesMG);
        Cordinates cordinatesLA = new Cordinates(10.267554, 73.608656,"Lakshadweep");
        CoordinateListData.add(cordinatesLA);

        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(this.getContext()));

        this.googleMap = googleMap;

        final double LAT = 18.593683;
        final double LNG = 77.962883;
        final float ZOOM = 5.0f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LAT, LNG), ZOOM);

        googleMap.moveCamera(cameraUpdate);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.getContext(), R.raw.style_json));

        ShimmerFrameLayout mProvCardShimmer = Objects.requireNonNull(this.getView()).findViewById(R.id.prov_shimmer);

        RecyclerView mProvRecyclerView = this.getView().findViewById(R.id.prov_rv);

        //data fetch

        StateData  stateData;

        stateData = ViewModelProviders.of(this).get(StateData.class);
        stateData.init();

        stateData.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showLoading(mProvCardShimmer,mProvRecyclerView);
                }else {
                    hideLoading(mProvCardShimmer,mProvRecyclerView);
                }
            }
        });

        stateData.getRegulardatas().observe(this, new Observer<CasesTimeSeries>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onChanged(CasesTimeSeries casesTimeSeries) {

                List<CasesTimeSeries.Statewise> statewiseList = casesTimeSeries.getStatewise();
                Percentage = statewiseList.get(0).getConfirmed();
                for(CasesTimeSeries.Statewise Statewisedata :  statewiseList ){
                String NAME = Statewisedata.getState();


                   for(int i = 0;i<CoordinateListData.size();i++){
                       if (NAME.toLowerCase().equals(CoordinateListData.get(i).getTitle().toLowerCase())){
                          double lng = CoordinateListData.get(i).getLng();
                          double lat = CoordinateListData.get(i).getLat();

                           LatLng latLng = new LatLng(lat,lng);
                           marker = googleMap.addMarker(new MarkerOptions()
                                   .position(latLng)
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot32))
                                   );
                           markerArrayList.add(marker);
                       }

                   }

                }

                LoadLocale loadLocale = new LoadLocale(getActivity());

                if (loadLocale.getLocale().equals("hi")) {
                    mProvCollectedData.setText("आकड़ों को एकत्र किया गया");

                } else {
                    mProvCollectedData.setText("Data Collected" );

                }

                googleMap.setInfoWindowAdapter(new ProvInfoWindowAdapter());
                setupBottomSheet(mProvDetailedCaseButton, casesTimeSeries);
                setupRecyclerView(mProvRecyclerView, casesTimeSeries);
            }
        });

    }
    private void setupBottomSheet(LinearLayout mProvDetailedCaseButton, CasesTimeSeries casesTimeSeries) {
        Toast mToast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
//        BottomSheetMapsDialog bottomSheetMapsDialog = new BottomSheetMapsDialog();

        mProvDetailedCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                mToast.setText("Other data coming soon!");
                mToast.show();
            }
        });

    }

    @Override
    public void onListClicked(int position) {
        int zoom = (int) googleMap.getCameraPosition().zoom;
        double lng;
        double lat;
        mFilter.clearFocus();
        markerArrayList.get(position + arraySizeBefore).showInfoWindow();
        String NAME = filteredList.get(position).getState();
        for(int i = 0;i<CoordinateListData.size();i++) {
            if (NAME.toLowerCase().equals(CoordinateListData.get(i).getTitle().toLowerCase())) {
                 lnglist = CoordinateListData.get(i).getLng();
                 latlist = CoordinateListData.get(i).getLat();
                final float ZOOM = 7;
            }
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latlist, lnglist), ZOOM);

        final Handler handler = new Handler();

        //Tricky part that collapse the slide up panel after 200ms (the keyboard already hide)
        //Can't find another approach to do the same

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.animateCamera(cameraUpdate, 1000, null);
                mSlideUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        },200);

    }
    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(value));
    }


    private class ProvInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        View view = getLayoutInflater().inflate(R.layout.info_window_prov, null);

        @SuppressLint({"CutPasteId", "DefaultLocale"})
        @Override
        public View getInfoWindow(Marker marker) {
            TextView mProvName, mProvCase, mProvDeath, mProvCured, mProvTreated, mProvPercentage,StateNoteText;

            mProvName = view.findViewById(R.id.info_window_prov_name);
            mProvCase = view.findViewById(R.id.info_window_prov_case);
            mProvDeath = view.findViewById(R.id.info_window_prov_death);
            mProvCured = view.findViewById(R.id.info_window_prov_cured);
            mProvTreated = view.findViewById(R.id.info_window_prov_treated);
            mProvPercentage = view.findViewById(R.id.info_window_prov_percentage);
            StateNoteText = view.findViewById(R.id.StateNotes);




            String id = marker.getId();
            int convId = Integer.parseInt(id.replaceAll("[^\\d.]", "")) - arraySizeBefore;

           CasesTimeSeries.Statewise provListData = filteredList.get(convId);
            String ConfirmCases = provListData.getConfirmed();


            double observer = Double.parseDouble(ConfirmCases);
            double total  = Double.parseDouble(Percentage);
            double truePercentage = (observer/total) * 100;

            DecimalFormat form = new DecimalFormat("0.00");
            String FinalPercentage = form.format(truePercentage);

            mProvName.setText(provListData.getState());
            int ConfirmCasesInt = Integer.parseInt(provListData.getConfirmed());
            mProvCase.setText(numberSeparator(ConfirmCasesInt));
            int DeathCasesInt=Integer.parseInt(provListData.getDeaths());
            mProvDeath.setText(numberSeparator(DeathCasesInt));
            int RecoverCasesInt = Integer.parseInt(provListData.getRecovered());
            mProvCured.setText(numberSeparator(RecoverCasesInt));
            int ActiveCasesInt  = Integer.parseInt(provListData.getActive());
            mProvTreated.setText(numberSeparator(ActiveCasesInt));
            mProvPercentage.setText(FinalPercentage);
            StateNoteText.setText(provListData.getStatenotes());


            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }

    }


