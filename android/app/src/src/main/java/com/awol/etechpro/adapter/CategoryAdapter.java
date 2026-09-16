package com.awol.etechpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.awol.etechpro.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    private Context context;
    private List<String[]> categories; // [icon, name, count]
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context, List<String[]> categories,
                           OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String[] category = categories.get(position);
        holder.tvIcon.setText(category[0]);
        holder.tvName.setText(category[1]);
        holder.tvCount.setText(category[2]);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(category[1]);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvCount;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon  = itemView.findViewById(R.id.tv_category_icon);
            tvName  = itemView.findViewById(R.id.tv_category_name);
            tvCount = itemView.findViewById(R.id.tv_category_count);
        }
    }
}
