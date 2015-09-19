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

import com.lukedeighton.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import superduper.foober.WheelView.MaterialColor;
import superduper.foober.WheelView.YelpBusinessAdapter;

public class WheelActivity extends Activity {

    private static final int ITEM_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        //create data for the adapter
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(ITEM_COUNT);
        for (int i = 0; i < ITEM_COUNT; i++) {
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
        final Runnable spinning = new Runnable() {
            int iterPosition = 0;
            int slowingDown = 100;
            int decceleration = 1;

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
        };

        handler.postDelayed(spinning, 500);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.equals(imageView)) {
                    handler.postDelayed(spinning, 500);
                }
            }
        };
        imageView.setOnClickListener(clickListener);

        int choice = wheelView.getSelectedPosition();
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
