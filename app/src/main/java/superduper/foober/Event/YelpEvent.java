package superduper.foober.Event;

import superduper.foober.models.BusinessList;

/**
 * Created by anhbui on 9/19/15.
 */
public class YelpEvent {
    public final BusinessList businessList;
    public YelpEvent(BusinessList businessList) {
        this.businessList = businessList;
    }
}
