package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchRoute {
    @SerializedName("paths")
    @Expose
    private List<PathContainer> paths;

    public List<PathContainer> getPaths() {
        return paths;
    }

    public static class PathContainer {

        @SerializedName("points")
        @Expose
        private Line points;

        @SerializedName("snapped_waypoints")
        @Expose
        private Line snappedWaypoints;

        @SerializedName("bbox")
        @Expose
        private List<Double> box;

        public Line getPoints() {
            return points;
        }

        public Line getSnappedWaypoints() {
            return snappedWaypoints;
        }

        public List<Double> getBox() {
            return box;
        }
    }

    public static class Line {

        @SerializedName("coordinates")
        @Expose
        private List<List<Double>> coordinates;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }
    }
}
