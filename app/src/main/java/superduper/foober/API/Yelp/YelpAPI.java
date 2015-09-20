package superduper.foober.API.Yelp;

/**
 * Created by anhbui on 9/18/15.
 */
import android.util.Log;

import com.google.gson.Gson;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import superduper.foober.API.QueryParams;
import superduper.foober.models.BusinessList;

/**
 * Code sample for accessing the Yelp API V2.
 *
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 *
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 *
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "dinner";
    private static final String DEFAULT_LOCATION = "San Francisco, CA";
    private static final int SEARCH_LIMIT = 3;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(QueryParams queryParams) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        String term = queryParams.term;
        double longitude = queryParams.longitude;
        double latitude = queryParams.latitude;
        int limit = queryParams.limit;
        int radiusFilter = queryParams.radiusFilter;
        String ll = String.valueOf(latitude)+","+String.valueOf(longitude);
        request.addQuerystringParameter("radius_filter", String.valueOf(radiusFilter));
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", queryParams.location);
        request.addQuerystringParameter("limit", String.valueOf(limit));
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and sends a request to the Business API by business ID.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        System.out.println("Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    /**
     * Queries the Search API based on the command line arguments and takes the first result to query
     * the Business API.
     *
     * @param yelpApi <tt>YelpAPI</tt> service instance
     */
    public static BusinessList queryAPI(YelpAPI yelpApi, QueryParams queryParams) {
        String searchResponseJSON =
                yelpApi.searchForBusinessesByLocation(queryParams);

        Log.d("RAW JSON: ",searchResponseJSON);

        BusinessList response = null;
        Gson gson = new Gson();
        try {
            response = gson.fromJson(searchResponseJSON, BusinessList.class);
        } catch (Exception pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }

        return response;
    }
}
