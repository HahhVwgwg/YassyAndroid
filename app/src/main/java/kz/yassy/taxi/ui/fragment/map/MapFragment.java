package kz.yassy.taxi.ui.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.AndroidGesturesManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.R;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.ui.utils.DisplayUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;

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
    private final static String POINT_1_LAYER_ID = "point-1-layer-id";
    private final static String POINT_1_SOURCE_ID = "point-1-source-id";
    private final static String POINT_2_LAYER_ID = "point-2-layer-id";
    private final static String POINT_2_SOURCE_ID = "point-2-source-id";

    @BindView(R.id.map_box)
    MapView mapView;

    protected MapboxMap mapBoxMap;
    protected MarkerViewManager markerViewManager;
    protected AndroidGesturesManager androidGesturesManager;
    protected LocationComponent locationComponent;

    protected LineLayer routeLayer;
    protected Style style;
    protected SymbolLayer pointLayer1;
    protected GeoJsonSource pointSource1;
    protected SymbolLayer pointLayer2;
    protected GeoJsonSource pointSource2;
    protected MarkerView timeMarker;

    protected boolean isScaling = false;
    protected boolean connected = true;

    private LatLng center;
    private IMapView listener;

    private TextView time;
    private View bulb;
    List<MarkerView> taxi = new ArrayList<>();
    private int markerSize = 60;
    private double coefficient = 3.75;

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

    private ArrayList<LatLng> taxiLocations = new ArrayList<>();

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

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapBoxMap -> {
            this.mapBoxMap = mapBoxMap;
            this.markerViewManager = new MarkerViewManager(mapView, mapBoxMap);
            mapBoxMap.setStyle("mapbox://styles/yassy-taxi/ckmdta9je8c5q18rwrhi7co4k", style -> {
                mapBoxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(43.2973300, 68.2517500))
                        .padding(
                                0.0,
                                0.0,
                                0.0,
                                listener.getMapPadding()
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

    private View generateTimeBulb() {
        View customView = LayoutInflater.from(requireContext()).inflate(
                R.layout.time_bulb,
                null
        );
        customView.setLayoutParams(new FrameLayout.LayoutParams(
                DisplayUtils.dpToPx(82),
                DisplayUtils.dpToPx(50)
        ));
        time = customView.findViewById(R.id.time_value);
        return customView;
    }

    protected boolean showLocation() {
        return true;
    }

    protected void mapInit(Style style) {
        this.style = style;
        bulb = generateTimeBulb();
        bulb.setAlpha(0f);
        Drawable start = ContextCompat.getDrawable(requireContext(), R.drawable.ic_start_pin);
        if (start != null) {
            style.addImage(("marker-icon-point-1"), start);
        }
        Drawable end = ContextCompat.getDrawable(requireContext(), R.drawable.ic_finish_pin);
        if (end != null) {
            style.addImage(("marker-icon-point-2"), end);
        }
        pointSource1 = new GeoJsonSource(
                POINT_1_SOURCE_ID,
                Feature.fromGeometry(
                        Point.fromLngLat(0.0, 0.0),
                        null,
                        "point-1"
                )
        );
        pointSource2 = new GeoJsonSource(
                POINT_2_SOURCE_ID,
                Feature.fromGeometry(
                        Point.fromLngLat(0.0, 0.0),
                        null,
                        "point-2"
                )
        );
        pointLayer1 = new SymbolLayer(POINT_1_LAYER_ID, POINT_1_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage("marker-icon-point-1"),
                        PropertyFactory.iconOpacity(0.0f),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                );
        pointLayer2 = new SymbolLayer(POINT_2_LAYER_ID, POINT_2_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage("marker-icon-point-2"),
                        PropertyFactory.iconOpacity(0.0f),
                        PropertyFactory.iconOffset(new Float[]{0f, -DisplayUtils.dpToPxInFloat(8)}),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                );
        style.addSource(pointSource1);
        style.addSource(pointSource2);
        routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(0f),
                lineColor(ContextCompat.getColor(requireContext(), R.color.app_orange))
        );
        style.addLayer(routeLayer);
        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>())));
        style.addLayerAbove(pointLayer1, ROUTE_LAYER_ID);
        style.addLayerAbove(pointLayer2, ROUTE_LAYER_ID);
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

    private View generateTaxiBulb() {
        View customView = LayoutInflater.from(requireContext()).inflate(
                R.layout.taxi_bulb,
                null
        );
        customView.setLayoutParams(new FrameLayout.LayoutParams(
                DisplayUtils.dpToPx(markerSize),
                DisplayUtils.dpToPx(markerSize)
        ));
        return customView;
    }

    public void enableMyLocation(boolean enable) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            locationComponent.setLocationComponentEnabled(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRoute() {
        routeLayer.setProperties(
                lineWidth(0f)
        );
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (s != null) {
            s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
        }
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointSource1.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );
        pointSource2.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );
        if (timeMarker != null) {
            markerViewManager.removeMarker(timeMarker);
        }
    }

    public void showRoute(@Nullable SearchRoute o) {
        routeLayer.setProperties(
                lineWidth(3f)
        );
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        LatLng origin = null;
        LatLng destination = null;
        try {
            //noinspection ConstantConditions
            origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
            //noinspection ConstantConditions
            destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Point> points = new ArrayList<>();
        try {
            for (List<Double> one : o.getPaths().get(0).getPoints().getCoordinates()) {
                points.add(Point.fromLngLat(one.get(0), one.get(1)));
            }
            List<Double> one = o.getPaths().get(0).getPoints().getCoordinates().get(0);
            List<Double> two = o.getPaths().get(0).getPoints().getCoordinates().get(o.getPaths().get(0).getPoints().getCoordinates().size() - 1);
            origin = new LatLng(one.get(1), one.get(0));
            destination = new LatLng(two.get(1), two.get(0));
        } catch (Exception e) {
            Log.d("MapFragment", "onEr" + e.toString());
        }
        Feature directionsRouteFeature = Feature.fromGeometry(
                LineString.fromLngLats(points, null)
        );
        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (points.size() > 2) {
            if (s != null) {
                s.setGeoJson(collection);
            }
        } else {
            if (s != null) {
                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
            }
        }
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        if (origin != null) {
            pointSource1.setGeoJson(
                    Point.fromLngLat(origin.getLongitude(), origin.getLatitude())
            );
            latLngBounds.include(origin);
            timeMarker = new MarkerView(origin, bulb);
            timeMarker.setOnPositionUpdateListener(pf -> {
                pf.offset(
                        -((float) bulb.getWidth() / 2),
                        -((float) bulb.getHeight())
                );
                return pf;
            });
            if (timeMarker != null) {
                markerViewManager.addMarker(timeMarker);
            }
        }
        if (destination != null) {
            pointSource2.setGeoJson(
                    Point.fromLngLat(destination.getLongitude(), destination.getLatitude())
            );
            latLngBounds.include(destination);
        }
        for (Point point : points) {
            latLngBounds.include(new LatLng(point.latitude(), point.longitude()));
        }
        LatLngBounds llb = latLngBounds.build();
        mapView.post(() -> mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                llb,
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(100),
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(90) + ((listener != null) ? listener.getMapPadding() : 0)
        )));
    }

    public void deleteRouteMine() {
        routeLayer.setProperties(
                lineWidth(0f)
        );
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (s != null) {
            s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
        }
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointSource1.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );
        pointSource2.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );
    }

    public void getRoute(Point originPoint, Point destinationPoint, boolean animate) {
        Log.e("draweRoute", "Start");
        MapboxDirections client = MapboxDirections.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(originPoint)
                .destination(destinationPoint)
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() == null || response.body().routes().size() < 1) {
                    return;
                }

                DirectionsRoute currentRoute = response.body().routes().get(0);
