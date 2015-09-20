package superduper.foober.Event;

import org.scribe.model.Token;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberAccessTokenEvent {
    public final Token accessToken;

    public UberAccessTokenEvent(Token accessToken) {
        this.accessToken = accessToken;
    }
}
