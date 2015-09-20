package superduper.foober.Job;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.scribe.oauth.OAuthService;

import de.greenrobot.event.EventBus;
import superduper.foober.API.QueryParams;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.API.Yelp.YelpAPI;
import superduper.foober.Event.UberEvent;
import superduper.foober.Event.YelpEvent;

/**
 * Created by anhbui on 9/19/15.
 */
public class GetUber extends Job {
    private int limit = 1;
    private UberAPI uberApi;

    public GetUber(int limit, UberAPI uberApi) {
        super(new Params(1));
        Log.i("querying Uber", "sdf");

        this.uberApi = uberApi;
        this.limit = limit;
    }

    @Override
    public void onAdded() {        }

    @Override
    public void onRun() throws Throwable {
        QueryParams queryParams = QueryParams.builder()
                .setLimit(limit)
                .build();

        EventBus.getDefault().post(new UberEvent(uberApi.queryApi("v1.2/history", queryParams)));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
