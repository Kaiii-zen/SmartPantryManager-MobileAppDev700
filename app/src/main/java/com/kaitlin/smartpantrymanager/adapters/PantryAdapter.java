package com.kaitlin.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaitlin.smartpantrymanager.R;
import com.kaitlin.smartpantrymanager.models.PantryItem;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // callback interface
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(item.getQuantity() + " " + item.getUnit());

        // expiry date row only shows if an expiry date exists
        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setVisibility(View.VISIBLE);
            holder.textExpiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.textExpiry.setVisibility(View.GONE);
        }

        // Attach click listeners
        // tap opens edit screen
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // Long press deletes row
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });

        holder.buttonDelete.setOnClickListener(v -> listener.onItemLongClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textQuantity;
        TextView textExpiry;
        ImageButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textIngredientName);
            textQuantity = itemView.findViewById(R.id.textIngredientQuantity);
            textExpiry = itemView.findViewById(R.id.textIngredientExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
