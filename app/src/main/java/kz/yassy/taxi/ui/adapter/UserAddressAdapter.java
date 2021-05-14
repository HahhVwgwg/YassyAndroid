package kz.yassy.taxi.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import kz.yassy.taxi.R;
import kz.yassy.taxi.data.network.model.UserAddress;

public class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.AddressViewHolder> {

    public interface ClickDelegate {
        void onClick(UserAddress item);
    }

    private List<UserAddress> list;
    final private ClickDelegate delegate;

    public UserAddressAdapter(List<UserAddress> list,
                         ClickDelegate delegate) {
        this.list = list;
        this.delegate = delegate;
    }

    public void update(List<UserAddress> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UserAddress item = list.get(position);
        if (item != null) {
            if (item.getAddress() == null || item.getAddress().isEmpty()) {
                holder.name.setText(Objects.equals(item.getType(), "home") ? R.string.add_home : R.string.add_work);
            } else {
                holder.name.setText(item.getAddress());
            }
            holder.name.setCompoundDrawablesWithIntrinsicBounds(
                    Objects.equals(item.getType(), "home") ? R.drawable.ic_home_orange : R.drawable.ic_work_orange,
                    0, 0, 0);
            holder.name.setOnClickListener(v -> delegate.onClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        AddressViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text);
        }
    }
}