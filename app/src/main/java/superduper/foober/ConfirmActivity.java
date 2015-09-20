package superduper.foober;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberEvent;
import superduper.foober.Job.GetUber;
import superduper.foober.models.Price;
import superduper.foober.models.PriceList;

public class ConfirmActivity extends Activity {
    private TextView mDescriptionTextView;
    private ImageView mImageView;
    SharedPreferences preferences;
    double latitude;
    double longitude;
    private TextView mPriceEstimateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = Double.valueOf(preferences.getString("lat",null));
        longitude = Double.valueOf(preferences.getString("long",null));
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("snippet_image_url");

        mImageView = (ImageView) findViewById(R.id.yelp_imageview);
        mPriceEstimateTextView = (TextView) findViewById(R.id.price_estimate_textview);
        mDescriptionTextView = (TextView) findViewById(R.id.description_textview);
        mDescriptionTextView.setText(description);

        Picasso.with(this)
                .load(imageUrl)
                .into(mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(UberEvent event) {
//        PriceList priceList = event.priceList;
//        String text="";
//        for(Price item:priceList.getPrices()) {
//            text = text + item.getDisplayName() + ":" +item.getEstimate() +"\n";
//        }
    }
}
