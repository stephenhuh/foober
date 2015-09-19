package superduper.foober.models;

import java.util.List;

public class LocationModel {
    private List<String> address;

    private String city;

    private String country_code;

    public String getAddress() {
        return address.get(0);
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return country_code;
    }
}
