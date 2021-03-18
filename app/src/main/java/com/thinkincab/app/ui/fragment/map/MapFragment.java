package com.thinkincab.app.ui.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.AndroidGesturesManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
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
import com.thinkincab.app.R;
import com.thinkincab.app.ui.utils.DisplayUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.map_box)
    MapView mapView;

    protected MapboxMap mapBoxMap;
    protected MarkerViewManager markerViewManager;
    protected AndroidGesturesManager androidGesturesManager;
    protected LocationComponent locationComponent;

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
