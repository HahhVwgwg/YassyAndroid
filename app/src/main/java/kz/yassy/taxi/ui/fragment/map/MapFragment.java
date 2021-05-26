package kz.yassy.taxi.ui.fragment.map;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String MAKI_ICON_CAFE = "cafe-15";
    private static final String MAKI_ICON_HARBOR = "harbor-15";
    private static final String MAKI_ICON_AIRPORT = "airport-15";
    public HashMap<Integer, Symbol> taxiSymbols = new HashMap<>();
    public HashMap<Integer, ValueAnimator> valueAnimators = new HashMap<>();

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
    private SymbolManager symbolManager;
    private Symbol symbol;
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

    private Handler handler;

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

    private int index, next;

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

    private float v;

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

    private Runnable runnable;
    private double lat, lng;
    private Point startPosition, endPosition;
    private ValueAnimator valueAnimator;
    private List<Point> points = new ArrayList<>();
    private List<Point> routeCoordinateList;
    private List<Point> markerLinePointList = new ArrayList<>();
    private int routeIndex;
    private Animator currentAnimator;
    private int counter = 0;
    private Point lastKnownLocation;
    private int speed = 120;

    public void setStartPosition(Point startPosition) {
        this.startPosition = startPosition;
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
        Drawable taxi = ContextCompat.getDrawable(requireContext(), R.drawable.taxi);
        if (taxi != null) {
            style.addImage(("taxi_icon"), taxi);
        }
        Drawable taxiComfort = ContextCompat.getDrawable(requireContext(), R.drawable.taxi);
        if (taxiComfort != null) {
            style.addImage(("taxi_icon_comfort"), taxiComfort);
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
        symbolManager = new SymbolManager(mapView, mapBoxMap, style);

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconOptional(true);
        symbolManager.setTextAllowOverlap(true);
        symbol = symbolManager.create(new SymbolOptions()
                .withLatLng(new LatLng(43.2973300, 68.2517500))
                .withIconImage("taxi_icon")
                .withIconSize(0.17f)
                .withIconOpacity(0f)
                .withDraggable(false));
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
                double km = currentRoute.distance().intValue() * 0.001;
                Log.e("TimeTv", String.valueOf(km).substring(0, 3) + " км");
                Log.e("TimeTv", TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue()) + "");
                SharedHelper.putKey(Objects.requireNonNull(getContext()), "timeEstimated", ((int) TimeUnit.SECONDS.toMinutes(Objects.requireNonNull(currentRoute.duration()).longValue())));
                showRouteMine(currentRoute, new LatLng(originPoint.latitude(), originPoint.longitude()), new LatLng(destinationPoint.latitude(), destinationPoint.longitude()), animate);
            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, Throwable t) {
                Log.e("draweRoute", t.getMessage());
            }
        });
    }

    public void getRouteWithoutTaxiAnimation(Point originPoint, Point destinationPoint) {
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
                showRouteWithoutTaxiAnimation(currentRoute, new LatLng(originPoint.latitude(), originPoint.longitude()), new LatLng(destinationPoint.latitude(), destinationPoint.longitude()));
            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, Throwable t) {
                Log.e("draweRoute", t.getMessage());
            }
        });
    }

    public void hideTaxiAnimation() {
        if (symbol != null)
            symbol.setIconOpacity(0f);
        if (handler != null) {
            symbolManager.update(symbol);
            valueAnimator.removeAllListeners();
            valueAnimator.removeAllUpdateListeners();
            deleteRouteMine();
            handler.removeCallbacks(runnable);
            valueAnimator.cancel();
            valueAnimator.end();
            runnable = null;
            handler = null;
            Log.e("restarted", "yeah I admit it");
        }
    }

    private void stopTaxiAnimation() {
        if (handler != null) {
            valueAnimator.removeAllListeners();
            valueAnimator.removeAllUpdateListeners();
            handler.removeCallbacks(runnable);
            valueAnimator.cancel();
            valueAnimator.end();
            runnable = null;
            handler = null;
            Log.e("restarted", "yeah I admit it");
        }
    }

    @SuppressLint("MissingPermission")
    private void showTaxiAnimation() {
        symbol.setIconOpacity(1f);
        symbolManager.update(symbol);
        try {
            locationComponent.setLocationComponentEnabled(false);
        } catch (Exception ignored) {
        }
        if (handler != null) {
            valueAnimator.removeAllListeners();
            valueAnimator.removeAllUpdateListeners();
            handler.removeCallbacks(runnable);
            valueAnimator.cancel();
            valueAnimator.end();
            runnable = null;
            handler = null;
            Log.e("restarted", "yeah I admit it");
        }
        handler = new Handler();
        index = -1;
        next = 1;
        runnable = () -> {
            if (index == points.size() - 2) {
                points.clear();
                handler.removeCallbacks(runnable);
                return;
            }
            if (index < points.size() - 1) {
                index++;
                Log.e("distanceMine", index + " incremented");
                next = index + 1;
            }
            if (index < points.size() - 1) {
                startPosition = points.get(index);
                endPosition = points.get(next);
            }
            Log.e("distanceMine", TurfMeasurement.distance(startPosition, endPosition, "meters") + " ");
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration((int) TurfMeasurement.distance(startPosition, endPosition, "meters") * speed);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(valueAnimator1 -> {
                v = valueAnimator1.getAnimatedFraction();
                lng = v * endPosition.longitude() + (1 - v)
                        * startPosition.longitude();
                lat = v * endPosition.latitude() + (1 - v)
                        * startPosition.latitude();
                LatLng newPos = new LatLng(lat, lng);
                symbol.setIconRotate(90 + getBearing(startPosition, newPos));
                symbol.setLatLng(newPos);
                symbolManager.update(symbol);
            });
            valueAnimator.start();
            Log.e("distanceMine", "second" + TurfMeasurement.distance(startPosition, endPosition, "meters") + " ");
            handler.postDelayed(runnable, (int) TurfMeasurement.distance(startPosition, endPosition, "meters") * speed);
        };
        handler.postDelayed(runnable, 0);
    }

    private void animate() {
// Check if we are at the end of the points list
        if ((routeCoordinateList.size() > routeIndex)) {
            Point indexPoint = routeCoordinateList.get(routeIndex);
            Point newPoint = Point.fromLngLat(indexPoint.longitude(), indexPoint.latitude());
            currentAnimator = createLatLngAnimator(indexPoint, newPoint);
            currentAnimator.start();
            routeIndex++;
        } else if (routeIndex == routeCoordinateList.size()) {
            routeIndex = 0;
            if (routeCoordinateList != null)
                routeCoordinateList.clear();
        }
    }

    private void animateWithEnd() {
        if ((routeCoordinateList.size() > routeIndex)) {
            Point indexPoint = routeCoordinateList.get(routeIndex);
            Point newPoint = Point.fromLngLat(indexPoint.longitude(), indexPoint.latitude());
            currentAnimator = createLatLngAnimatorWithEnd(indexPoint, newPoint);
            currentAnimator.start();
            routeIndex++;
        } else if (routeIndex == routeCoordinateList.size()) {
            routeIndex = 0;
            if (routeCoordinateList != null)
                routeCoordinateList.clear();
            GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
            s.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(new ArrayList<>())));
        }
    }

    private void initData(@NonNull FeatureCollection featureCollection) {
        if (featureCollection.features() != null) {
            LineString lineString = ((LineString) featureCollection.features().get(0).geometry());
            if (lineString != null) {
                if (routeCoordinateList != null)
                    routeCoordinateList.clear();
                if (markerLinePointList != null)
                    markerLinePointList.clear();
                routeCoordinateList = lineString.coordinates();
                animate();
            }
        }
    }

    private void initDataWithEnd(@NonNull FeatureCollection featureCollection) {
        Log.e("draweRoute", "initDataWithEnd");
        if (featureCollection.features() != null) {
            LineString lineString = ((LineString) featureCollection.features().get(0).geometry());
            if (lineString != null) {
                if (routeCoordinateList != null)
                    routeCoordinateList.clear();
                if (markerLinePointList != null)
                    markerLinePointList.clear();
                routeCoordinateList = lineString.coordinates();
                animateWithEnd();
            }
        }
    }

    private Animator createLatLngAnimatorWithEnd(Point currentPosition, Point targetPosition) {
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new PointEvaluator(), currentPosition, targetPosition);
        Log.e("Duration", (long) TurfMeasurement.distance(currentPosition, targetPosition, "meters") + " ");
        latLngAnimator.setDuration((long) TurfMeasurement.distance(currentPosition, targetPosition, "meters") + 200);
        latLngAnimator.setInterpolator(new LinearInterpolator());
        latLngAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animateWithEnd();
            }
        });
        latLngAnimator.addUpdateListener(animation -> {
            Point point = (Point) animation.getAnimatedValue();
//            pointSource2.setGeoJson(point);
            markerLinePointList.add(point);
            GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
            s.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(markerLinePointList)));
        });

        return latLngAnimator;
    }

    private Animator createLatLngAnimator(Point currentPosition, Point targetPosition) {
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new PointEvaluator(), currentPosition, targetPosition);
        Log.e("Duration", (long) TurfMeasurement.distance(currentPosition, targetPosition, "meters") + " ");
        latLngAnimator.setDuration((long) TurfMeasurement.distance(currentPosition, targetPosition, "meters") + 100);
        latLngAnimator.setInterpolator(new LinearInterpolator());
        latLngAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animate();
            }
        });
        latLngAnimator.addUpdateListener(animation -> {
            Point point = (Point) animation.getAnimatedValue();
//            pointSource2.setGeoJson(point);
            markerLinePointList.add(point);
            GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
            s.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(markerLinePointList)));
        });

        return latLngAnimator;
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
                PropertyFactory.iconOpacity(0f)
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
        this.points.clear();
        this.points = points;
        if (startPosition == null) {
            showTaxiAnimation();
        } else {
            int distance = (int) TurfMeasurement.distance(startPosition, points.get(0), "meters");
            if (distance > 1000) {
                showTaxiAnimation();
                speed = 120;
            } else if (distance > 500) {
                if (speed != 50)
                    showTaxiAnimation();
                speed = 50;
            } else if (distance > 210) {
                if (speed != 80)
                    showTaxiAnimation();
                speed = 80;
            } else if (distance > 30) {
                if (speed != 120)
                    showTaxiAnimation();
                speed = 120;
            } else if (distance > 10) {
                if (speed != 500)
                    showTaxiAnimation();
                speed = 500;
            } else {
                if (speed != 9000)
                    stopTaxiAnimation();
                speed = 9000;
            }
            Log.e("DistanceChange", distance + " " + speed);
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

    private void showRouteWithoutTaxiAnimation(final DirectionsRoute route, LatLng origin, LatLng destination) {
        routeLayer.setProperties(
                lineWidth(3f)
        );
        pointLayer1.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        pointLayer2.setProperties(
                PropertyFactory.iconOpacity(1f)
        );
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (s != null) {
            s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
        }
        List<Feature> directionsRouteFeatureList = new ArrayList<>();
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> points = lineString.coordinates();
        for (int i = 0; i < points.size(); i++) {
            directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        }
        this.points.clear();
        this.points = points;
        initDataWithEnd(FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromPolyline(route.geometry(), PRECISION_6))));
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
        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
        if (s != null) {
            s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
        }
        List<Feature> directionsRouteFeatureList = new ArrayList<>();
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> points = lineString.coordinates();
        for (int i = 0; i < points.size(); i++) {
            directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        }
        this.points.clear();
        this.points = points;
