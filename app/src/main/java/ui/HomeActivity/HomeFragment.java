package ui.HomeActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment {

    MapView mMapViewFriends;
    private GoogleMap googleMapFriends;

    MapView mMapViewDiscos;
    private GoogleMap googleMapDiscos;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mMapViewFriends = rootView.findViewById(R.id.mapViewFriends);
        mMapViewFriends.onCreate(savedInstanceState);
        mMapViewFriends.onResume(); // needed to get the map to display immediately

        mMapViewDiscos = rootView.findViewById(R.id.mapViewDiscos);
        mMapViewDiscos.onCreate(savedInstanceState);
        mMapViewDiscos.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapViewFriends.getMapAsync(mMap -> {
            googleMapFriends = mMap;

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                googleMapFriends.setMyLocationEnabled(true);
            }

            Bundle fullScreenBundle = new Bundle();

            // For dropping a marker at a point on the Map
            LatLng sydney = new LatLng(-34, 151);
            fullScreenBundle.putParcelable(FullScreenMapFragment.ARG_MARKER, sydney);
            googleMapFriends.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//            googleMapFriends.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            fullScreenBundle.putInt(FullScreenMapFragment.ARG_MAP_NAME_COLOR_ID, R.color.shiny_disco_purple);
            fullScreenBundle.putInt(FullScreenMapFragment.ARG_MAP_NAME_ID, R.string.friends_nearby);
            googleMapFriends.setOnMapClickListener(map -> {
                getParentFragmentManager().beginTransaction()
                        .add(R.id.home_fragment_container, FullScreenMapFragment.class, fullScreenBundle)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            });
        });

        mMapViewDiscos.getMapAsync(mMap -> {
            googleMapDiscos = mMap;

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                googleMapDiscos.setMyLocationEnabled(true);
            }

            Bundle fullScreenBundle = new Bundle();

            // For dropping a marker at a point on the Map
            LatLng sydney = new LatLng(-34, 30);
            fullScreenBundle.putParcelable(FullScreenMapFragment.ARG_MARKER, sydney);
            googleMapDiscos.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
            googleMapDiscos.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            fullScreenBundle.putInt(FullScreenMapFragment.ARG_MAP_NAME_COLOR_ID, R.color.shiny_disco_pink);
            fullScreenBundle.putInt(FullScreenMapFragment.ARG_MAP_NAME_ID, R.string.explore_discos);
            googleMapDiscos.setOnMapClickListener(map -> {
                getParentFragmentManager().beginTransaction()
                        .add(R.id.home_fragment_container, FullScreenMapFragment.class, fullScreenBundle)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            });
        });

        return rootView;
    }

    public GoogleMap getGoogleMapFriends() {
        return googleMapFriends;
    }

    public GoogleMap getGoogleMapDiscos() {
        return googleMapDiscos;
    }
}