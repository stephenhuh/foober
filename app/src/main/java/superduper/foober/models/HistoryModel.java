package superduper.foober.models;

/**
 * Created by anhbui on 9/19/15.
 */
public class HistoryModel {
    private String status;
    private double request_time;
    private double end_time;
    private double start_time;
    private double distance;
    private String request_id;
    private String product_id;
    private StartCityModel start_city;

    public String getStatus() { return status; }
    public double getRequestTime() { return request_time; }
    public double getEndTime() { return end_time; }
    public double getStartTime() { return start_time; }
    public double getDistance() { return distance; }
    public String getRequestId() { return request_id; }
    public String getProductId() { return product_id; }
    public StartCityModel getStartCity() { return start_city; }
    public double getStartCityLatitude() { return start_city.getLatitude(); }
    public double getStartCityLongitude() { return start_city.getLongitude(); }
    public String getStartCityDisplayName() { return start_city.getDisplayName(); }


}