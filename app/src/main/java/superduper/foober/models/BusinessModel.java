package superduper.foober.models;

public class BusinessModel {

    private boolean is_closed;

    private String image_url;

    private LocationModel location;

    private String name;

    private String phone;

    private String rating_img_url_small;

    private Integer review_count;

    public boolean isClosed() {
        return is_closed;
    }

    public String getImageUrl() {
        return image_url;
    }

    public LocationModel getLocationData() {
        return location;
    }

    public String getAddress() {
        return location.getAddress();
    }

    public String getCity() {
        return location.getCity();
    }

    public String getCountryCode() {
        return location.getCountryCode();
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getRatingImage() {
        return rating_img_url_small;
    }

    public Integer getReviewCount() {
        return review_count;
    }
}
