package superduper.foober;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //If GPS is enabled, update the location. Else, show an alert dialog.
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocation();
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
}
