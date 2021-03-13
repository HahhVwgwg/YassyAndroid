package com.thinkincab.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thinkincab.app.MvpApplication;
import com.thinkincab.app.R;
import com.thinkincab.app.data.network.model.Service;

import java.text.NumberFormat;
import java.util.List;

public class ServiceAdapterSingle extends RecyclerView.Adapter<ServiceAdapterSingle.MyViewHolder> {

    private List<Service> list;
    private Context context;
    private int lastCheckedPos = 0;
    NumberFormat numberFormat;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ConstraintLayout itemView;
        private TextView serviceName, price;
        private RadioButton selectionState;
        private ImageView image;

        MyViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.service_name);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            itemView = view.findViewById(R.id.item_view);
            selectionState = view.findViewById(R.id.selection_state);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Service item = list.get(position);
            if (view.getId() == R.id.item_view) {
                lastCheckedPos = position;
                notifyDataSetChanged();
            }

        }
    }

    public ServiceAdapterSingle(Context context, List<Service> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service_single, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service item = list.get(position);
        holder.serviceName.setText(item.getName());
        if(holder.image!=null)
        Glide.with(context).load(item.getImage()).into(holder.image);

        if(lastCheckedPos==position)
            holder.itemView.setBackgroundResource(R.drawable.yellowstrokegerysolid);
        else
            holder.itemView.setBackgroundResource(R.drawable.yellowstroke);
        holder.selectionState.setChecked(lastCheckedPos == position);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public Service getSelectedService() {
        if (list.size() > 0) {
            return list.get(lastCheckedPos);
        } else {
            return null;
        }
    }

    public Service getItem(int pos) {
        return list.get(pos);
    }
}