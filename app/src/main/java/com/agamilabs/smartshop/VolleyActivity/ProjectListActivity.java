package com.agamilabs.smartshop.VolleyActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectListActivity extends AppCompatActivity {

    private TextView mAppbarTV;
    private CircleImageView mAppbarImage;
    private ImageButton mSearchImgBtn, mCancelImgBtn;
    private EditText mSearchET;
    private LinearLayout mSearchEditLinear;
    private ProgressBar mUserProgressbar;

    private RecyclerView mUserChatMsgRecyclerview;
    private ProjectListAdapter mProjectListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<ProjectModel> mProjectList = new ArrayList<>();

    private int pageno=1, limit=10 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        Initialize();
        initializeAdapter();

        loadProjectList();
    }

    private void loadProjectList() {
        int userno = getApplicationContext().getSharedPreferences("login_info", Context.MODE_PRIVATE).getInt("userno", 0);

        HashMap<String, String> map = new HashMap<>();
        map.put("userno", userno+"") ;
        map.put("pageno", pageno+"") ;
        map.put("limit", limit+"") ;
        AppController.getAppController().getAppNetworkController().makeRequest(Config.Project_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("list_tag", "category response: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("error").equalsIgnoreCase("false")) {
                        JSONArray mStockCategoryArray = object.getJSONArray("data");
//                        AppController.getAppController().getInAppNotifier().log("response", "mStockCategoryArray: "+ mStockCategoryArray );
                        Log.e("list_tag", "mStockCategoryArray: " + mStockCategoryArray);

                        for (int i = 0; i < mStockCategoryArray.length(); i++) {
                            JSONObject mCategoryObject = mStockCategoryArray.getJSONObject(i);
                            ProjectModel aProjectModel = new ProjectModel();
                            Field[] fields = aProjectModel.getAllFields();

                            for (int j = 0; j < fields.length; j++) {
                                String fieldName = fields[j].getName();
                                String fieldValueInJson = mCategoryObject.has(fieldName) ? mCategoryObject.getString(fieldName) : "";
                                try {
                                    fields[j].set(aProjectModel, fieldValueInJson);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            mProjectList.add(aProjectModel);

                            mProjectListAdapter.notifyDataSetChanged();

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map);
    }

    private void Initialize() {
        mAppbarTV = findViewById(R.id.appbar_text);
        mAppbarImage = findViewById(R.id.appbar_circle_image);
        mSearchImgBtn = findViewById(R.id.appbar_searchbtn);
        mSearchET = findViewById(R.id.appbar_search_edit);
        mCancelImgBtn = findViewById(R.id.appbar_cancelbtn);
        mSearchEditLinear = findViewById(R.id.appbar_linear_search);
        mUserProgressbar = findViewById(R.id.progress_user_firestore);
        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview);
    }

    private void initializeAdapter() {
        mProjectListAdapter = new ProjectListAdapter(this, mProjectList);
        mUserChatMsgRecyclerview.setAdapter(mProjectListAdapter);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mUserChatMsgRecyclerview.setLayoutManager(linearLayoutManager);
    }


}