//        showTaxiAnimation();
//        Feature directionsRouteFeature = Feature.fromGeometry(
//                LineString.fromLngLats(points, null)
//        );
//        FeatureCollection collection = FeatureCollection.fromFeature(directionsRouteFeature);
//        GeoJsonSource s = style.getSourceAs(ROUTE_SOURCE_ID);
//        if (points.size() > 2) {
//            if (s != null) {
//                s.setGeoJson(collection);
//            }
//        } else {
//            if (s != null) {
//                s.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
//            }
//        }
        initData(FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromPolyline(route.geometry(), PRECISION_6))));
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
//            updateAllMarker(zoom);
        }
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

    public void addMarker(LatLng latLng, boolean isComfort, int providerId) {
        taxiSymbols.put(providerId, symbolManager.create(new SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(isComfort ? "taxi_icon_comfort" : "taxi_icon")
                .withIconSize(0.17f)
                .withDraggable(false)));
        taxiLocations.add(latLng);
        Log.e("Callling", "add");
    }

    @Override
    public void onScaleEnd(@NonNull @NotNull StandardScaleGestureDetector detector) {
        isScaling = false;
        MoveGestureDetector gestureDetector = androidGesturesManager.getMoveGestureDetector();
        gestureDetector.setMoveThreshold(0f);
    }

    public void updateMarker(LatLng latLng, boolean b, int id) {
//        if (valueAnimators.containsKey(id)){
//            valueAnimators.get(id).cancel();
//            valueAnimators.get(id).removeAllUpdateListeners();
//            valueAnimators.get(id).removeAllListeners();
//            valueAnimators.get(id).end();
//        }
        Log.e("Callling", "update");
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//        valueAnimator.setDuration(5000);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.addUpdateListener(valueAnimator1 -> {
//            float v = valueAnimator1.getAnimatedFraction();
//            double lng = v * latLng.getLongitude() + (1 - v)
//                    * taxiSymbols.get(id).getLatLng().getLongitude();
//            double lat = v * latLng.getLatitude() + (1 - v)
//                    * taxiSymbols.get(id).getLatLng().getLatitude();
//            Log.e("Callling","insideUpdate");
//            LatLng newPos = new LatLng(lat, lng);
//            taxiSymbols.get(id).setIconRotate(90 + getBearing(Point.fromLngLat(taxiSymbols.get(id).getLatLng().getLongitude(),taxiSymbols.get(id).getLatLng().getLatitude()), newPos));
//            taxiSymbols.get(id).setLatLng(newPos);
//            symbolManager.update(symbol);
//        });
//        valueAnimator.start();
//        valueAnimators.put(id, valueAnimator);
        if (!taxiSymbols.get(id).getLatLng().equals(latLng)) {
            float type = getBearing(taxiSymbols.get(id).getLatLng(), latLng);
            if (type != -1) {
                Objects.requireNonNull(taxiSymbols.get(id)).setIconRotate(90 + type);
            }
        }
//        if (animateTaxi != null){
//            animateTaxi.getValueAnimator().cancel();
//            animateTaxi.getValueAnimator().end();
//            animateTaxi.getValueAnimator().removeAllListeners();
//            animateTaxi.getValueAnimator().removeAllListeners();
//        }
//        animateTaxi = new AnimateTaxi(taxiSymbols.get(id).getLatLng(),latLng, taxiSymbols.get(id), symbolManagerUpdate);
//        animateTaxi.showTaxiAnimation();
        Objects.requireNonNull(taxiSymbols.get(id)).setLatLng(latLng);
        symbolManager.update(taxiSymbols.get(id));
    }

