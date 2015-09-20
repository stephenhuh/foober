package superduper.foober.Event;

import superduper.foober.models.HistoryList;
import superduper.foober.models.HistoryModel;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberEvent {
    public final HistoryList historyList;

    public UberEvent(HistoryList historyList) {
        this.historyList = historyList;
    }
}
