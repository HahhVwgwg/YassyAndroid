package com.thinkincab.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkincab.app.R;
import com.thinkincab.app.data.network.model.SearchAddress;

import java.util.List;

public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressAdapter.AddressViewHolder> {

    public interface ClickDelegate {
        void onClick(SearchAddress item);
    }

    private List<SearchAddress> list;
    final private ClickDelegate delegate;

    public SearchAddressAdapter(List<SearchAddress> list,
                              ClickDelegate delegate) {
        this.list = list;
        this.delegate = delegate;
    }

    public void update(List<SearchAddress> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        SearchAddress item = list.get(position);
        if (item != null) {
            String shortAddress = "";
            if (item.getAddress().getAmenity() != null) {
                shortAddress = item.getAddress().getAmenity();
            } else if (item.getAddress().getRoad() != null && item.getAddress().getHouse() != null) {
                shortAddress = item.getAddress().getRoad() + " " + item.getAddress().getHouse();
            } else if (item.getAddress().getRoad() != null) {
                shortAddress = item.getAddress().getRoad();
            } else if (item.getAddress().getTown() != null) {
                shortAddress = item.getAddress().getTown();
            } else if (item.getAddress().getCounty() != null) {
                shortAddress = item.getAddress().getCounty();
            } else if (item.getAddress().getState() != null) {
                shortAddress = item.getAddress().getState();
            }
            holder.name.setText(shortAddress);
            holder.nameSmall.setText(item.getDisplayName());
            holder.click.setOnClickListener(v -> delegate.onClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView nameSmall;
        private final View click;

        AddressViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_1);
            nameSmall = view.findViewById(R.id.text_2);
            click = view.findViewById(R.id.address_wrapper);
        }
    }
}