//    private SymbolManagerUpdate symbolManagerUpdate = new SymbolManagerUpdate() {
//        @Override
//        public void updateSymbol(Symbol symbol) {
//            symbolManager.update(symbol);
//        }
//    };
//    private AnimateTaxi animateTaxi;

    public void deleteMarker(Integer id) {
        Log.e("Callling", "delete");
        symbolManager.delete(taxiSymbols.get(id));
    }

    public void clearAllMarker() {
//        for (int i = 0; i < taxiSymbols.size(); i++) {
//            symbolManager.delete(taxiSymbols.get(i));
//        }
        for (HashMap.Entry<Integer, Symbol> entry : taxiSymbols.entrySet()) {
            symbolManager.delete(entry.getValue());
        }
        taxiSymbols.clear();
        taxiLocations.clear();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.getLatitude() - end.getLatitude());
        double lng = Math.abs(begin.getLongitude() - end.getLongitude());
        Log.e("Callling", "lat" + lat);
        Log.e("Callling", "long" + lng);
        if (lat < 15.2200000000461841E-5 && lng < 15.2200000000461841E-5) {
            Log.e("Callling", "-1");
            return -1;
        }
        if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private float getBearing(Point start, LatLng end) {
        LatLng begin = new LatLng(start.latitude(), start.longitude());
        double lat = Math.abs(begin.getLatitude() - end.getLatitude());
        double lng = Math.abs(begin.getLongitude() - end.getLongitude());

        if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private static class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            return Point.fromLngLat(
                    startValue.longitude() + ((endValue.longitude() - startValue.longitude()) * fraction),
                    startValue.latitude() + ((endValue.latitude() - startValue.latitude()) * fraction)
            );
        }
    }
}
