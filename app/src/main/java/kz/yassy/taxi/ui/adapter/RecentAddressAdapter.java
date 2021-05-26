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
import kz.yassy.taxi.data.network.model.UserAddress;

public class RecentAddressAdapter extends RecyclerView.Adapter<RecentAddressAdapter.AddressViewHolder> {
    final private ClickDelegate delegate;
    private boolean visibility = true;
    private List<UserAddress> list;

    public RecentAddressAdapter(List<UserAddress> list,
                                ClickDelegate delegate) {
        this.list = list;
        this.delegate = delegate;
    }

    public void updateVisibility(boolean b) {
        this.visibility = b;
        notifyDataSetChanged();
    }

    public void update(List<UserAddress> list) {
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
        UserAddress item = list.get(position);
        if (item != null) {
            holder.name.setText(item.getAddress());
            holder.nameSmall.setText(item.getAddress());
            holder.click.setOnClickListener(v -> delegate.onClick(item));
        }
        holder.itemView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickDelegate {
        void onClick(UserAddress item);
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