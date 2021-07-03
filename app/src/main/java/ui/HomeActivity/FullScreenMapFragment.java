package ui.HomeActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import data.Disco;
import data.User;
import ui.AddDiscoScreen.AddDiscoFragment;
import ui.ViewDiscoScreen.ViewDiscoFragment;
import utils.Firebase;

public class FullScreenMapFragment extends Fragment {

    public static final String ARG_MARKER = "marker_latLng";
    public static final String ARG_MAP_VIEW_ID = "map_view_id";
    public static final String ARG_MAP_NAME_ID = "map_name";
    public static final String ARG_MAP_NAME_COLOR_ID = "map_name_color";

    private LatLng markerLatLng;
    private int mapNameResId;
    private int mapNameColorResId;
    private int mapViewId;

    private MapView mMapViewFullScreen;
    private GoogleMap googleMap;
    private List<MarkerOptions> googleMapMarkers;
    private List<Target> markerTargets;

    private TextView mapNameView;
    private FloatingActionButton addDiscoButton;

    private FullScreenMapViewModel viewModel;

    public FullScreenMapFragment() {
        // Required empty public constructor
    }

//    public static FullScreenMapFragment newInstance(String param1, String param2) {
//        FullScreenMapFragment fragment = new FullScreenMapFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_SYDNEY, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            markerLatLng = arguments.getParcelable(ARG_MARKER);
            mapNameResId = arguments.getInt(ARG_MAP_NAME_ID);
            mapNameColorResId = arguments.getInt(ARG_MAP_NAME_COLOR_ID);
            mapViewId = arguments.getInt(ARG_MAP_VIEW_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel = new ViewModelProvider(this).get(FullScreenMapViewModel.class);
        switch (mapViewId) {
            case R.id.mapViewFriends:
                FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
                if (loggedInUser != null) {
                    Firebase.getDbRef().child(Firebase.DB_USERS).child(loggedInUser.getUid()).get().addOnSuccessListener(snapshot -> {
                        User user = snapshot.getValue(User.class);
                        if (user.getFriends() != null && user.getFriends().size() > 0) {
                            viewModel.getFriends(user.getFriends().keySet()).observe(this, friends -> {
                                googleMapMarkers = new ArrayList<>();
                                markerTargets = new ArrayList<>();
                                googleMap.clear();

                                for (int i = 0; i < friends.size(); i++) {
                                    User friend = friends.get(i);
                                    markerTargets.add(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            MarkerOptions marker = new MarkerOptions();
                                            marker.position(new LatLng(friend.getLat(), friend.getLon()));
                                            marker.title(friend.getName());
                                            marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                            marker.snippet(friend.getUid());

                                            googleMapMarkers.add(marker);
                                            googleMap.addMarker(marker);
                                            googleMap.setOnInfoWindowClickListener(friendClickListener);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });

                                    Picasso.get().load(friend.getImageUrl()).into(markerTargets.get(i));
                                }
                            });
                        }
                    });
                }
                break;
            case R.id.explore_discos_image:
                viewModel.getDiscos().observe(this, discos -> {
                    googleMapMarkers = new ArrayList<>();
                    markerTargets = new ArrayList<>();
                    googleMap.clear();

                    for (int i = 0; i < discos.size(); i++) {
                        Disco d = discos.get(i);
                        markerTargets.add(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(new LatLng(d.getLat(), d.getLon()));
                                marker.title(d.getName());
                                marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                marker.snippet(d.getId());

                                googleMapMarkers.add(marker);
                                googleMap.addMarker(marker);
                                googleMap.setOnInfoWindowClickListener(discoClickListener);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                        Picasso.get().load(d.getImageUrl()).into(markerTargets.get(i));
                    }

                });
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_screen_map, container, false);

        mMapViewFullScreen = rootView.findViewById(R.id.mapView_fullscreen);
        mMapViewFullScreen.onCreate(savedInstanceState);
        mMapViewFullScreen.onResume(); // needed to get the map to display immediately
        mapNameView = rootView.findViewById(R.id.map_name);
        addDiscoButton = rootView.findViewById(R.id.add_disco_button);

        if (mapViewId != R.id.explore_discos_image) {
            addDiscoButton.setVisibility(View.GONE);
        } else {
            addDiscoButton.setOnClickListener(btn -> {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && mapViewId == R.id.explore_discos_image)
                    LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation()
                            .addOnSuccessListener(location -> {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Bundle addDiscoBundle = new Bundle();
                                addDiscoBundle.putParcelable("location", currentLatLng);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.home_fragment_container, AddDiscoFragment.class, addDiscoBundle)
                                        .setReorderingAllowed(true)
                                        .addToBackStack(null)
                                        .commit();
                            });
            });
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapViewFullScreen.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_dark));

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                googleMap.setMyLocationEnabled(true);
            }

            if (mapNameResId != 0) {
                mapNameView.setText(mapNameResId);
            }

            if (mapNameColorResId != 0)
                mapNameView.setTextColor(ContextCompat.getColor(getContext(), mapNameColorResId));

            if (googleMapMarkers != null) {
                for (MarkerOptions marker : googleMapMarkers)
                    googleMap.addMarker(marker);
            }

        });

        return rootView;
    }

    GoogleMap.OnInfoWindowClickListener discoClickListener = marker -> {
        String discoId = marker.getSnippet();
        Bundle bundle = new Bundle();
        bundle.putString(ViewDiscoFragment.ARG_DISCO_ID, discoId);
        getParentFragmentManager().beginTransaction()
                .add(R.id.home_fragment_container, ViewDiscoFragment.class, bundle)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    };

    GoogleMap.OnInfoWindowClickListener friendClickListener = marker -> {
//        String friendId = marker.getSnippet();
//        Bundle bundle = new Bundle();
//        bundle.putString(ViewDiscoFragment.ARG_DISCO_ID, discoId);
//        getParentFragmentManager().beginTransaction()
//                .add(R.id.home_fragment_container, ViewDiscoFragment.class, bundle)
//                .setReorderingAllowed(true)
//                .addToBackStack(null)
//                .commit();
    };

}