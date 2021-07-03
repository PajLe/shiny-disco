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
import java.util.Collection;
import java.util.List;

import data.Disco;
import data.User;
import utils.Firebase;

public class FullScreenMapViewModel extends ViewModel {
    MutableLiveData<List<Disco>> discosLiveData = new MutableLiveData<>();
    MutableLiveData<List<User>> friendLiveData = new MutableLiveData<>();

    private DatabaseReference discoDbRef;

    public FullScreenMapViewModel() {
        discoDbRef = Firebase.getDbRef();
    }

    public LiveData<List<User>> getFriends(Collection<String> friendsIds) {

        if (friendLiveData.getValue() == null) {
            friendLiveData.setValue(new ArrayList<User>());
            for (String friendId : friendsIds) {
                discoDbRef.child(Firebase.DB_USERS).child(friendId).addValueEventListener(friendEventListener);
            }
        }

        return friendLiveData;
    }

    public LiveData<List<Disco>> getDiscos() {
        if (discosLiveData.getValue() == null) {
            discoDbRef.child(Firebase.DB_DISCOS).addValueEventListener(discosEventListener);
        }

        return discosLiveData;
    }

    ValueEventListener friendEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            User friend = snapshot.getValue(User.class);
            List<User> users = friendLiveData.getValue();

            int i = 0;
            for (; i < users.size(); i++) {
                if (users.get(i).getUid().equals(friend.getUid())) {
                    users.set(i, friend);
                    break;
                }
            }
            if (i == users.size())
                users.add(friend);

            friendLiveData.postValue(users);
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

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
