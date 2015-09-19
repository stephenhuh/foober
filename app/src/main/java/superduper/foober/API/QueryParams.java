package superduper.foober.API;

/**
 * Created by anhbui on 9/19/15.
 */
public class QueryParams {
    public static class Builder {
        private int limit = 3;
        private int radiusFilter = 10;
        private double latitude = 42.449650999999996, longitude = -76.4812924;
        private String term = "dinner";
        public QueryParams build() {
            return new QueryParams(this);
        }
        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }
        public Builder setTerm(String term) {
            this.term = term;
            return this;
        }

        public Builder setRadiusFilter(int radiusFilter) {
            this.radiusFilter = radiusFilter;
            return this;
        }
        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }
        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
    }
    public static Builder builder() {
        return new Builder();
    }
    private QueryParams(Builder builder) {
        this.term = builder.term;
        this.limit = builder.limit;
        this.radiusFilter = builder.radiusFilter;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public final String term;
    public final int limit;
    public final int radiusFilter;
    public final double latitude, longitude;
}