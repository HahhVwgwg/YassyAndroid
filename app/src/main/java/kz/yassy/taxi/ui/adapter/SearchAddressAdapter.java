package kz.yassy.taxi.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kz.yassy.taxi.R;
import kz.yassy.taxi.data.network.model.SearchAddress;

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
            holder.name.setText(item.getValue());
            holder.nameSmall.setText(item.getValue());
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