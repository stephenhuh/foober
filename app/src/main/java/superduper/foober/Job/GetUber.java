package superduper.foober.Job;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.Map;

import de.greenrobot.event.EventBus;
import superduper.foober.API.QueryParams;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberEvent;

public class GetUber<T> extends Job {
    private final Map<String, String> queryParams;
    private int limit = 1;
    private UberAPI<T> uberApi;

    public GetUber(UberAPI<T> uberApi, Map<String, String> queryParams) {
        super(new Params(1));
        Log.i("querying Uber", "sdf");

        this.uberApi = uberApi;
        this.queryParams = queryParams;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        EventBus.getDefault().post(new UberEvent(uberApi.queryApi(this.queryParams)));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
