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
                        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        int distance = Integer.parseInt(distanceFromMe.getText().toString()) / 1000;
                        double new_latitude = location.getLatitude() + (distance / 6378) * (180 / Math.PI);
                        double new_longitude = location.getLongitude() + (distance / 6378) * (180 / Math.PI) / Math.cos(location.getLatitude() * Math.PI / 180);

                        discos.removeIf(d -> d.getLat() > new_latitude && d.getLon() > new_longitude);
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

                        }
                    });

                });
            }
        });
        return rootView;
    }
}