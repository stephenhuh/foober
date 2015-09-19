package superduper.foober;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import superduper.foober.Event.YelpEvent;
import superduper.foober.Job.GetYelp;
import superduper.foober.models.BusinessList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO Remove test data in GetYelp when needed
        FooberApplication.getJobManager().addJobInBackground(new GetYelp(
                42.449650999999996, //Lat
                -76.4812924, //Long
                10000,//radius
                1,//limit
                "dinner"));
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
}
