package com.example.bookexchange.ui;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookexchange.R;
import com.example.bookexchange.util.BookItem;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {

    ArrayList<BookItem> adItems;
    Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdAdapter(Context context, ArrayList<BookItem> adItems) {
        this.context = context;
        this.adItems = adItems;
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView bookNameAd;
        public AdViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            bookNameAd = itemView.findViewById(R.id.bookNameAd);

            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),0,0,"Delete");
        }
    }
    @NonNull
    @Override
    public AdAdapter.AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item,parent,false);
        return new AdAdapter.AdViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        holder.bookNameAd.setText(adItems.get(position).getBookName());
    }

    @Override
    public int getItemCount() {
        return adItems.size();
    }

}
