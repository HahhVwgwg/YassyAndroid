package kz.yassy.taxi.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kz.yassy.taxi.R;

public class EmptyAddressAdapter extends RecyclerView.Adapter<EmptyAddressAdapter.AddressViewHolder> {

    private int size;

    public EmptyAddressAdapter(int size) {
        this.size = size;
    }

    public void update(int size) {
        this.size = size;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_empty_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return size;
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {

        AddressViewHolder(View view) {
            super(view);
        }
    }
}
