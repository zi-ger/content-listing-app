package com.ziger.content_listing_app;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Context context;
    private List<Category> categoryList;
    private Actions actions;
    private int posicaoRemovidoRecentemente;
    private Category categoryRemovidoRecentemente;

    public CategoryAdapter(List<Category> categoryList, Actions actions, Context context) {
        this.categoryList = categoryList;
        this.actions = actions;
        this.activity = (Activity) context;
        this.context = context;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> newCategoryList) {
        while(getItemCount()>0)
            remove(0);

        for (Category category: newCategoryList)
            insert(category);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_category, viewGroup, false);

        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.catNameTextView.setText(categoryList.get(i).getName());
        holder.catIdTextView.setText(Integer.toString(categoryList.get(i).getId()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.editCategory(viewHolder.getAdapterPosition());
            }
        });
    }

    public void update(Category cat, int position) {
        updateName(cat.getName(), position);
    }

    public void remove(int position){
        posicaoRemovidoRecentemente = position;
        categoryRemovidoRecentemente = categoryList.get(position);

        categoryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,this.getItemCount());
        actions.undo();
    }

    public void restore(){
        categoryList.add(posicaoRemovidoRecentemente, categoryRemovidoRecentemente);
        notifyItemInserted(posicaoRemovidoRecentemente);
    }

    public void insert(Category category){
        categoryList.add(category);
        notifyItemInserted(getItemCount());
    }

    public void move(int fromPosition, int toPosition){
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(categoryList, i, i+1);
        else
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(categoryList, i, i-1);
        notifyItemMoved(fromPosition,toPosition);
    }

    public void updateName(String newName, int position){
        categoryList.get(position).setName(newName);
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView catNameTextView;
        TextView catIdTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            catNameTextView = (TextView) itemView.findViewById(R.id.catNameTextView);
            catIdTextView = (TextView) itemView.findViewById(R.id.catIdTextView);
        }
    }
}