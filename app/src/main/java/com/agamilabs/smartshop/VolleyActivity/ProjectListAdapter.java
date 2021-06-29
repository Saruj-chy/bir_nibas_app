package com.agamilabs.smartshop.VolleyActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjecListViewHolder> {
    private Context mCtx;
    private List<ProjectModel> mProjectList;


    private boolean filterCheck = false;



    //sharedprefarence
    SharedPreferences sharedPreferences ;
     String SHARED_PREFS = "admin_store";
    String USERNO ;
    String usernoState = "userno";

    public ProjectListAdapter(Context mCtx, List<ProjectModel> mProjectList ) {

        this.mCtx = mCtx;
        this.mProjectList = mProjectList;


    }
//    //  subject search option
//    public void filterList(List<BatiUsersDetailsModal> filteredList, boolean textExist) {
//        filterCheck = textExist ;
//        mProjectList = filteredList;
//        notifyDataSetChanged();
//    }

    @Override
    public ProjecListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_inbox_user, null);
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS, mCtx.MODE_PRIVATE) ;

        return new ProjecListViewHolder(view);
    }
    public static void sharedSaved(SharedPreferences sharedPreferences, String state, String memberState){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(state, memberState);
        editor.apply();
    }


    @Override
    public void onBindViewHolder(final ProjecListViewHolder holder, final int position) {
        final ProjectModel projectModel = mProjectList.get(position);

        Log.e("list_tag", " id: " + projectModel.getProjectid());



        ((ProjecListViewHolder) holder).bind(projectModel);


    }

    @Override
    public int getItemCount() {
        return mProjectList.size();
    }

    class ProjecListViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewUserStatus, textViewUserLastTime;
        CircleImageView mUserImageLogo;
        ImageButton mActiveImgbtn;

        public ProjecListViewHolder(View itemView) {
            super(itemView);

            mUserImageLogo = itemView.findViewById(R.id.image_user);
            textViewUserName = itemView.findViewById(R.id.text_user_name);
            textViewUserStatus = itemView.findViewById(R.id.text_user_status);
            textViewUserLastTime = itemView.findViewById(R.id.text_user_last_time);
            mActiveImgbtn = itemView.findViewById(R.id.imgbtn_active);

        }

        public void bind(ProjectModel projectModel) {

            textViewUserName.setText(projectModel.getProjecttitle());
            textViewUserStatus.setText(projectModel.getProjectid()+" - "+projectModel.getReferenceid());

            USERNO = sharedPreferences.getString(usernoState, "");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Toast.makeText(mCtx, "success", Toast.LENGTH_SHORT).show();
//                    AppController.getAppController().getInAppNotifier().log("projectno", projectModel.projectno);
                    Intent intent = new Intent(mCtx, ProjectUpdatesActivity.class);
                    intent.putExtra("projectno", projectModel.getProjectno());
//                    intent.putExtra("userno", USERNO);

//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mCtx.startActivity(intent);
                }
            });

        }
    }
}