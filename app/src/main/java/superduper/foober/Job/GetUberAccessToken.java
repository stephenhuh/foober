package superduper.foober.Job;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import de.greenrobot.event.EventBus;
import superduper.foober.API.Uber.UberAPI;
import superduper.foober.Event.UberAccessTokenEvent;

/**
 * Created by anhbui on 9/19/15.
 */
public class GetUberAccessToken extends Job {
    private final String authorizationToken;
    private int limit = 1;
    private UberAPI uberApi;

    public GetUberAccessToken(int limit, UberAPI uberApi, String authorizationToken) {
        super(new Params(1));
        this.authorizationToken = authorizationToken;
        this.uberApi = uberApi;
        this.limit = limit;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        Log.i("Getting", "Access Token");
        Token accessToken = uberApi.getAccessToken(new Verifier(authorizationToken));
        EventBus.getDefault().post(new UberAccessTokenEvent(accessToken));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}

