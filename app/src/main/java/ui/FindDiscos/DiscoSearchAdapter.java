package ui.FindDiscos;

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


public class DiscoSearchAdapter extends ArrayAdapter<Disco> {

    private Context mContext;
    private List<Disco> discoList = new ArrayList<>();

    public DiscoSearchAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Disco> list) {
        super(context, 0 , list);
        mContext = context;
        discoList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.disco_search_view_layout,parent,false);

        Disco disco = discoList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        Picasso.get().load(disco.getImageUrl()).into(image);

        TextView name = (TextView) listItem.findViewById(R.id.text1);
        name.setText(disco.getName());

        TextView release = (TextView) listItem.findViewById(R.id.text2);
        release.setText(disco.toString());

        return listItem;
    }
}
