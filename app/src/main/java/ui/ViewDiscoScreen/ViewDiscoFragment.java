package ui.ViewDiscoScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import data.Disco;
import data.Rating;
import data.User;
import utils.Firebase;


public class ViewDiscoFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_DISCO_ID = "discoId";

    private String discoId;

    public ViewDiscoFragment() {
        // Required empty public constructor
    }

    private TextView latitudeValueTextView;
    private TextView longitudeValueTextView;
    private TextView nameValueTextView;
    private TextView musicValueTextView;
    private TextView pricingValueTextView;
    private TextView ratingValueTextView;
    private TextView ratingsValueTextView;
    private Spinner ratingSpinner;
    private ImageButton discoImageButton;
    private Button addRatingButton;

    private Disco disco = null;
    private User user = null;

    public static ViewDiscoFragment newInstance(String discoId) {
        ViewDiscoFragment fragment = new ViewDiscoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DISCO_ID, discoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            discoId = getArguments().getString(ARG_DISCO_ID);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_disco, container, false);

        latitudeValueTextView = rootView.findViewById(R.id.disco_latitude_value_textview);
        longitudeValueTextView = rootView.findViewById(R.id.disco_longitude_value_textview);
        nameValueTextView = rootView.findViewById(R.id.disco_name_value_textview);
        musicValueTextView = rootView.findViewById(R.id.disco_music_value_textview);
        pricingValueTextView = rootView.findViewById(R.id.disco_pricing_value_textview);
        ratingValueTextView = rootView.findViewById(R.id.disco_rating_value_textview);
        ratingsValueTextView = rootView.findViewById(R.id.disco_ratings_count_value_textview);
        ratingSpinner = rootView.findViewById(R.id.spinner_disco_add_rating);
        discoImageButton = rootView.findViewById(R.id.disco_image_button);
        addRatingButton = rootView.findViewById(R.id.add_rating_button);

        FirebaseUser currentUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnSuccessListener(snapshot -> {
                this.user = snapshot.getValue(User.class);;

                if (discoId != null) {
                    Firebase.getDbRef().child(Firebase.DB_DISCOS).child(discoId).get().addOnSuccessListener(snapshot2 -> {
                        if (snapshot2.exists()) {
                            Disco disco = snapshot2.getValue(Disco.class);

                            this.disco = disco;

                            latitudeValueTextView.setText(disco.getLat() + "");
                            longitudeValueTextView.setText(disco.getLon() + "");
                            nameValueTextView.setText(disco.getName());

                            StringBuilder musics = new StringBuilder();
                            for (String music : disco.getMusicGenres()) {
                                musics.append(music);
                                musics.append(", ");
                            }
                            musicValueTextView.setText(musics.substring(0, musics.length() - 2));
                            pricingValueTextView.setText(disco.getPricing());
                            ratingValueTextView.setText(String.format("%.2f", disco.getAverageRating()));
                            ratingsValueTextView.setText(disco.getRatings().size() + "");
                            Picasso.get().load(disco.getImageUrl()).into(discoImageButton);

                            for (Rating r : disco.getRatings()) {
                                if (r.getRatedById().equals(user.getUid()))
                                    addRatingButton.setEnabled(false);
                            }
                        }
                    });
                }
            });
        }

        addRatingButton.setOnClickListener(addRatingButtonListener);

        return rootView;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    View.OnClickListener addRatingButtonListener = v -> {

        if (disco != null) {
            List<Rating> ratings = disco.getRatings();
            Rating r = new Rating();
            r.setRatedById(user.getUid());
            r.setRating(Double.parseDouble(ratingSpinner.getSelectedItem().toString()));
            ratings.add(r);

            disco.setAverageRating((disco.getAverageRating() * (disco.getRatings().size() - 1) + r.getRating()) / ratings.size() * 1.0);

            ratingValueTextView.setText(String.format("%.2f", disco.getAverageRating()));
            ratingsValueTextView.setText(disco.getRatings().size() + "");

            Firebase.getDbRef().child(Firebase.DB_DISCOS).child(discoId).setValue(disco).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Rating added successfully", Toast.LENGTH_SHORT).show();
                addRatingButton.setEnabled(false);
            });
        }
    };
}