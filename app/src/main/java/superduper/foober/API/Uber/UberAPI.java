package superduper.foober.API.Uber;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import superduper.foober.API.QueryParams;
import superduper.foober.models.HistoryList;
import superduper.foober.models.PriceList;
import superduper.foober.models.TimeList;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberAPI<T> {
    private final OAuthService service;
    private final String USER_HISTORY_ENDPOINT = "v1.2/history";
    private final String PRICE_ESTIMATE_ENDPOINT = "v1/estimates/price";
    private Class<T> dataModel;
    private final String endpoint;
    private static Token accessToken;

    public UberAPI(String scope, String endpoint, Class<T> type) {
        ServiceBuilder builder =
                new ServiceBuilder().provider(UberAPIOAuth.class)
                        .apiKey(MyConstants.UBER_CLIENT_ID)
                        .apiSecret(MyConstants.UBER_CLIENT_SECRET)
                        .callback(MyConstants.UBER_REDIRECT_URL);
        if (!scope.isEmpty()) {
            builder.scope(scope);
        }
        this.service = builder.build();
        this.dataModel = type;
        this.endpoint = endpoint;
    }

    public Class<T> getDataModel() {
        return dataModel;
    }

    public void setAccessToken(Token token) {
        this.accessToken = token;
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(new Token(MyConstants.UBER_CLIENT_ID, MyConstants.UBER_CLIENT_SECRET));
    }

    private T sendAndGetResponse(OAuthRequest request) {
        service.signRequest(accessToken, request);
        String searchResponseJSON = request.send().getBody();
        Log.d("RAW JSON: ", searchResponseJSON);
        T response = null;
        Gson gson = new Gson();
        try {
            Log.d("type", getDataModel().toString());
            response = gson.fromJson(searchResponseJSON, dataModel);
        } catch (Exception pe) {
            Log.d("json error", pe.getMessage());
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        return response;
    }

    public T queryApi(Map<String, String> queryParams) {
        String url = MyConstants.UBER_API_URL + this.endpoint;
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        Iterator<String> keys = queryParams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            request.addQuerystringParameter(key, queryParams.get(key));
        }
        return sendAndGetResponse(request);
    }

    public PriceList getPriceEstimate(String startLat, String startLong, String endLat, String endLong) {
        String url = MyConstants.UBER_API_URL + USER_HISTORY_ENDPOINT;
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        request.addQuerystringParameter("start_latitude",startLat);
        request.addQuerystringParameter("start_longitude",startLong);
        request.addQuerystringParameter("end_latitude",endLat);
        request.addQuerystringParameter("end_longitude",endLong);
        service.signRequest(accessToken, request);
        String searchResponseJSON = request.send().getBody();

        Log.d("RAW JSON: ", searchResponseJSON);
        PriceList response = null;
        Gson gson = new Gson();
        try {
            response = gson.fromJson(searchResponseJSON, PriceList.class);
        } catch (Exception pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        return response;
    }
    public TimeList getTimeEstimate(String startLat, String startLong) {
        String url = MyConstants.UBER_API_URL + USER_HISTORY_ENDPOINT;
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        request.addQuerystringParameter("start_latitude",startLat);
        request.addQuerystringParameter("start_longitude",startLong);
        service.signRequest(accessToken, request);
        String searchResponseJSON = request.send().getBody();

        Log.d("RAW JSON: ", searchResponseJSON);
        TimeList response = null;
        Gson gson = new Gson();
        try {
            response = gson.fromJson(searchResponseJSON, TimeList.class);
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
