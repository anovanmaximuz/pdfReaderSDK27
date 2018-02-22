package com.novel.android.dilan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Ano on 2/22/2018.
 */

public class NovelAdapter  extends RecyclerView.Adapter<NovelAdapter.ViewHolder> {
    private Drawable[] mDataSet;
    private Context mContext;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public NovelAdapter(Context context, Drawable[] DataSet) {
        this.mDataSet = DataSet;
        this.mContext = context;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public RelativeLayout mRelativeLayout;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.poster);
            mRelativeLayout = (RelativeLayout) v.findViewById(R.id.parent_poster);
        }
    }

    @Override
    public NovelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_novel, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.imageView.setImageDrawable(mDataSet[position]);
        if (mOnItemClickListener != null) {
            holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(null, view, position, 0 );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}