package com.agamilabs.smartshop.VolleyActivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DialogImageProjectListAdapter extends RecyclerView.Adapter<DialogImageProjectListAdapter.PostViewHolder> {
    private Context mCtx;
    private List<String> mRealImgtList ;
    private OnIntentUrl onIntentUrl ;



    public DialogImageProjectListAdapter(Context mCtx, List<String> mRealImgtList, OnIntentUrl onIntentUrl) {
        this.mCtx = mCtx;
        this.mRealImgtList = mRealImgtList;
        this.onIntentUrl = onIntentUrl;
    }

    @Override
    public DialogImageProjectListAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.nested_post_image, null);
        return new DialogImageProjectListAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final DialogImageProjectListAdapter.PostViewHolder holder, final int position) {
        final String mImgRealList = mRealImgtList.get(position);

        ((DialogImageProjectListAdapter.PostViewHolder) holder).bind(mImgRealList);

    }

    @Override
    public int getItemCount() {
        return mRealImgtList.size();
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        private AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mCtx);
        private AlertDialog dialog;

        public PostViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.nested_imageView);
        }

        public void bind( String realImgUrl){

            Log.e("image_tag", " realImgUrl: "+realImgUrl+"||" ) ;
            if(!realImgUrl.isEmpty()){
                Picasso.get().load(realImgUrl)
                        .placeholder(R.drawable.camera_add)
                        .into(imageView);
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onIntentUrl.onIntentUrl(getAdapterPosition()+"");

                }
            });
        }

    }
}