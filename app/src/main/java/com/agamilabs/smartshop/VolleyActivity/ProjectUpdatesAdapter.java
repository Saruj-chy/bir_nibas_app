package com.agamilabs.smartshop.VolleyActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.BatiChatMsgModel;
import com.agamilabs.smartshop.FireInboxShow.activity.FirestoreChatImageActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProjectUpdatesAdapter extends RecyclerView.Adapter<ProjectUpdatesAdapter.FirestoreUserChatsViewHolder> {

    private Context mCtx;
    private List<ProjectUpdateModel> mUpdateProjectList;

    final static int SENDER_VIEWTYPE_TEXT = 1001;
    final static int SENDER_VIEWTYPE_IMAGE = 1002;
    final static int SENDER_VIEWTYPE_IMAGE_TEXT = 1003;
    final static int RECEIVER_VIEWTYPE_TEXT = 2001;
    final static int RECEIVER_VIEWTYPE_IMAGE = 2002;
    final static int RECEIVER_VIEWTYPE_IMAGE_TEXT = 2003;
    final static int COMMON_DATETEXT = 3001;


    HashMap<String, String> mapDateList = new HashMap<>();
    HashMap<String, String> mapSentList = new HashMap<>();
    HashMap<String, String> mapReceiveList = new HashMap<>();

    int dataSize = 0, tempDataSize = 0;
    SharedPreferences sharedPreferences;
    static String SHARED_PREFS = "admin_store";
    String USER_ID;

    CharSequence timeFormat, tempTimeFormat = null, dateFormat, tempDateFormat = null;
    Date date, calenderDate;
    Timestamp timestamp, dateTimestamp;
    int tempPosition = 0;


    public ProjectUpdatesAdapter(Context mCtx, List<ProjectUpdateModel> mUpdateProjectList) {
        this.mCtx = mCtx;
        this.mUpdateProjectList = mUpdateProjectList;
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("admin_user_id", "");


        return SENDER_VIEWTYPE_IMAGE_TEXT;

    }


    @Override
    public FirestoreUserChatsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view;


        switch (viewtype) {
            case SENDER_VIEWTYPE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_sender_text, viewGroup, false);
                break;
            case SENDER_VIEWTYPE_IMAGE:
                view = layoutInflater.inflate(R.layout.layout_sender_image, viewGroup, false);
                break;
            case SENDER_VIEWTYPE_IMAGE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_sender_imagetext, viewGroup, false);
                break;
            case RECEIVER_VIEWTYPE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_receiver_text, viewGroup, false);
                break;
            case RECEIVER_VIEWTYPE_IMAGE:
                view = layoutInflater.inflate(R.layout.layout_receiver_image, viewGroup, false);
                break;
            case RECEIVER_VIEWTYPE_IMAGE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_receiver_imagetext, viewGroup, false);
                break;
            default:
                view = layoutInflater.inflate(R.layout.layout_sender_text, viewGroup, false);
                break;
        }

        return new FirestoreUserChatsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FirestoreUserChatsViewHolder viewHolder, final int position) {
        final ProjectUpdateModel projectUpdateModel = mUpdateProjectList.get(position);

        switch (getItemViewType(position)) {
            //            case SENDER_VIEWTYPE_TEXT:

//                ((FirestoreUserChatsViewHolder)viewHolder).bind1001(updateProjectModel);
//                break;
//            case SENDER_VIEWTYPE_IMAGE:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind1002(updateProjectModel);
//                break;
            case SENDER_VIEWTYPE_IMAGE_TEXT:
                ((FirestoreUserChatsViewHolder) viewHolder).bind1003(projectUpdateModel);
                break;
//            case RECEIVER_VIEWTYPE_TEXT:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind2001(updateProjectModel);
//                break;
//            case RECEIVER_VIEWTYPE_IMAGE:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind2002(updateProjectModel);
//                break;
//            case RECEIVER_VIEWTYPE_IMAGE_TEXT:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind2003(updateProjectModel);
//                break;
            default:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind6(uploads);
                break;
        }

//        viewHolder.dateTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mCtx, "size: "+ getItemViewType(position), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void bindDateTimeView(FirestoreUserChatsViewHolder viewHolder, BatiChatMsgModel chatMsgModel) {
        if (chatMsgModel.getChatId().equalsIgnoreCase(mapSentList.get(timeFormat))) {
            viewHolder.viewTime.setText(timeFormat);
            viewHolder.viewTime.setVisibility(View.VISIBLE);
        } else if (chatMsgModel.getChatId().equalsIgnoreCase(mapReceiveList.get(timeFormat))) {
            viewHolder.viewTime.setText(timeFormat);
            viewHolder.viewTime.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewTime.setVisibility(View.GONE);
        }

        if (chatMsgModel.getChatId().equalsIgnoreCase(mapDateList.get(dateFormat))) {
            viewHolder.dateTextView.setText(dateFormat);
            viewHolder.dateLinear.setVisibility(View.VISIBLE);
        } else {
            viewHolder.dateLinear.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mUpdateProjectList.size();
    }

    public class FirestoreUserChatsViewHolder extends RecyclerView.ViewHolder {


        TextView sentText, viewTime, receiveText, blurTextView, dateTextView;
        //        ImageView sentImageView, receiveImageView;
        ImageView image1, image2, image3, image4, image5;
        ConstraintLayout image5Constraint;
        LinearLayout dateLinear, allImageLinear;

        public FirestoreUserChatsViewHolder(View itemView) {
            super(itemView);

            viewTime = itemView.findViewById(R.id.text_time_layout);
            sentText = (TextView) itemView.findViewById(R.id.text_sent_layout);
            receiveText = itemView.findViewById(R.id.text_receive_layout);

            image1 = itemView.findViewById(R.id.image1_layout);
            image2 = itemView.findViewById(R.id.image2_layout);
            image3 = itemView.findViewById(R.id.image3_layout);
            image4 = itemView.findViewById(R.id.image4_layout);
            image5 = itemView.findViewById(R.id.image5_layout);
            blurTextView = itemView.findViewById(R.id.text_blur_layout);
            image5Constraint = itemView.findViewById(R.id.constraint_image5_layout);
            dateLinear = itemView.findViewById(R.id.linear_date_layout);
            allImageLinear = itemView.findViewById(R.id.linear_image_layout);
            dateTextView = itemView.findViewById(R.id.text_date_layout);

        }

        public void bind1001(ProjectUpdateModel chatMsgModel) {
            sentText.setText(chatMsgModel.getTitle());
        }


        public void bind1002(ProjectUpdateModel chatMsgModel) {
            imageShowLayout(chatMsgModel);
        }


        public void bind1003(ProjectUpdateModel projectUpdateModel) {

            imageShowLayout(projectUpdateModel) ;
            sentText.setText(projectUpdateModel.getTitle());

        }

        public void bind2001(ProjectUpdateModel chatMsgModel) {
            receiveText.setText(chatMsgModel.getTitle());
        }

        public void bind2002(ProjectUpdateModel chatMsgModel) {
            imageShowLayout(chatMsgModel);
        }

        public void bind2003(BatiChatMsgModel chatMsgModel) {
            receiveText.setText(chatMsgModel.getMessage());
//            imageShowLayout(chatMsgModel);
        }


        private void imageShowLayout(ProjectUpdateModel chatMsgModel) {
            String media = chatMsgModel.media;
            String thumbnail = chatMsgModel.thumbnail;

            String[] mediaList = media.split(",");
            String[] thumbList = thumbnail.split(",");

            if (mediaList.length == 4) {
                AppImageLoader.loadImageInView(thumbList[0], R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(thumbList[1], R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(thumbList[2], R.drawable.profile_image, image3);
                AppImageLoader.loadImageInView(thumbList[3], R.drawable.profile_image, image4);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.VISIBLE);
                image5Constraint.setVisibility(View.GONE);
            } else if (mediaList.length == 3) {
                AppImageLoader.loadImageInView(thumbList[0], R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(thumbList[1], R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(thumbList[2], R.drawable.profile_image, image3);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.GONE);
                image5Constraint.setVisibility(View.GONE);
            } else if (mediaList.length == 2) {
                AppImageLoader.loadImageInView(thumbList[0], R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(thumbList[1], R.drawable.profile_image, image2);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
                image4.setVisibility(View.GONE);
                image5Constraint.setVisibility(View.GONE);
            } else if (mediaList.length == 1) {
                AppImageLoader.loadImageInView(thumbList[0], R.drawable.profile_image, image1);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
                image4.setVisibility(View.GONE);
                image5Constraint.setVisibility(View.GONE);
            } else if (mediaList.length > 4) {
                AppImageLoader.loadImageInView(thumbList[0], R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(thumbList[1], R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(thumbList[2], R.drawable.profile_image, image3);
                AppImageLoader.loadImageInView(thumbList[3], R.drawable.profile_image, image5);

                int subCount = mediaList.length - 3;
                blurTextView.setText("+ " + subCount);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.GONE);
                image5Constraint.setVisibility(View.VISIBLE);
            }


            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    putIntentExtra(chatMsgModel, 0 + "");
                }
            });
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    putIntentExtra(chatMsgModel, 1 + "");

                }
            });
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    putIntentExtra(chatMsgModel, 2 + "");
                }
            });
            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    putIntentExtra(chatMsgModel, 3 + "");
                }
            });

            image5Constraint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    putIntentExtra(chatMsgModel, 3 + "");


                }
            });


        }

        private void putIntentExtra(ProjectUpdateModel batiChatMsgModel, String number) {
            List<String> realImageList = Arrays.asList(batiChatMsgModel.media.split(","));
//            List<String> object2 = chatMsgModel.getImageThumbList();
            Intent intent = new Intent(mCtx, FirestoreChatImageActivity.class);
            intent.putExtra("position", number);
            intent.putExtra("sentBy", batiChatMsgModel.wpno);
            Bundle args = new Bundle();
            args.putSerializable("real_image_list", (Serializable) realImageList);
            intent.putExtra("bundle", args);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mCtx.startActivity(intent);
        }

    }


}