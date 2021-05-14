package kz.yassy.taxi.ui.activity.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kz.yassy.taxi.R;
import kz.yassy.taxi.data.network.model.HelpLocal;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.MyViewHolder> {

    private List<HelpLocal> helpLocals;
    private Context mContext;

    public HelpAdapter(List<HelpLocal> helpLocals) {
        this.helpLocals = helpLocals;
    }

    @NonNull
    @Override
    public HelpAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new HelpAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_help, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HelpAdapter.MyViewHolder holder, int position) {
        holder.text.setText(helpLocals.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return helpLocals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView text;

        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.item_view) {
                if (helpLocals.get(position).getOpenType() >= 100) {
                    Log.e("EEEEEEEEEEEEEE", "EEEEEEEEEEE");
                    Intent intent = new Intent(mContext, HelpChildActivity.class);
                    intent.putExtra("type", helpLocals.get(position).getOpenType());
                    mContext.startActivity(intent);
                }
            }
        }
    }
}
