package superduper.foober.models;

/**
 * Created by anhbui on 9/20/15.
 */
public class TripModel {
    private double distance_estimate;
    private int duration_estimate;

    public int getEstimate() {
        return duration_estimate;
    }

    public double getDistanceEstimate() {
        return distance_estimate;
    }
}
