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
import kz.yassy.taxi.data.network.model.MenuDrawer;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MenuViewHolder> {

    public interface ClickDelegate {
        void onClick(MenuDrawer item);
    }

    final private List<MenuDrawer> menuList;
    final private ClickDelegate delegate;

    public DrawerAdapter(List<MenuDrawer> menuList,
                         ClickDelegate delegate) {
        this.menuList = menuList;
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_drawer, parent, false);
        return new MenuViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuDrawer item = menuList.get(position);
        if (item != null) {
            holder.name.setText(item.getName());
            holder.name.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
            holder.name.setOnClickListener(v -> delegate.onClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        MenuViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text);
        }
    }
}