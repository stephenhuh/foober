package superduper.foober.API.Uber;

import android.util.Log;

import com.google.gson.Gson;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import superduper.foober.API.QueryParams;
import superduper.foober.models.HistoryList;
import superduper.foober.models.HistoryModel;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberAPI {
    private final OAuthService service;
    private final String userHistory = "/v1.2/history";
    private Token accessToken;

    public UberAPI() {
        this.service =
                new ServiceBuilder().provider(UberAPIOAuth.class)
                        .apiKey(MyConstants.UBER_CLIENT_ID)
                        .apiSecret(MyConstants.UBER_CLIENT_SECRET)
                        .callback(MyConstants.UBER_REDIRECT_URL)
                        .scope("history")
                        .build();
    }

    public void setAccessToken(Token token) {
        this.accessToken = token;
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(new Token(MyConstants.UBER_CLIENT_ID, MyConstants.UBER_CLIENT_SECRET));
    }

    public HistoryList queryApi(String endpoint, QueryParams queryParams) {
        String url = MyConstants.UBER_API_URL + endpoint;
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        String searchResponseJSON = request.send().getBody();

        Log.d("RAW JSON: ", searchResponseJSON);
        HistoryList response = null;
        Gson gson = new Gson();
        try {
            response = gson.fromJson(searchResponseJSON, HistoryList.class);
        } catch (Exception pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        return response;
    }

    public Token getAccessToken(Verifier verifier) {
        return service.getAccessToken(null, verifier);
    }
}
