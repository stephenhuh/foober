package superduper.foober.Event;

import superduper.foober.models.BusinessModel;

/**
 * Created by anhbui on 9/19/15.
 */
public class YelpEvent {
    public final BusinessModel business;
    public YelpEvent(BusinessModel business) {
        this.business = business;
    }
}
