package com.thinkincab.app.ui.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinkincab.app.R;
import com.thinkincab.app.data.network.model.Service;
import com.thinkincab.app.data.network.model.Tariffs;
import com.thinkincab.app.ui.fragment.service.ServiceTypesFragment;

import java.util.List;

import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SERVICE_TYPE;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private final List<Service> list;
    private int lastCheckedPos = 0;
    private final ServiceTypesFragment.ServiceListener mListener;
    private Tariffs estimateFare;
    private int itemWidth;

    public ServiceAdapter(List<Service> list, ServiceTypesFragment.ServiceListener listener, Tariffs fare) {
        this.list = list;
        this.mListener = listener;
        this.estimateFare = fare;
    }

    public ServiceAdapter(List<Service> list,
                          ServiceTypesFragment.ServiceListener listener,
                          Tariffs fare, int itemWidth) {
        this.list = list;
        this.mListener = listener;
        this.estimateFare = fare;
        this.itemWidth = itemWidth;
    }

    public void setEstimateFare(Tariffs estimateFare) {
        this.estimateFare = estimateFare;
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
        }

        if (position == lastCheckedPos) {
            holder.serviceName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_black));
            holder.itemView.setElevation(24f);
            holder.itemView.setBackgroundResource(R.drawable.service_bg_active);
            holder.itemView.setAlpha(1);
            if (obj !=  null && estimateFare != null && estimateFare.getType() != null) {
                for (Tariffs.TariffType type : estimateFare.getType()) {
                    if (type.getServiceType() == obj.getId()) {
                        holder.price.setText(holder.itemView.getContext().getString(R.string.sum_template, type.getEstimatedFare()));
                    }
                }

            }
        } else {
            holder.serviceName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_service_grey));
            holder.itemView.setBackgroundResource(R.drawable.service_bg_inactive);
            holder.itemView.setElevation(0f);
            holder.itemView.setAlpha((float) 0.5);
        }

        holder.itemView.setOnClickListener(view -> {
            Service object = list.get(position);
            if (object != null) {
                if (view.getId() == R.id.item_view) {
                    if (lastCheckedPos == position) {
                        mListener.whenClicked(position);
                    } else {
                        lastCheckedPos = position;
                        RIDE_REQUEST.put(SERVICE_TYPE, object.getId());
                        notifyDataSetChanged();
                    }
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
