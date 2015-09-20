package superduper.foober.Event;

import superduper.foober.models.PriceList;

/**
 * Created by anhbui on 9/19/15.
 */
public class UberEvent<T> {
    private final T value;

    public UberEvent(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
