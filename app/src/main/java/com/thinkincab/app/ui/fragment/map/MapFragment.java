package com.thinkincab.app.ui.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.AndroidGesturesManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.thinkincab.app.R;
import com.thinkincab.app.data.network.model.SearchRoute;
import com.thinkincab.app.ui.utils.DisplayUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapFragment extends Fragment implements
        MapboxMap.OnMapClickListener,
        MapboxMap.OnScaleListener,
        MapboxMap.OnMapLongClickListener,
        MapboxMap.OnMoveListener,
        MapboxMap.OnFlingListener,
        MapboxMap.OnCameraIdleListener,
        MapboxMap.OnCameraMoveCanceledListener,
        MapboxMap.OnCameraMoveListener,
        MapboxMap.OnCameraMoveStartedListener {

    private final static String ROUTE_LAYER_ID = "route-layer-id";
    private final static String ROUTE_SOURCE_ID = "route-source-id";

    @BindView(R.id.map_box)
    MapView mapView;

    protected MapboxMap mapBoxMap;
    protected MarkerViewManager markerViewManager;
    protected AndroidGesturesManager androidGesturesManager;
    protected LocationComponent locationComponent;

    protected LineLayer routeLayer;
    protected Style style;

    protected boolean isScaling = false;
    protected boolean connected = true;

    private LatLng center;

    private IMapView listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IMapView) {
            listener = (IMapView) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapBoxMap -> {
            this.mapBoxMap = mapBoxMap;
            this.markerViewManager = new MarkerViewManager(mapView, mapBoxMap);
            mapBoxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                mapBoxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(43.2973300, 68.2517500))
                        .padding(
                                0.0,
                                0.0,
                                0.0,
                                DisplayUtils.dpToPx(0) // current padding
                        )
                        .zoom(15)
                        .build()));
                mapBoxMap.addOnMapClickListener(MapFragment.this);
                mapBoxMap.addOnMapLongClickListener(MapFragment.this);
                mapBoxMap.addOnScaleListener(MapFragment.this);
                mapBoxMap.addOnMoveListener(MapFragment.this);
                mapBoxMap.addOnFlingListener(MapFragment.this);
                mapBoxMap.addOnCameraIdleListener(MapFragment.this);
                mapBoxMap.addOnCameraMoveCancelListener(MapFragment.this);
                mapBoxMap.addOnCameraMoveListener(MapFragment.this);
                mapBoxMap.addOnCameraMoveStartedListener(MapFragment.this);
                mapBoxMap.getUiSettings().setLogoGravity(Gravity.BOTTOM | Gravity.START);
                mapBoxMap.getUiSettings().setAttributionTintColor(Color.LTGRAY);
                mapBoxMap.getUiSettings().setAttributionGravity(Gravity.BOTTOM | Gravity.END);
                mapBoxMap.getUiSettings().setLogoMargins(DisplayUtils.dpToPx(10), 0, 0, 0);
                mapBoxMap.getUiSettings().setAttributionMargins(0, 0, DisplayUtils.dpToPx(10), 0);
                androidGesturesManager = mapBoxMap.getGesturesManager();
                if (showLocation()) {
                    if (PermissionsManager.areLocationPermissionsGranted(view.getContext())) {
                        locationComponent = mapBoxMap.getLocationComponent();
                        locationComponent.activateLocationComponent(
                                LocationComponentActivationOptions.builder(view.getContext(), style).locationComponentOptions(
                                        LocationComponentOptions.builder(view.getContext())
                                                .accuracyAlpha(0.1f)
                                                .accuracyColor(Color.parseColor("#3589f3"))
                                                .build()
                                ).build()
                        );
                        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationComponent.setLocationComponentEnabled(true);
                        locationComponent.setCameraMode(CameraMode.NONE);
                        locationComponent.setRenderMode(RenderMode.COMPASS);
                    }
                }
                mapInit(style);
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapBoxMap.removeOnMapLongClickListener(this);
        mapBoxMap.removeOnMapClickListener(this);
        mapBoxMap.removeOnScaleListener(this);
        mapBoxMap.removeOnMoveListener(this);
        mapBoxMap.removeOnFlingListener(this);
        mapBoxMap.removeOnCameraIdleListener(this);
        mapBoxMap.removeOnCameraMoveCancelListener(this);
        mapBoxMap.removeOnCameraMoveListener(this);
        mapBoxMap.removeOnCameraMoveStartedListener(this);
        mapView.onDestroy();
        markerViewManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void handleZoom(double zoom) {
    }

    protected boolean showLocation() {
        return true;
    }

    protected void mapInit(Style style) {
        this.style = style;
        routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(0f),
                lineColor(ContextCompat.getColor(requireContext(), R.color.app_orange))
        );
        style.addLayer(routeLayer);
        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>())));
        if (listener != null) {
            listener.onMapReady();
        }
    }

    public void animateCamera(@NonNull CameraUpdate update) {
        if (mapBoxMap == null) {
            return;
        }
        mapBoxMap.animateCamera(update);
    }

    public void enableMyLocation(boolean enable) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationComponent.setLocationComponentEnabled(enable);
    }

    public void deleteRoute() {
        routeLayer.setProperties(
                lineWidth(0f)
        );
        GeoJsonSource s = ((GeoJsonSource) style.getSourceAs(ROUTE_SOURCE_ID));
        if (s != null) {
            s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
        }
    }

    public void showRoute(SearchRoute o) {
        routeLayer.setProperties(
                lineWidth(3f)
        );
        List<Point> points = new ArrayList<>();
        try {
            for (List<Double> one : o.getPaths().get(0).getPoints().getCoordinates()) {
                points.add(Point.fromLngLat(one.get(0), one.get(1)));
            }
        } catch (Exception e) {
            Log.d("gdsdg", "onEr" + e.toString());
        }
        Feature directionsRouteFeature = Feature.fromGeometry(
                LineString.fromLngLats(points, null)
        );
        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
        GeoJsonSource s = ((GeoJsonSource) style.getSourceAs(ROUTE_SOURCE_ID));
        if (points.size() > 2) {
            if (s != null) {
                s.setGeoJson(collection);
            }
        } else {
            if (s != null) {
                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
            }
        }
    }

    // map

    @Override
    public void onCameraIdle() {
        if (mapBoxMap == null) {
            return;
        }
        LatLng point = mapBoxMap.getCameraPosition().target;
        if (point != null) {
            if (!isScaling) {
                if (listener != null) {
                    listener.onActionUp(point);
                }
            }
        }
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == REASON_API_GESTURE) {
            if (listener != null) {
                listener.onMapMoved(true);
                listener.onActionDown();
            }
        }
    }

    @Override
    public void onFling() {

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (mapBoxMap == null) {
            return false;
        }
        if (listener != null) {
            listener.onMapMoved(true);
            listener.onActionUp(point);
            CameraPosition position = new CameraPosition.Builder()
                    .target(point)
                    .padding(
                            0.0,
                            0.0,
                            0.0,
                            listener.getMapPadding()
                    )
                    .build();
            mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        return true;
    }

    @Override
    public boolean onMapLongClick(@NonNull @NotNull LatLng point) {
        return true;
    }

    @Override
    public void onMoveBegin(@NonNull @NotNull MoveGestureDetector detector) {

    }

    @Override
    public void onMove(@NonNull @NotNull MoveGestureDetector detector) {

    }

    @Override
    public void onMoveEnd(@NonNull @NotNull MoveGestureDetector detector) {

    }

    @Override
    public void onScaleBegin(@NonNull @NotNull StandardScaleGestureDetector detector) {
        if (mapBoxMap == null) {
            return;
        }
        isScaling = true;
        if (listener != null) {
            listener.onMapMoved(true);
        }
        MoveGestureDetector gestureDetector = androidGesturesManager.getMoveGestureDetector();
        gestureDetector.setMoveThreshold(100000f);
        gestureDetector.interrupt();
        center = mapBoxMap.getCameraPosition().target;
    }

    @Override
    public void onScale(@NonNull @NotNull StandardScaleGestureDetector detector) {
        if (mapBoxMap == null) {
            return;
        }
        if (center != null && listener != null) {
            double zoom = mapBoxMap.getCameraPosition().zoom;
            handleZoom(zoom);
            mapBoxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(center)
                    .padding(
                            0.0,
                            0.0,
                            0.0,
                            listener.getMapPadding()
                    )
                    .zoom(!detector.isScalingOut() ? zoom + (detector.getScaleFactor() - 1) : zoom - (1 - detector.getScaleFactor()))
                    .build());
        }
    }

    @Override
    public void onScaleEnd(@NonNull @NotNull StandardScaleGestureDetector detector) {
        isScaling = false;
        MoveGestureDetector gestureDetector = androidGesturesManager.getMoveGestureDetector();
        gestureDetector.setMoveThreshold(0f);
    }
}
