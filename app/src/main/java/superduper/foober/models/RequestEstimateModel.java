package superduper.foober.models;

/**
 * Created by anhbui on 9/20/15.
 */
public class RequestEstimateModel {
    private Price price;
    private TripModel trip;
    private int pickup_estimate;

    public Price getPrice() {
        return price;
    }

    public TripModel getTrip() {
        return trip;
    }

    public int getPickupEstimate() {
        return pickup_estimate;
    }
}
