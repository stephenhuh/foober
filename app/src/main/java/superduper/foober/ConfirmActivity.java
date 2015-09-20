package superduper.foober;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.path.android.jobqueue.JobManager;
import com.squareup.okhttp.internal.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberEvent;
import superduper.foober.Job.GetUber;
import superduper.foober.models.Price;
import superduper.foober.models.PriceList;
import superduper.foober.models.ProductsList;
import superduper.foober.models.RequestEstimateModel;

public class ConfirmActivity extends Activity {
    private TextView mDescriptionTextView;
    private ImageView mImageView;
    SharedPreferences preferences;
    double latitude;
    double longitude;
    private TextView mPriceEstimateTextView;
    Button confirm;
    double current_lat;
    double current_long;
    UberAPI<ProductsList> uberProducts = new UberAPI<>("request", "v1/products", ProductsList.class);
    UberAPI<RequestEstimateModel> uberRequest = new UberAPI<>("request", "v1/requests/estimate",
            RequestEstimateModel.class);

    private JobManager jobManager;

    String product_id;
    private RequestEstimateModel request_estimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        current_lat = Utils.CURRENT_LOCATION.getLatitude();
        current_long = Utils.CURRENT_LOCATION.getLongitude();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = Double.valueOf(preferences.getString("lat",null));
        longitude = Double.valueOf(preferences.getString("long",null));
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("snippet_image_url");
        confirm = (Button) findViewById(R.id.confirm_button);
        mImageView = (ImageView) findViewById(R.id.yelp_imageview);
        mPriceEstimateTextView = (TextView) findViewById(R.id.price_estimate_textview);
        mDescriptionTextView = (TextView) findViewById(R.id.description_textview);
        mDescriptionTextView.setText(description);
        jobManager = FooberApplication.getJobManager();

        Map<String, String> queryParams = new HashMap<String, String>(200);
        queryParams.put("latitude", String.valueOf(current_lat));
        queryParams.put("longitude", String.valueOf(current_long));
        jobManager.addJobInBackground(new GetUber<ProductsList>(uberProducts, queryParams));

        Picasso.with(this)
                .load(imageUrl)
                .into(mImageView);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(UberEvent event) {
        Log.i("parsed uber", new Gson().toJson(event.getValue()));
        if (event.getValue() instanceof ProductsList) {
            product_id = ((ProductsList) event.getValue()).getProductsList().get(0).getProductId();
            Log.i("lat, long", String.valueOf(latitude) + "\n" + String.valueOf(current_lat));
            Map<String, String> queryParams = new HashMap<String, String>(200);
            queryParams.put("product_id", String.valueOf(product_id));
            queryParams.put("end_latitude", String.valueOf(latitude));
            queryParams.put("end_longitude", String.valueOf(longitude));
            queryParams.put("start_latitude", String.valueOf(current_lat));
            queryParams.put("start_longitude", String.valueOf(current_long));
            jobManager.addJobInBackground(new GetUber<RequestEstimateModel>(uberRequest, queryParams));
        }
        else {
            request_estimate = ((RequestEstimateModel) event.getValue());
            String output = "Do you want to call Uber?\nPrice Estimates:"+ String.valueOf(request_estimate.getPrice().getHighEstimate())+" dollars\n"
                    + "Pickup Estimate:" + String.valueOf(request_estimate.getPickupEstimate())+" minutes\n"
                    + "Duration Estimate: " + String.valueOf(request_estimate.getTrip().getEstimate()) + " seconds\n";
            mPriceEstimateTextView.setText(output);
        }

    }
}
