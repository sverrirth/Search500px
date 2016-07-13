package ir.sverr.gridsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

final class GridViewImageAdapter extends BaseAdapter {
    private final Context context;

    private static boolean fragment_enabled = false;
    private static DetailFragment detailFragment;

    private List<String> urls = new ArrayList<String>();

    public GridViewImageAdapter(Context context) {
        this.context = context;
        urls = Search500px.getImageUrls();
    }

    @Override public View getView(final int position, View convertView, final ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
        }

        String url = getItem(position);

        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.loading)    //Grey image that looks like the one on 500px.com
                .error(R.drawable.error)            //Darker grey image
                .fit() //
                .tag(context) //
                .into(view);

        view.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                if (Search500px.isLargeScreen(context)) {
                    if(fragment_enabled) {                   //makes sure that the fragment is only attached once
                        DetailFragment newDetailFragment = new DetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("POSITION", position);
                        newDetailFragment.setArguments(bundle);
                        ((Activity) context).getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, newDetailFragment)
                                .commit();
                    } else {
                        detailFragment = new DetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("POSITION", position);
                        detailFragment.setArguments(bundle);
                        final GridView gridView = (GridView) ((Activity) context).findViewById(R.id.grid_view);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gridView.getLayoutParams();
                        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            layoutParams.height = gridView.getHeight()/2;           //the fragment is displayed at different places
                            gridView.setLayoutParams(layoutParams);                 //depending on orientation
                        } else {
                            layoutParams.width = gridView.getWidth()/2;
                            gridView.setLayoutParams(layoutParams);
                        }
                        ((Activity) context).getFragmentManager().beginTransaction()
                                .add(R.id.fragment_container, detailFragment)
                                .commit();
                        fragment_enabled = true;
                    }

                } else {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("POSITION", position);
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}