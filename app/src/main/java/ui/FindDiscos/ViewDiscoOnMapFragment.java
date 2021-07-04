package ui.FindDiscos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shinydisco.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ui.ViewDiscoScreen.ViewDiscoFragment;

public class ViewDiscoOnMapFragment extends Fragment {

    public static final String ARG_LAT = "lat";
    public static final String ARG_LON = "lon";
    public static final String ARG_DISCO_ID = "discoId";
    public static final String ARG_DISCO_IMG = "discoImg";
    public static final String ARG_DISCO_NAME = "discoName";

    private double lat;
    private double lon;
    private String discoId;
    private String discoImgUrl;
    private String discoName;

    private GoogleMap googleMap;
    private MapView mMapViewFullScreen;
    private TextView mapNameView;

    public ViewDiscoOnMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            lat = arguments.getDouble(ARG_LAT);
            lon = arguments.getDouble(ARG_LON);
            discoId = arguments.getString(ARG_DISCO_ID);
            discoImgUrl = arguments.getString(ARG_DISCO_IMG);
            discoName = arguments.getString(ARG_DISCO_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_disco_on_map, container, false);
        mMapViewFullScreen = rootView.findViewById(R.id.mapView_fullscreen);
        mMapViewFullScreen.onCreate(savedInstanceState);
        mMapViewFullScreen.onResume(); // needed to get the map to display immediately
        mapNameView = rootView.findViewById(R.id.map_name);

        mMapViewFullScreen.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_dark));

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                googleMap.setMyLocationEnabled(true);
            }

            LatLng discoLatLng = new LatLng(lat, lon);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(discoLatLng).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Target markerTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(discoLatLng);
                    marker.title(discoName);
                    marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    marker.snippet(discoId);

                    googleMap.addMarker(marker);
                    googleMap.setOnInfoWindowClickListener(discoClickListener);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get().load(discoImgUrl).into(markerTarget);
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
}