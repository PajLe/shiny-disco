package ui.FindFriendsScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shinydisco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import data.Disco;
import data.User;


public class FriendSearchAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> userList = new ArrayList<>();

    public FriendSearchAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<User> list) {
        super(context, 0 , list);
        mContext = context;
        userList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.friend_search_view_layout,parent,false);

        User user = userList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        Picasso.get().load(user.getImageUrl()).into(image);

        TextView name = (TextView) listItem.findViewById(R.id.text1);
        name.setText(user.getName());

        TextView release = (TextView) listItem.findViewById(R.id.text2);
        release.setText(user.getUid());

        return listItem;
    }
}
