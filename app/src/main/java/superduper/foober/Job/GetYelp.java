package superduper.foober.Job;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;
import superduper.foober.API.QueryParams;
import superduper.foober.API.YelpAPI;
import superduper.foober.Event.YelpEvent;

/**
 * Created by anhbui on 9/19/15.
 */
public class GetYelp extends Job {
    public GetYelp() {
        super(new Params(1));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        YelpAPI yelpApi = new YelpAPI("C7jpYv-BIy90FZNFVKb0sA", "Y4Q5KP0Ix4lDjAtLuZFTwoOBDzU",
                "gAipPmG2nAR80B1Rb1BKBdLe2hnWY1i0", "gklfMFQUqn0HKTZfpxPl_BpzWKc");
        QueryParams queryParams = QueryParams.builder()
                .build();
        EventBus.getDefault().post(new YelpEvent(yelpApi.queryAPI(yelpApi, queryParams)));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
