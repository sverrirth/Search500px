package ir.sverr.gridsearch;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int position = getArguments().getInt("POSITION");
        String image_url = Search500px.getImageUrls().get(position);

        ArrayList<HashMap<String, String>> photoList = Search500px.getPhotoList();

        HashMap<String,String> pic_info = photoList.get(position);

        pic_info.put(Search500px.TAG_NAME,pic_info.get(Search500px.TAG_NAME).
                replaceAll("\\<.*?>", ""));
        pic_info.put(Search500px.TAG_DESCRIPTION,pic_info.get(Search500px.TAG_DESCRIPTION).
                 replaceAll("\\<.*?>", ""));   //Removal of all HTML

        final ImageView imageView = (ImageView) rootView.findViewById(R.id.selected_pic);
        Picasso.with(getActivity().getApplicationContext()).load(image_url).into(imageView);

        final TextView infoView = (TextView) rootView.findViewById(R.id.pic_info);
        infoView.setText(Html.fromHtml("<b>" + getString(R.string.tag_photo_title) + ":</b> " + pic_info.get(Search500px.TAG_NAME)
                + "<br><b>" + getString(R.string.tag_artist) + ":</b> " + pic_info.get(Search500px.TAG_FULL_NAME)
                + "<br><b>" + getString(R.string.tag_description) + ":</b> " + pic_info.get(Search500px.TAG_DESCRIPTION)
                + "<br><b>" + getString(R.string.tag_favorites) + ":</b> " + pic_info.get(Search500px.TAG_FAVORITES)
                + "<br><b>" + getString(R.string.tag_rating) + ":</b> " + pic_info.get(Search500px.TAG_RATING)
                + "<br><b>" + getString(R.string.tag_website) + ":</b> " + pic_info.get(Search500px.TAG_FULL_URL)));

        return rootView;
    }
}