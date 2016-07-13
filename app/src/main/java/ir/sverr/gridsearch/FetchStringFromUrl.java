package ir.sverr.gridsearch;

import android.os.AsyncTask;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class FetchStringFromUrl extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
        String JSON_result = "";
        try {
            JSON_result = new Scanner(new URL(urls[0]).openStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (MalformedURLException m) {
            System.err.println("Failed to fetch search results from 500px; bad URL.");
            m.printStackTrace();
        } catch (java.io.IOException i) {
            System.err.println("Website failed to load/URL may be bad.");
            i.printStackTrace();
        }
        return JSON_result;
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
