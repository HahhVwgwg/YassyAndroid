package kz.yassy.taxi.ui.fragment.map;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.turf.TurfMeasurement;

import java.util.ArrayList;
import java.util.List;

public class AnimateTaxi implements Runnable {
    private Handler handler;
    private int index, next;
    private float v;
    private double lat, lng;
    private Point startPosition, endPosition;
    private ValueAnimator valueAnimator;
    private List<Point> points = new ArrayList<>();
    private Symbol symbol;
    private SymbolManagerUpdate listener;

    public AnimateTaxi(LatLng startPosition, LatLng endPosition, Symbol symbol, SymbolManagerUpdate listener) {
        this.startPosition = Point.fromLngLat(startPosition.getLongitude(), startPosition.getLatitude());
        this.endPosition = Point.fromLngLat(endPosition.getLongitude() + 200.2200000000461841E-5, endPosition.getLatitude() + 200.2200000000461841E-5);
        this.symbol = symbol;
        this.listener = listener;
    }

    public void showTaxiAnimation() {
//        if (handler != null){
//            valueAnimator.removeAllListeners();
//            valueAnimator.removeAllUpdateListeners();
//            handler.removeCallbacks(this);
//            valueAnimator.cancel();
//            valueAnimator.end();
//            handler = null;
//            Log.e("restarted","yeah I admit it");
//        }
//        handler = new Handler();
//        index = -1;
//        next = 1;
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration((int) TurfMeasurement.distance(startPosition, endPosition, "meters") * 100);
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
            listener.updateSymbol(symbol);
            Log.e("Eeeee", "running");
        });
        valueAnimator.start();
//        handler.postDelayed(this, 0);
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

    @Override
    public void run() {
//        if (index == points.size() - 2){
//            points.clear();
//            handler.removeCallbacks(this);
//            return;
//        }
//        if (index < points.size() - 1) {
//            index++;
//            Log.e("distanceMine", index + " incremented");
//            next = index + 1;
//        }
//        if (index < points.size() - 1) {
//            startPosition = points.get(index);
//            endPosition = points.get(next);
//        }
        Log.e("distanceMine", TurfMeasurement.distance(startPosition, endPosition, "meters") + " ");
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration((int) TurfMeasurement.distance(startPosition, endPosition, "meters") * 100);
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
            listener.updateSymbol(symbol);
        });
        valueAnimator.start();
        Log.e("distanceMine", "second" + TurfMeasurement.distance(startPosition, endPosition, "meters") + " ");
//        handler.postDelayed(this, (int)TurfMeasurement.distance(startPosition, endPosition, "meters") * 100);
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Point getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point startPosition) {
        this.startPosition = startPosition;
    }

    public Point getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Point endPosition) {
        this.endPosition = endPosition;
    }

    public ValueAnimator getValueAnimator() {
        return valueAnimator;
    }

    public void setValueAnimator(ValueAnimator valueAnimator) {
        this.valueAnimator = valueAnimator;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
