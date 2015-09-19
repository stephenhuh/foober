package superduper.foober.models;

public class RegionModel {

    private CenterModel center;

    public CenterModel getCenterModel() {
        return center;
    }

    public class CenterModel{
        private double latitude;

        private double longitude;

        public double getLatitude() {
            return latitude;
        }
        public double getLongitude() {
            return longitude;
        }

    }
}