//                double km = currentRoute.distance().intValue() * 0.001;
//                Log.e("Km", String.valueOf(km).substring(0, 3) + " км");
//                Log.e("TimeTv", TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue()) + "");
                SharedHelper.putKey(Objects.requireNonNull(getContext()), "timeEstimated", ((int) TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue())));
                showRouteMine(currentRoute, new LatLng(originPoint.latitude(), originPoint.longitude()), new LatLng(destinationPoint.latitude(), destinationPoint.longitude()), animate);
            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, Throwable t) {
                Log.e("draweRoute", t.getMessage());
            }
        });
    }

    public void getRoute(Point originPoint, Point destinationPoint) {
        Log.e("draweRoute", "Start");
        MapboxDirections client = MapboxDirections.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(originPoint)
                .destination(destinationPoint)
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() == null || response.body().routes().size() < 1) {
                    return;
                }

                DirectionsRoute currentRoute = response.body().routes().get(0);
                Log.e("draweRoute", currentRoute.toString());
                double km = currentRoute.distance().intValue() * 0.001;
                Log.e("Km", String.valueOf(km).substring(0, 3) + " км");
                Log.e("TimeTv", TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue()) + " мин");
                SharedHelper.putKey(Objects.requireNonNull(getContext()), "timeEstimated", TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue()));
