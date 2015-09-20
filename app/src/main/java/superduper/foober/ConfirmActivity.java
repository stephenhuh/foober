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

import de.greenrobot.event.EventBus;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import superduper.foober.Event.YelpEvent;
import superduper.foober.Job.GetYelp;
import superduper.foober.models.BusinessList;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class ConfirmActivity extends Activity {
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        .setTextOverImage("Italian Beaches")
                        .useDrawableId(R.drawable.chipotle)
                        .setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large, null )
                        .build();


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
    
    public void onEventMainThread(YelpEvent yelpEvent) {
        BusinessList response = yelpEvent.businessList;
        System.out.println(response.getBusinessList().get(0).getAddress());
        System.out.println(response.getBusinessList().get(0).getName());
        System.out.println(response.getBusinessList().get(0).getPhoneNumber());
        System.out.println(response.getBusinessList().get(0).getLocationData());
        System.out.println(response.getBusinessList().get(0).getCity());
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
}
