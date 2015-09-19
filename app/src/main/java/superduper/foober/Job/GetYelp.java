package superduper.foober.Job;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;
import superduper.foober.API.Yelp.QueryParams;
import superduper.foober.API.Yelp.YelpAPI;
import superduper.foober.Event.YelpEvent;

/**
 * Created by anhbui on 9/19/15.
 */
public class GetYelp extends Job {
    private double latitude;
    private double longitude;
    private int radiusFilter;
    private int limit;
    private String term;

    public GetYelp(double latitude, double longitude, int radiusFilter, int limit, String term) {
        super(new Params(1));
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusFilter = radiusFilter;
        this.limit = limit;
        this.term = term;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        YelpAPI yelpApi = new YelpAPI("C7jpYv-BIy90FZNFVKb0sA", "Y4Q5KP0Ix4lDjAtLuZFTwoOBDzU",
                "gAipPmG2nAR80B1Rb1BKBdLe2hnWY1i0", "gklfMFQUqn0HKTZfpxPl_BpzWKc");
        QueryParams queryParams = QueryParams.builder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setRadiusFilter(radiusFilter)
                .setLimit(limit)
                .setTerm(term)
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