//                drawNavigationPolylineRoute(currentRoute);
                showRouteMine(currentRoute, new LatLng(originPoint.latitude(), originPoint.longitude()), new LatLng(destinationPoint.latitude(), destinationPoint.longitude()));
            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, Throwable t) {
                Log.e("draweRoute", t.getMessage());
            }
        });
    }

    private void showRouteMine(final DirectionsRoute route, LatLng origin, LatLng destination, boolean animate) {
        try {
            bulb.setAlpha(0f);
        } catch (Exception ignored) {
        }
        routeLayer.setProperties(
                lineWidth(3f)
        );
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        List<Feature> directionsRouteFeatureList = new ArrayList<>();
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> points = lineString.coordinates();
        for (int i = 0; i < points.size(); i++) {
            directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        }
        Feature directionsRouteFeature = Feature.fromGeometry(
                LineString.fromLngLats(points, null)
        );
        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (points.size() > 2) {
            if (s != null) {
                s.setGeoJson(collection);
            }
        } else {
            if (s != null) {
                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
            }
        }
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        if (origin != null) {
            pointSource1.setGeoJson(
                    Point.fromLngLat(origin.getLongitude(), origin.getLatitude())
            );
            latLngBounds.include(origin);
        }
        if (destination != null) {
            pointSource2.setGeoJson(
                    Point.fromLngLat(destination.getLongitude(), destination.getLatitude())
            );
            latLngBounds.include(destination);
        }
        for (Point point : points) {
            latLngBounds.include(new LatLng(point.latitude(), point.longitude()));
        }
        LatLngBounds llb = latLngBounds.build();
        if (animate) {
            mapView.post(() -> mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    llb,
                    DisplayUtils.dpToPx(42),
                    DisplayUtils.dpToPx(100),
                    DisplayUtils.dpToPx(42),
                    DisplayUtils.dpToPx(90) + ((listener != null) ? listener.getMapPadding() : 0)
            )));
        }
    }

    private void showRouteMine(final DirectionsRoute route, LatLng origin, LatLng destination) {
        routeLayer.setProperties(
                lineWidth(3f)
        );
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        List<Feature> directionsRouteFeatureList = new ArrayList<>();
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> points = lineString.coordinates();
        for (int i = 0; i < points.size(); i++) {
            directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        }
        Feature directionsRouteFeature = Feature.fromGeometry(
                LineString.fromLngLats(points, null)
        );
        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (points.size() > 2) {
            if (s != null) {
                s.setGeoJson(collection);
            }
        } else {
            if (s != null) {
                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
            }
        }
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        if (origin != null) {
            pointSource1.setGeoJson(
                    Point.fromLngLat(origin.getLongitude(), origin.getLatitude())
            );
            latLngBounds.include(origin);
            timeMarker = new MarkerView(origin, bulb);
            timeMarker.setOnPositionUpdateListener(pf -> {
                pf.offset(
                        -((float) bulb.getWidth() / 2),
                        -((float) bulb.getHeight())
                );
                return pf;
            });
        }
        if (destination != null) {
            pointSource2.setGeoJson(
                    Point.fromLngLat(destination.getLongitude(), destination.getLatitude())
            );
            latLngBounds.include(destination);
        }
        for (Point point : points) {
            latLngBounds.include(new LatLng(point.latitude(), point.longitude()));
        }
        LatLngBounds llb = latLngBounds.build();
        mapView.post(() -> mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                llb,
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(100),
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(90) + ((listener != null) ? listener.getMapPadding() : 0)
        )));
    }

