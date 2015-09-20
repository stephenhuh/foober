package superduper.foober;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lukedeighton.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import superduper.foober.WheelView.MaterialColor;
import superduper.foober.WheelView.YelpBusinessAdapter;
import superduper.foober.models.BusinessList;

public class WheelActivity extends Activity {

    private int itemCount = 10;
    private static Random randomGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        // get yelp businesses chosen
        String json = getIntent().getExtras().getString("businesses_json");
        BusinessList businesses = new Gson().fromJson(json, BusinessList.class);
        itemCount = businesses.getBusinessList().size();

        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        wheelView.setWheelItemCount(itemCount);
        //create data for the adapter
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            Map.Entry<String, Integer> entry = MaterialColor.random(this, "\\D*_500$");
            entries.add(entry);
        }

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
        wheelView.setAdapter(new YelpBusinessAdapter(entries));

        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, Drawable itemDrawable, int position) {
                //get the item at this position
                Map.Entry<String, Integer> selectedEntry = ((YelpBusinessAdapter) parent.getAdapter()).getItem(position);
                parent.setSelectionColor(getContrastColor(selectedEntry));
            }
        });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                String msg = String.valueOf(position) + " " + isSelected;
                Toast.makeText(WheelActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        //initialise the selection drawable with the first contrast color
        wheelView.setSelectionColor(getContrastColor(entries.get(0)));

        final Handler handler = new Handler();

        handler.postDelayed(new Spinning(wheelView, handler), 500);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.equals(imageView)) {
                    handler.postDelayed(new Spinning(wheelView, handler), 0);
                }
            }
        };
        imageView.setOnClickListener(clickListener);

        int choice = wheelView.getSelectedPosition();


    }

    private static class Spinning implements Runnable {
        private Handler handler;
        private WheelView wheelView;
        int iterPosition = randomGenerator.nextInt(10);
        int slowingDown = 100;
        int decceleration = 1;

        public Spinning(WheelView wheelView, Handler handler) {
            this.wheelView = wheelView;
            this.handler = handler;
        }

        @Override
        public void run() {
            wheelView.setAngle(-wheelView.getAngleForPosition(iterPosition));
            iterPosition = ++iterPosition % 10;
            if (slowingDown < 3400) {
                handler.postDelayed(this, slowingDown/4);
                slowingDown += decceleration*decceleration;
                if (slowingDown > 200) decceleration++;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wheel, menu);
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

    private int getContrastColor(Map.Entry<String, Integer> entry) {
        String colorName = MaterialColor.getColorName(entry);
        return MaterialColor.getContrastColor(colorName);
    }
}
