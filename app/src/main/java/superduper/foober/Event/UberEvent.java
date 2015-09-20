package superduper.foober.Event;

import superduper.foober.models.PriceList;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberEvent {
    public final PriceList priceList;

    public UberEvent(PriceList historyList) {
        this.priceList = historyList;
    }
}
