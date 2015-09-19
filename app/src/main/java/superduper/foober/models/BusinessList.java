package superduper.foober.models;

import java.util.List;

/**
 * Created by anhbui on 9/19/15.
 */
public class BusinessList {
    private List<BusinessModel> businesses;
    private RegionModel region;
    public List<BusinessModel> getBusinessList() {
        return businesses;
    }
    public RegionModel getRegionModel() {
        return region;
    }
    public double getLatitude() {
        return region.getCenterModel().getLatitude();
    }
    public double getLongitude() {
        return region.getCenterModel().getLongitude();
    }
}
