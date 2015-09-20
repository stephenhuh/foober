package superduper.foober.API.Uber;

import android.util.Log;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberAPIOAuth extends DefaultApi20 {
    private static final String AUTHORIZE_URL = "https://login.uber.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://login.uber.com/oauth/token";
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new AccessTokenExtractor() {

            @Override
            public Token extract(String response) {
                Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");

                Matcher matcher = Pattern.compile("\"access_token\":\"([^&\"]++)\"").matcher(response);
                if (matcher.find())
                {
                    String token = OAuthEncoder.decode(matcher.group(1));
                    return new Token(token, "", response);
                }
                else
                {
                    throw new OAuthException("Response body is incorrect. Can't extract a token from this: '" + response + "'", null);
                }
            }
        };
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(),
                    OAuthEncoder.encode(config.getCallback()),
                    OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL, config.getApiKey(),
                    OAuthEncoder.encode(config.getCallback()));
        }
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public OAuthService createService(OAuthConfig config) {
        return new UberOAuth2Service(this, config);
    }

    private class UberOAuth2Service extends OAuth20ServiceImpl {

        private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
        private static final String GRANT_TYPE = "grant_type";
        private DefaultApi20 api;
        public OAuthConfig config;

        public UberOAuth2Service(DefaultApi20 api, OAuthConfig config) {
            super(api, config);
            this.api = api;
            this.config = config;
        }

        @Override
        public Token getAccessToken(Token requestToken, Verifier verifier) {
            OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
            switch (api.getAccessTokenVerb()) {
                case POST:
                    Log.i("POST", "yay");
                    request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
                    request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
                    request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
                    request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
                    request.addBodyParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
                    break;
                case GET:
                    Log.i("GET", "yay");
                default:
                    Log.i("WTF", "yay");
                    request.addQuerystringParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
                    request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
                    request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
                    request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
                    if (config.hasScope())
                        request.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
            }
            Log.i("SEDNING RQ", request.getCompleteUrl());
            Log.i("BODY", request.getBodyParams().asFormUrlEncodedString());
            Log.i("RQ HEADERS", request.getHeaders().toString());
            Response response = request.send();
            Log.i("response", response.getBody());
            return api.getAccessTokenExtractor().extract(response.getBody());
        }
    }
}