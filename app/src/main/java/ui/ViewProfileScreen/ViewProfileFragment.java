package ui.ViewProfileScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shinydisco.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import data.User;
import utils.Firebase;

public class ViewProfileFragment extends Fragment {

    public static final String ARG_USER_ID = "userId";

    private String uid;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    public static ViewProfileFragment newInstance(String uid) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    private User user;

    private TextView userIdTextView;
    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView rankTextView;
    private TextView rankPointsTextView;
    private ImageView profileImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_USER_ID);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);

        // initialize views
        userIdTextView = rootView.findViewById(R.id.profile_uid_value_textview);
        usernameTextView = rootView.findViewById(R.id.profile_username_value_textview);
        nameTextView = rootView.findViewById(R.id.profile_name_value_textview);
        emailTextView = rootView.findViewById(R.id.profile_email_value_textview);
        rankTextView = rootView.findViewById(R.id.profile_rank_value_textview);
        rankPointsTextView = rootView.findViewById(R.id.profile_rank_points_value_textview);
        profileImageView = rootView.findViewById(R.id.profile_imageview);

        if (uid != null) {
            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnSuccessListener(snapshot -> {
                user = snapshot.getValue(User.class);

                // set views
                userIdTextView.setText(user.getUid());
                usernameTextView.setText(user.getUsername());
                nameTextView.setText(user.getName());
                emailTextView.setText(user.getEmail());
                rankTextView.setText(user.getRank());
                rankPointsTextView.setText(user.getRankPoints() + "");
                Picasso.get().load(user.getImageUrl()).into(profileImageView);

            });
        }

        return rootView;
    }
}