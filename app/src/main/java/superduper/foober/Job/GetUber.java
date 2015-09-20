package superduper.foober.Job;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;
import superduper.foober.API.QueryParams;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberEvent;

public class GetUber extends Job {
    private int limit = 1;
    private UberAPI uberApi;
    private String startLat;
    private String startLong;
    private String endLat;
    private String endLong;

    public GetUber(int limit, UberAPI uberApi, String startLat, String startLong, String endLat, String endLong) {
        super(new Params(1));
        Log.i("querying Uber", "sdf");

        this.uberApi = uberApi;
        this.limit = limit;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        QueryParams queryParams = QueryParams.builder()
                .setLimit(limit)
                .build();

        EventBus.getDefault().post(new UberEvent(uberApi.getPriceEstimate(startLat,startLong,endLat,endLong)));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
