package ir.sverr.gridsearch;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Search500px extends Activity {

    public static final String TAG_PHOTOS = "photos";
    public static final String TAG_NAME = "name";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_IMG_URL = "image_url";
    public static final String TAG_FULL_URL = "url";
    public static final String TAG_USER = "user";
    public static final String TAG_FULL_NAME = "fullname";
    public static final String TAG_RATING = "rating";
    public static final String TAG_FAVORITES = "favorites_count";
    public static final String TAG_TOTAL_PAGES = "total_pages";

    private static final int max_pages = 2;

    private static ArrayList<String> image_urls; //TEST

    private static int nPhotos;

    private static ArrayList<HashMap<String, String>> photo_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search500px);
        final EditText editText = (EditText) findViewById(R.id.input_box);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v.getText().toString(), editText);  //Search is initiated with the user input.
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        final EditText editText = (EditText) findViewById(R.id.input_box);
        editText.setError(null);     //clearing any search error messages
    }

    public void search(String input, EditText editText) {

        if(input.equals("")) {    //Checks if the user did indeed search for something.
            editText.setError(getString(R.string.no_string_error));
            return;
        }

        if(!isOnline()) {         //Checks if there is an internet connection.
            editText.setError(getString(R.string.no_network_error));
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);   //Hides the keyboard as it is not needed right now.

        String search_input = "https://api.500px.com/v1/photos/search?term="
                + input + "&consumer_key=" + getResources().getString(R.string.consumer_key);
        String JSON_first_search = "";             //Communication with 500px.

        try {
            JSON_first_search = new FetchStringFromUrl().execute(search_input).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JSON_first_search == null || JSON_first_search.equals("")) { //Using short-circuited OR to prevent NullPointerException
            editText.setError(getString(R.string.no_network_error));
            return;
        } else {
            try {
                JSONObject jsonFirstSearch = new JSONObject(JSON_first_search); //Parsing the JSON-string follows
                int total_pages = jsonFirstSearch.getInt(TAG_TOTAL_PAGES);
                if(total_pages==0) {
                    editText.setError(getString(R.string.no_results_error));
                    return;
                }

                image_urls = new ArrayList<String>();
                photo_list = new ArrayList<HashMap<String, String>>();

                String current_search = "";

                int max_pages_to_load = Math.min(max_pages,total_pages);

                for(int page = 1; page <= max_pages_to_load; page++) {
                    current_search += search_input + "&page=" + page;
                    String JSON_result = "";
                    try {
                        JSON_result = new FetchStringFromUrl().execute(current_search).get();
                    } catch (InterruptedException i) {
                        i.printStackTrace();
                        return;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        return;
                    }
                    JSONObject jsonObj = new JSONObject(JSON_result);
                    JSONArray photos = jsonObj.getJSONArray(TAG_PHOTOS);
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photo = photos.getJSONObject(i);
                        String name = photo.getString(TAG_NAME);
                        String description = photo.getString(TAG_DESCRIPTION);
                        String img_url = photo.getString(TAG_IMG_URL);
                        String rating = photo.getString(TAG_RATING);
                        String favorites = photo.getString(TAG_FAVORITES);
                        String full_url = "http://500px.com" + photo.getString(TAG_FULL_URL);

                        JSONObject user = photo.getJSONObject(TAG_USER);
                        String full_name = user.getString(TAG_FULL_NAME);

                        HashMap<String, String> photo_info = new HashMap<String, String>();
                        photo_info.put(TAG_NAME, name);
                        photo_info.put(TAG_DESCRIPTION, description);
                        photo_info.put(TAG_RATING, rating);
                        photo_info.put(TAG_FAVORITES, favorites);
                        photo_info.put(TAG_IMG_URL, img_url);

                        image_urls.add(img_url);

                        photo_info.put(TAG_FULL_URL, full_url);
                        photo_info.put(TAG_FULL_NAME, full_name);
                        photo_list.add(photo_info);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        nPhotos = photo_list.size();
        loadPhotos();
    }

    public void loadPhotos() {
        final GridView picGrid = (GridView) findViewById(R.id.grid_view);
        ArrayList<SquareImageView> images = new ArrayList<SquareImageView>();

        for(int i=0;i<nPhotos;i++) {
            final SquareImageView cellView = new SquareImageView(this);
            String imageURL = photo_list.get(i).get(TAG_IMG_URL);
            Picasso.with(getApplicationContext()).load(imageURL).into(cellView);
            images.add(cellView);
        }
        picGrid.setAdapter(new GridViewImageAdapter(this));
    }

    public static ArrayList<String> getImageUrls() {
        return image_urls;
    }

    public static ArrayList<HashMap<String, String>> getPhotoList() {
        return photo_list;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isLargeScreen(Context context) {  //Source: http://stackoverflow.com/a/11330947/2784835
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
