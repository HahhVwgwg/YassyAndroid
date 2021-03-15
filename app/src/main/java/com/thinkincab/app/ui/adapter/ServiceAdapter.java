package com.thinkincab.app.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thinkincab.app.R;
import com.thinkincab.app.common.Constants;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.EstimateFare;
import com.thinkincab.app.data.network.model.Service;
import com.thinkincab.app.ui.fragment.RateCardFragment;
import com.thinkincab.app.ui.fragment.service.ServiceTypesFragment;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<Service> list;
    private TextView capacity;
    private Context context;
    private int lastCheckedPos = 0;
    private Animation zoomIn;
    private ServiceTypesFragment.ServiceListener mListener;
    private EstimateFare estimateFare;
    private boolean canNotifyDataSetChanged = true;
    private int itemWidth;

    public ServiceAdapter(Context context, List<Service> list,
                          ServiceTypesFragment.ServiceListener listener,
                          TextView capacity, EstimateFare fare) {
        this.context = context;
        this.list = list;
        this.capacity = capacity;
        this.mListener = listener;
        this.estimateFare = fare;
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
    }

    public ServiceAdapter(Context context, List<Service> list,
                          ServiceTypesFragment.ServiceListener listener,
                          TextView capacity, EstimateFare fare, int itemWidth) {
        this.context = context;
        this.list = list;
        this.capacity = capacity;
        this.mListener = listener;
        this.estimateFare = fare;
        this.itemWidth = itemWidth;
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
    }

    public void setEstimateFare(EstimateFare estimateFare) {
        this.estimateFare = estimateFare;
        canNotifyDataSetChanged = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service, parent, false));
    }

    @SuppressLint({"StringFormatMatches", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Service obj = list.get(position);
        if (itemWidth > 0) {
            holder.itemView.getLayoutParams().width = itemWidth;
        }
        if (obj != null)
            holder.serviceName.setText(obj.getName());
        if (estimateFare != null) {
            holder.price.setVisibility(View.VISIBLE);
            if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                }
            } else {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }
            }
        }

        if (position == lastCheckedPos && canNotifyDataSetChanged) {
            canNotifyDataSetChanged = true;
            if (capacity != null) {
                capacity.setText(String.valueOf(obj.getCapacity()));
            }
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.text_black));
            holder.itemView.setElevation(24f);
            holder.itemView.setBackgroundResource(R.drawable.service_bg_active);
            holder.itemView.setAlpha(1);

            if (estimateFare != null) {
                if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                } else {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }

            }
        } else {
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.text_service_grey));
            holder.itemView.setBackgroundResource(R.drawable.service_bg_inactive);
            holder.itemView.setElevation(0f);
            holder.itemView.setAlpha((float) 0.5);
        }

        holder.itemView.setOnClickListener(view -> {
            Service object = list.get(position);
            if (object != null) {
                if (view.getId() == R.id.item_view) {
                    if (lastCheckedPos == position) {
                        RateCardFragment.SERVICE = object;
                        //((MainActivity) context).changeFragment(new RateCardFragment());
                    }
                    lastCheckedPos = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Service getSelectedService() {
        if (list.size() > 0) return list.get(lastCheckedPos);
        else return null;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final View info;
        private final TextView serviceName;
        private final TextView price;
        private final ImageView image;

        MyViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            info = view.findViewById(R.id.info);
            itemView = view.findViewById(R.id.item_view);
        }
    }
}