//    private void drawRoute(DirectionsRoute route) {
//        // Convert List<Waypoint> into LatLng[]
//        List<Waypoint> waypoints = route.getGeometry().getWaypoints();
//        LatLng[] point = new LatLng[waypoints.size()];
//        for (int i = 0; i < waypoints.size(); i++) {
//            point[i] = new LatLng(
//                    waypoints.get(i).getLatitude(),
//                    waypoints.get(i).getLongitude());
//        }
//
//        // Draw Points on MapView
//        mapBoxMap.addPolyline(new PolylineOptions()
//                .add(point)
//                .color(Color.parseColor("#38afea"))
//                .width(5));
//    }

    // map

    public void showTime(String time) {
        if (this.time != null) {
            this.time.setText(time.split(" ")[0] + " минут");
            bulb.setAlpha(1f);
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
        Log.e("eeeeeeeeeeeee", "eeeeeeeeeeee");
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
            Log.e("detector", detector.getScaleFactor() + " " + zoom);
            updateAllMarker(zoom);
        }
    }

    @Override
    public void onScaleEnd(@NonNull @NotNull StandardScaleGestureDetector detector) {
        isScaling = false;
        MoveGestureDetector gestureDetector = androidGesturesManager.getMoveGestureDetector();
        gestureDetector.setMoveThreshold(0f);
    }

    private void updateAllMarker(double zoom) {
        for (int i = 0; i < taxi.size(); i++) {
            markerViewManager.removeMarker(taxi.get(i));
        }
        for (int i = 0; i < taxiLocations.size(); i++) {
            markerSize = (int) ((zoom / 16) * 3.65) * 16;
            taxi.add(new MarkerView(taxiLocations.get(i), generateTaxiBulb()));
            markerViewManager.addMarker(taxi.get(taxi.size() - 1));
        }
    }

    public void addMarker(LatLng latLng) {
        taxi.add(new MarkerView(latLng, generateTaxiBulb()));
        markerViewManager.addMarker(taxi.get(taxi.size() - 1));
        taxiLocations.add(latLng);
    }

    public void clearAllMarker() {
        for (int i = 0; i < taxi.size(); i++) {
            markerViewManager.removeMarker(taxi.get(i));
        }
        taxi.clear();
        taxiLocations.clear();
    }

    public void showRouteForTaxi(@Nullable SearchRoute o) {
        routeLayer.setProperties(
                lineWidth(3f)
        );
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(0f)
        );
        pointSource1.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );
        pointSource2.setGeoJson(
                Point.fromLngLat(0.0, 0.0)
        );

        Log.e(">>>>>>>>>>>>>>>>>>>>>>", "2");
        LatLng origin = null;
        LatLng destination = null;
        try {
            //noinspection ConstantConditions

            origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
            //noinspection ConstantConditions
            destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(">>>>>>>>>>>>>>>>>>>>>>", "3");
        List<Point> points = new ArrayList<>();
        try {
            for (List<Double> one : o.getPaths().get(0).getPoints().getCoordinates()) {
                points.add(Point.fromLngLat(one.get(0), one.get(1)));
            }
            List<Double> one = o.getPaths().get(0).getPoints().getCoordinates().get(0);
            List<Double> two = o.getPaths().get(0).getPoints().getCoordinates().get(o.getPaths().get(0).getPoints().getCoordinates().size() - 1);
            origin = new LatLng(one.get(1), one.get(0));
            destination = new LatLng(two.get(1), two.get(0));
        } catch (Exception e) {
            Log.d("MapFragment", "onEr" + e.toString());
        }
        Feature directionsRouteFeature = Feature.fromGeometry(
                LineString.fromLngLats(points, null)
        );
        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (points.size() > 2) {
            if (s != null) {
                s.setGeoJson(collection);
            }
        } else {
            if (s != null) {
                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
            }
        }

        Log.e(">>>>>>>>>>>>>>>>>>>>>>", "4");
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        if (origin != null) {
            pointSource1.setGeoJson(
                    Point.fromLngLat(origin.getLongitude(), origin.getLatitude())
            );
            latLngBounds.include(origin);
        }
        if (destination != null) {
            pointSource2.setGeoJson(
                    Point.fromLngLat(destination.getLongitude(), destination.getLatitude())
            );
            latLngBounds.include(destination);
        }
        for (Point point : points) {
            latLngBounds.include(new LatLng(point.latitude(), point.longitude()));
        }
        LatLngBounds llb = latLngBounds.build();
        mapView.post(() -> mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                llb,
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(100),
                DisplayUtils.dpToPx(42),
                DisplayUtils.dpToPx(90) + ((listener != null) ? listener.getMapPadding() : 0)
        )));
    }
}
