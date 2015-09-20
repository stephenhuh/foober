package superduper.foober.models;

public class Price {
    private String product_id;

    private String currency_code;

    private String display_name;

    private String estimate;

    private Integer low_estimate;

    private Integer high_estimate;

    private Integer surge_multiplier;

    private Integer duration;

    private String distance;

    public String getProductId() {
        return product_id;
    }

    public String getCurrencyCode() {
        return currency_code;
    }
    public String getDisplayName() {
        return display_name;
    }

    public String getEstimate() {
        return estimate;
    }

    public Integer getLowEstimate() {
        return low_estimate;
    }

    public Integer getHighEstimate() {
        return high_estimate;
    }
    public Integer getSurgeMultiplier() {
        return surge_multiplier;
    }

    public Integer getDuration() {
        return duration;
    }
    public String getDistance() {
        return distance;
    }
}
