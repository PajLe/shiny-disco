package ui.FindDiscos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.shinydisco.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import data.Disco;
import ui.ViewDiscoScreen.ViewDiscoFragment;
import utils.Firebase;
import utils.MultiSpinner;

public class FindDiscosFragment extends Fragment {
    private ListView lv_disco_search_result;

    public FindDiscosFragment() {
        // Required empty public constructor
    }

    public static FindDiscosFragment newInstance(String param1, String param2) {
        FindDiscosFragment fragment = new FindDiscosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_discos, container, false);

        Button btn = rootView.findViewById(R.id.disco_search_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                EditText searchDiscosTextBox = (EditText) rootView.findViewById(R.id.disco_search_box);
                EditText distanceFromMe = (EditText) rootView.findViewById(R.id.distance_from_me);
                Spinner discoRating = (Spinner) rootView.findViewById(R.id.spinner_disco_rating);
                Spinner price = (Spinner) rootView.findViewById(R.id.spinner_disco_price);
                MultiSpinner music = (MultiSpinner) rootView.findViewById(R.id.spinner_disco_music);

                Firebase.getDbRef().child(Firebase.DB_DISCOS).get().addOnSuccessListener(snapshot -> {
                    ArrayList<Disco> discos = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Disco disco = child.getValue(Disco.class);
                        discos.add(disco);
                    }
                    if (!(searchDiscosTextBox.getText().toString() == null || searchDiscosTextBox.getText().toString().length() == 0)) {
                        discos.removeIf(d -> !d.getName().contains(searchDiscosTextBox.getText().toString()));
                    }
                    if (!(distanceFromMe.getText().toString() == null || distanceFromMe.getText().toString().length() == 0)) {
                        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        @SuppressLint("MissingPermission") Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        double distance = Double.parseDouble(distanceFromMe.getText().toString());

                        discos.removeIf(d -> {
                            Location discoLocation = new Location("");
                            discoLocation.setLongitude(d.getLon());
                            discoLocation.setLatitude(d.getLat());
                            return myLocation.distanceTo(discoLocation) > distance;
                        });
                    }
                    discos.removeIf(d -> !d.getPricing().equals(price.getSelectedItem().toString()));
                    discos.removeIf(d -> d.getAverageRating() < Double.parseDouble(discoRating.getSelectedItem().toString()));
                    if(!music.getSelected().isEmpty())
                        discos.removeIf(d -> {
                            boolean result = false;
                            for (String musicGenre : d.getMusicGenres()) {
                                if(music.getSelected().contains(musicGenre))
                                    result = true;
                            }
                            return !result;
                        });
                    lv_disco_search_result = (ListView) rootView.findViewById(R.id.lv_disco_search_result);
                    lv_disco_search_result.setAdapter(new DiscoSearchAdapter(getActivity().getApplicationContext(), discos));

                    lv_disco_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Disco d = (Disco)parent.getItemAtPosition(position);
                            Bundle discoBundle = new Bundle();
                            discoBundle.putDouble(ViewDiscoOnMapFragment.ARG_LON, d.getLon());
                            discoBundle.putDouble(ViewDiscoOnMapFragment.ARG_LAT, d.getLat());
                            discoBundle.putString(ViewDiscoOnMapFragment.ARG_DISCO_ID, d.getId());
                            discoBundle.putString(ViewDiscoOnMapFragment.ARG_DISCO_NAME, d.getName());
                            discoBundle.putString(ViewDiscoOnMapFragment.ARG_DISCO_IMG, d.getImageUrl());

                            getParentFragmentManager().beginTransaction()
                                    .add(R.id.home_fragment_container, ViewDiscoOnMapFragment.class, discoBundle)
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                });
            }
        });
        return rootView;
    }
}