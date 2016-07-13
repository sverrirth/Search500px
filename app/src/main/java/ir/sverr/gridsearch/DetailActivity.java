package ir.sverr.gridsearch;

import android.app.Activity;
import android.os.Bundle;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeholder_activity); //This activity is not used
        Bundle extras = getIntent().getExtras();
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(extras);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, detailFragment)
                    .commit();
        }
    }
}
