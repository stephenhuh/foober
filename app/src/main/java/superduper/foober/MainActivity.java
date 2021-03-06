package superduper.foober;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;
import superduper.foober.API.Uber.MyConstants;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberAccessTokenEvent;
import superduper.foober.Event.UberEvent;
import superduper.foober.Event.YelpEvent;
import superduper.foober.Job.GetUber;
import superduper.foober.Job.GetUberAccessToken;
import superduper.foober.Job.GetYelp;
import superduper.foober.models.BusinessList;
import superduper.foober.models.BusinessModel;
import superduper.foober.models.HistoryModel;
import superduper.foober.models.ProductsList;
import superduper.foober.models.RequestEstimateModel;

public class MainActivity extends Activity implements LocationListener {
    private LocationManager mLocationManager;
    Gson gson = new Gson();
    MapView mMapView;
    GoogleMap mGoogleMap;
    Button mAddButton;
    Button mPickButton;
    EditText mToEditText;
    Geocoder geocoder;
    Random generator = new Random();
    NumberPicker mPickNumber;
    WebView webView;
    List<Marker> markers = new ArrayList<Marker>();
    ArrayList<BusinessModel> businessModelList = new ArrayList<>();
    EditText mFromEditText;
    TextView mWithinMiles;
    SharedPreferences prefs;
    final UberAPI<ProductsList> uberApi = new UberAPI<ProductsList>("request", "v1/products",
            ProductsList.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMapView = (MapView) findViewById(R.id.mapview);
        mAddButton = (Button) findViewById(R.id.add_button);
        mToEditText = (EditText) findViewById(R.id.to_edittext);
        mFromEditText = (EditText) findViewById(R.id.from_edittext);
        mFromEditText.setVisibility(View.GONE);
        mMapView.setVisibility(View.GONE);
        mAddButton.setVisibility(View.GONE);
        mToEditText.setVisibility(View.GONE);
        mPickButton = (Button) findViewById(R.id.random_pick_button);
        mPickButton.setVisibility(View.GONE);
        mPickNumber = (NumberPicker) findViewById(R.id.numberPicker);
        mPickNumber.setVisibility(View.GONE);
        mWithinMiles = (TextView) findViewById(R.id.miles_text);
        mWithinMiles.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.main_activity_web_view);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                Log.i("authorized!!!!!", authorizationUrl);
                if (authorizationUrl.startsWith(MyConstants.UBER_REDIRECT_URL)) {
                    Uri uri = Uri.parse(authorizationUrl);
                    //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                    //If not, that means the request may be a result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter("state");

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter("code");
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    //Generate URL for requesting Access Token
                    FooberApplication.getJobManager().addJobInBackground(new GetUberAccessToken(1,
                            uberApi, authorizationToken));
                } else {
                    //Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });


        //Get the authorization Url
        String authUrl = uberApi.getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        //Load the authorization URL into the webView
        webView.loadUrl(authUrl);


        mPickNumber = (NumberPicker) findViewById(R.id.numberPicker);
        mPickNumber.setMaxValue(25);
        mPickNumber.setMinValue(1);
        mPickNumber.setWrapSelectorWheel(true);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(Utils.CURRENT_LOCATION.getLatitude(),Utils.CURRENT_LOCATION.getLongitude(),1);
                    Address address = addresses.get(0);
                    FooberApplication.getJobManager().addJobInBackground(new GetYelp(
                            Utils.CURRENT_LOCATION.getLatitude(), //Lat
                            Utils.CURRENT_LOCATION.getLongitude(), //Long
                            mPickNumber.getValue()*1609,//radius = miles*meters/mile = meters
                            6,//limit
                            mToEditText.getText().toString(),
                            address.getAddressLine(0)+","+address.getLocality()+" "+address.getPostalCode()
                    ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WheelActivity.class);
                String json = gson.toJson(businessModelList);
                json = "{\"businesses\":" + json + "}";
                intent.putExtra("businesses_json", json);
                Log.d("Data", json);
                startActivity(intent);
            }
        });

        //If GPS is enabled, update the location. Else, show an alert dialog.
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocation();
            createMap(savedInstanceState);
        } else {
            createNoGpsAlert();
        }
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

