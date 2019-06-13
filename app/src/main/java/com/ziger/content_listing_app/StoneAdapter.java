package com.ziger.content_listing_app;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class StoneAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Context context;
    private List<Stone> stoneList;
    private Actions actions;
    private int posicaoRemovidoRecentemente;
    private Stone stoneRemovidoRecentemente;

    public StoneAdapter(List<Stone> stoneList, Actions actions, Context context) {
        this.stoneList = stoneList;
        this.actions = actions;
        this.activity = (Activity) context;
        this.context = context;
    }

    public List<Stone> getStoneList() {
        return stoneList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);

        StoneViewHolder holder = new StoneViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        StoneViewHolder holder = (StoneViewHolder) viewHolder;
        holder.nameTextView.setText(stoneList.get(i).getName());
        holder.colorTextView.setText(stoneList.get(i).getColor());

        holder.imageView.setImageBitmap(stoneList.get(i).getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.editStone(viewHolder.getAdapterPosition());
                actions.toast(stoneList.get(viewHolder.getAdapterPosition()));
            }
        });

    }

    public void remove(int position){
        posicaoRemovidoRecentemente = position;
        stoneRemovidoRecentemente = stoneList.get(position);

        stoneList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,this.getItemCount());
        actions.undo();
    }

    public void restore(){
        stoneList.add(posicaoRemovidoRecentemente, stoneRemovidoRecentemente);
        notifyItemInserted(posicaoRemovidoRecentemente);
    }

    public void insert(Stone stone){
        stoneList.add(stone);
        notifyItemInserted(getItemCount());
    }

    public void move(int fromPosition, int toPosition){
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(stoneList, i, i+1);
        else
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(stoneList, i, i-1);
        notifyItemMoved(fromPosition,toPosition);
    }

    public void updateName(String newName, int position){
        stoneList.get(position).setName(newName);
        notifyItemChanged(position);
    }

    public void updateColor(String newColor, int position){
        stoneList.get(position).setColor(newColor);
        notifyItemChanged(position);
    }


    public void updateImage (Bitmap newImage, int position){
        stoneList.get(position).setImage(newImage);
    }

    public void updateUrl (String newUrl, int position) {
        stoneList.get(position).setUrl(newUrl);
    }

    @Override
    public int getItemCount() {
        return stoneList.size();
    }

    public static class StoneViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView colorTextView;
        ImageView imageView;

        public StoneViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            colorTextView = (TextView) itemView.findViewById(R.id.colorTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}