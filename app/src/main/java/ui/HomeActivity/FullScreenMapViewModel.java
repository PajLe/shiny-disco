package ui.HomeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import data.Disco;
import utils.Firebase;

public class FullScreenMapViewModel extends ViewModel {
    MutableLiveData<List<Disco>> discosLiveData = new MutableLiveData<>();
//    MutableLiveData<List<Friend>> friendLiveData = new MutableLiveData<>();

    private DatabaseReference discoDbRef;

    public FullScreenMapViewModel() {
        discoDbRef = Firebase.getDbRef();
//        getFriends();
    }

    public LiveData<List<Disco>> getDiscos() {
        if (discosLiveData.getValue() == null) {
            discoDbRef.child(Firebase.DB_DISCOS).addListenerForSingleValueEvent(discosEventListener);
        }

        return discosLiveData;
    }

    ValueEventListener discosEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                discosLiveData.postValue(toDiscos(snapshot));
            }
        }

        private List<Disco> toDiscos(DataSnapshot snapshot) {
            List<Disco> discos = new ArrayList<>();
            for (DataSnapshot discoSnapshot: snapshot.getChildren()) {
                Disco disco = discoSnapshot.getValue(Disco.class);
                discos.add(disco);
            }

            return discos;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