//    public void onEventMainThread(UberEvent uberEvent) {
//        Object value = uberEvent.getValue();
//        Log.i("parsed uber", new Gson().toJson(value));
//    }

    public void onEventMainThread(UberAccessTokenEvent uberAccessTokenEvent) {
        uberApi.setAccessToken(uberAccessTokenEvent.accessToken);
        mMapView.setVisibility(View.VISIBLE);
        mAddButton.setVisibility(View.VISIBLE);
        mToEditText.setVisibility(View.VISIBLE);
        mPickButton.setVisibility(View.VISIBLE);
        mPickNumber.setVisibility(View.VISIBLE);
        mFromEditText.setVisibility(View.VISIBLE);
        mWithinMiles.setVisibility(View.VISIBLE);

        webView.setVisibility(View.GONE);

//        Map<String, String> queryParams = new HashMap<String, String>(200);
//        queryParams.put("latitude", String.valueOf(Utils.CURRENT_LOCATION.getLatitude()));
//        queryParams.put("longitude", String.valueOf(Utils.CURRENT_LOCATION.getLongitude()));
//         TEST QUERIISE
//        FooberApplication.getJobManager().addJobInBackground((new GetUber(uberApi, queryParams)));
    }
    
    public void onEventMainThread(YelpEvent yelpEvent) {
        Toast.makeText(this, mToEditText.getText().toString() + " Added!", Toast.LENGTH_SHORT).show();
        mToEditText.setText("");
        hideKeyboard();
        BusinessList response = yelpEvent.businessList;
        List<BusinessModel> businessModels = response.getBusinessList();
        int color = generator.nextInt(10)+1;
        for(int x=0; x<response.getBusinessList().size(); x++) {
            BusinessModel business = businessModels.get(x);
            businessModelList.add(business);
            String address = business.getAddress()+", "+business.getCity()+" "+business.getLocationData().getPostalCode();
            Log.d("LOCATION: ",address);
            LatLng position = convertAddress(address);
            if(position != null) {
                markers.add(mGoogleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(business.getName())
                        .snippet(business.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(Utils.COLORS[color]))));
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public LatLng convertAddress(String address) {
        if (address != null && !address.isEmpty()) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();
                    return new LatLng(lat,lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
        return null;
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

    /**
     * Creates and alert dialog which displays to the user when Location services are not enabled.
     */
    private void createNoGpsAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("GPS Disabled")
                .setMessage("GPS is disabled on the device. Enable it?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        AlertDialog mNoGpsAlertDialog = builder.create();
        mNoGpsAlertDialog.show();
    }

    /**
     * Sets the current location using GPS
     */
    private void setLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(Utils.CURRENT_LOCATION == null) {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation != null) {
                    Utils.CURRENT_LOCATION = lastLocation;
                } else {
                    Utils.CURRENT_LOCATION = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
        }
    }

    private void createMap(Bundle savedInstancestate) {
        mMapView.onCreate(savedInstancestate);
        mGoogleMap = mMapView.getMap();
        MapsInitializer.initialize(this);
        if(Utils.CURRENT_LOCATION != null) {
            LatLng currLocation = new LatLng(Utils.CURRENT_LOCATION.getLatitude(), Utils.CURRENT_LOCATION.getLongitude());
            markers.add(mGoogleMap.addMarker(new MarkerOptions().position(currLocation)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currLocation,14);
            mGoogleMap.animateCamera(cameraUpdate);
        }

    }

    @Override
    public void onResume() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Utils.TEN_MINUTES, 200, this);
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(Utils.isBetterLocation(location,Utils.CURRENT_LOCATION)) {
            Utils.CURRENT_LOCATION = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
