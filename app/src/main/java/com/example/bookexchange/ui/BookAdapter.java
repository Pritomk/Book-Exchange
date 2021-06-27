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

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    ArrayList<BookItem> bookItems;
    Context context;

    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BookAdapter(Context context,ArrayList<BookItem> bookItems) {
        this.context = context;
        this.bookItems = bookItems;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView bookNameCI;
        TextView priceCI;
        public BookViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            bookNameCI = itemView.findViewById(R.id.bookNameCI);
            priceCI = itemView.findViewById(R.id.priceCI);

            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
        return new BookViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bookNameCI.setText(bookItems.get(position).getBookName());
        holder.priceCI.setText(bookItems.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

}
