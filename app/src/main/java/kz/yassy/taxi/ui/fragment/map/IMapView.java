package kz.yassy.taxi.ui.fragment.map;

import com.mapbox.mapboxsdk.geometry.LatLng;

public interface IMapView {
    void onMapMoved(boolean moved);
    void onActionUp(LatLng point);
    void onActionDown();
    void onMapReady();
    int getMapPadding();
}
