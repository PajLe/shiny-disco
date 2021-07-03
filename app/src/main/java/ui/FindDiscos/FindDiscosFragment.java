package ui.FindDiscos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.shinydisco.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import data.Disco;
import utils.Firebase;

public class FindDiscosFragment extends Fragment {
    private ArrayAdapter adapter_disco_search_result;
    private ListView lv_disco_search_result;
    private EditText searchBox;

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

        this.searchBox = rootView.findViewById(R.id.disco_search_box);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //TODO Check with Paja
                Firebase.getDbRef().child(Firebase.DB_DISCOS).get().addOnSuccessListener(snapshot -> {
                    ArrayList<Disco> discos = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Disco disco = child.getValue(Disco.class);
                        discos.add(disco);
                    }
                    lv_disco_search_result = (ListView) rootView.findViewById(R.id.lv_disco_search_result);
                    lv_disco_search_result.setAdapter(new ArrayAdapter<Disco>(getActivity().getApplicationContext(), R.layout.disco_search_view_layout, discos));
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }
}