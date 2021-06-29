package com.agamilabs.smartshop.VolleyActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.ImageReSizer;
import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectUpdatesActivity extends AppCompatActivity implements OnIntentUrl {

    private String USERNO, PROJECTNO;


    private RelativeLayout mRecyclerRelative, mChatMsgRelative, mChatSentMsgRelative;
    private ProgressBar mChatProgressbar, mSentChatProgressbar;
    private RecyclerView mChatMsgRV, mSelectImageRV;
    private EditText mChatMsgET;
    private LinearLayoutManager linearLayoutManager;
    protected ProjectUpdatesAdapter mProjectUpdatesAdapter;

    private List<ProjectUpdateModel> mProjectUpdateList = new ArrayList<>();


    //msg click part
    private Uri imageuri;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private StorageReference postStorageRef ;


    byte[] mThumbByteData, mRealByteData;
    private ArrayList<byte[]> mThumbListByte = new ArrayList<byte[]>();
    private ArrayList<byte[]> mRealListByte = new ArrayList<byte[]>();

    private List<String> getThumbUrl = new ArrayList<String>();
    private List<String> getReallUrl = new ArrayList<String>();



    //popup dialog layout
    private RelativeLayout mImgTextRelataive;
    private ProgressBar mImgTextProgress ;
    private RecyclerView mImgTextRecycler;
    private Spinner mImgTextSpin;
    private EditText mImgTextEdit;
    private TextView mCountImgText ;
    private Button mImgTextBtn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private List<String> mSpinnerDataList = new ArrayList<String>();
    private String mSentMsg;
    private int mSpinSelectedNum ;



    //TODO:: mStartForResult
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("result", "result: " + result);
//                    ImageList.clear();
//                    mRealListByte.clear();
//                    mThumbListByte.clear();

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data.getData() == null && data.getClipData() == null) {
                            Bundle extras = data.getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageuri = getImageUri(imageBitmap);
                            getImageUriResult(imageuri);
                        }



//                        if (data.getData() != null && data.getClipData() == null) {
//                            imageuri = data.getData();
//                            getImageUriResult(imageuri);
//                        }
//
//
//                        else if (data.getClipData() != null && data.getData() == null){
//                                int count = data.getClipData().getItemCount();
//                                int CurrentImageSelect = 0;
//                                while (CurrentImageSelect < count) {
//                                    imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
//                                    getImageUriResult(imageuri);
//
//                                    CurrentImageSelect = CurrentImageSelect + 1;
//                                }
//                        }
                        createPopupDialog();
                        LoadSpinnerData() ;
                        DialogViewAdapter(ImageList);
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_updates);

        USERNO = ""+getApplicationContext().getSharedPreferences("login_info", Context.MODE_PRIVATE).getInt("userno", 0);
        PROJECTNO = ""+getIntent().getIntExtra("projectno", 0);

        Toolbar toolbar = findViewById(R.id.firestore_user_chats_appbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(PROJECTNO);

        postStorageRef = FirebaseStorage.getInstance().getReference();

        Initialize();
        loadUpdateList();

        initializeAdapter();


    }

    private void loadUpdateList() {
        HashMap<String, String> map = new HashMap<>();
        map.put("projectno", PROJECTNO);
        map.put("userno", USERNO);
        AppController.getAppController().getAppNetworkController().makeRequest(Config.Get_User_Project_Updates, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("list_tag", "category response: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("error").equalsIgnoreCase("false")) {


                        JSONArray mStockCategoryArray = object.getJSONObject("data").getJSONArray("image_data");
//                        AppController.getAppController().getInAppNotifier().log("response", "mStockCategoryArray: "+ mStockCategoryArray );
                        Log.e("list_tag", "mStockCategoryArray: " + mStockCategoryArray);

                        for (int i = 0; i < mStockCategoryArray.length(); i++) {
                            JSONObject mCategoryObject = mStockCategoryArray.getJSONObject(i);
                            ProjectUpdateModel aProjectModel = new ProjectUpdateModel();
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
                            mProjectUpdateList.add(aProjectModel);
                        }
                        Log.e("list_tags", "mUpdateProjectList: " + mProjectUpdateList.size());
                        mProjectUpdatesAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPositionWithOffset(mProjectUpdateList.size()-1, 0);
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

    //TODO:: onSentMsgClick
    public void onSentMsgClick() {
        mSentMsg = mImgTextEdit.getText().toString();
        mSpinSelectedNum = mImgTextSpin.getSelectedItemPosition()+1;

        if (ImageList.size() > 0) {
            if(mSentMsg.equalsIgnoreCase("")){
                Toast.makeText(this, "provide msg mandatory...", Toast.LENGTH_SHORT).show();
                return;
            }
            mImgTextProgress.setVisibility(View.VISIBLE);
            mImgTextRelataive.setAlpha((float) 0.1);

            for (int upload_count = 0; upload_count < ImageList.size(); upload_count++) {
                final Uri IndivitualImage = ImageList.get(upload_count);
                final StorageReference realImage = postStorageRef.child("RealImageFolder").child(USERNO).child(IndivitualImage.getLastPathSegment());
                final StorageReference thumbnilImage = postStorageRef.child("ThumbonilImageFolder").child(USERNO).child(IndivitualImage.getLastPathSegment());

                Log.e("camera", " "+ mRealListByte.get(upload_count)) ;
                int finalUpload_count = upload_count;
                realImage.putBytes(mRealListByte.get(upload_count))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                realImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = String.valueOf(uri);
                                        getReallUrl.add(url);

                                        thumbnilImage.putBytes(mThumbListByte.get(finalUpload_count))
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        thumbnilImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String url = String.valueOf(uri);
                                                                getThumbUrl.add(url);
                                                                if (getThumbUrl.size() == ImageList.size()) {

                                                                    UploadImageWithTex(mSentMsg, mSpinSelectedNum, getReallUrl, getThumbUrl);
                                                                }

                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });
                            }
                        });

            }
        }


    }

    private void UploadImageWithTex(String mSentMsg, int mSpinSelectedNum, List<String> getReallUrl, List<String> getThumbUrl) {
        Log.e("list_tag", "mUpdateProjectList size: " + mProjectUpdateList.size());
        HashMap<String, String> map = new HashMap<>();
        map.put("userno", USERNO);
        map.put("projectno", PROJECTNO);
        map.put("image_list", getReallUrl.toString());
        map.put("thumb_list", getThumbUrl.toString());
        map.put("description", mSentMsg);
        map.put("wpno", mSpinSelectedNum+"");


        AppController.getAppController().getAppNetworkController().makeRequest(Config.Get_User_Project_Updates, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("list_tag", "category response: " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("error").equalsIgnoreCase("false")) {

//                        String responseUpdate = object.getString("response") ;
                        String responseUpdate = "Project update is saved!";
                        Toast.makeText(ProjectUpdatesActivity.this, " " + responseUpdate, Toast.LENGTH_SHORT).show();

                        dialog.cancel();
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

    //TODO:: onSelectGalleryImage
    public void onSelectGalleryImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mStartForResult.launch(intent);


    }


    private void DialogViewAdapter(ArrayList<Uri> ImageList) {
        List<String> selectImages = new ArrayList<>();
        for (int i = 0; i <= ImageList.size(); i++) {
            if(i==ImageList.size()){
                selectImages.add("content://");
            }else{
                selectImages.add(ImageList.get(i)+"");
            }

        }

        DialogImageProjectListAdapter productAdapter = new DialogImageProjectListAdapter(getApplicationContext(), selectImages, this);
        mImgTextRecycler.setAdapter(productAdapter);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false);
        mImgTextRecycler.setLayoutManager(manager);

        mCountImgText.setText((1)+"/"+ImageList.size());
        mCountImgText.setVisibility(View.VISIBLE);
        mImgTextRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int p1 = manager.findFirstVisibleItemPosition();
                    int p2 = manager.findLastCompletelyVisibleItemPosition();
                    int p3 = manager.findLastVisibleItemPosition();

                    if(p3==selectImages.size()-1){
                        mCountImgText.setText((p3)+"/"+ImageList.size());
                    }else{
                        mCountImgText.setText((p3+1)+"/"+ImageList.size());
                    }


                }
            }
        });
    }




    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.e("inImage","inImage:"+ inImage ) ;
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "image-"+ImageList.size(), null);
        Log.e("inImage","path:"+ path ) ;

        return Uri.parse(path);
    }

    private void getImageUriResult(Uri imageuri) {
        ImageList.add(imageuri);

        Bitmap bitmapSrc = null;
        try {
            bitmapSrc = MediaStore.Images.Media.getBitmap(ProjectUpdatesActivity.this.getContentResolver(), imageuri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //===============   Real size image
        Bitmap RealSizeBitmap = ImageReSizer.reduceBitmapSize(bitmapSrc, 360000);
        ByteArrayOutputStream realByteBaos = new ByteArrayOutputStream();
        RealSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, realByteBaos);
        mRealByteData = realByteBaos.toByteArray();
        mRealListByte.add(mRealByteData);


        //========  Thumbonil Image
        Bitmap thumbSizeBitmap = ImageReSizer.generateThumb(bitmapSrc, 6500);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        thumbSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
        mThumbByteData = baos2.toByteArray();
        mThumbListByte.add(mThumbByteData);
    }



    private void initializeAdapter() {
        mChatMsgRV.setHasFixedSize(true);
        mProjectUpdatesAdapter = new ProjectUpdatesAdapter(this, mProjectUpdateList);
        mChatMsgRV.setAdapter(mProjectUpdatesAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mChatMsgRV.setLayoutManager(linearLayoutManager);
    }

    private void Initialize() {
        mChatMsgRV = findViewById(R.id.recycler_chatmsg);
        mChatMsgET = findViewById(R.id.edit_msgtext);
        mChatProgressbar = findViewById(R.id.progress_chat_firestore);
        mSelectImageRV = findViewById(R.id.recycler_select_image);
        mRecyclerRelative = findViewById(R.id.relative_recycler);
        mChatMsgRelative = findViewById(R.id.relative_chat_msg);
        mChatSentMsgRelative = findViewById(R.id.relative_chat_msg_sent);
        mSentChatProgressbar = findViewById(R.id.progress_sent_chat_msg);

    }




    //TODO:: onSelectCameraClick
    public void onSelectCameraClick(View view) {
        ImageList.clear();
        mRealListByte.clear();
        mThumbListByte.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        1); // Invokes onRequestPermissionsResult()

                return;
            }
        }


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mStartForResult.launch(takePictureIntent);
    }

    //TODO:: createPopupDialog
    private void createPopupDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_image_text, null);
        mImgTextRelataive = view.findViewById(R.id.relative_img_text) ;
        mImgTextProgress = view.findViewById(R.id.progress_img_text) ;
        mImgTextRecycler = view.findViewById(R.id.recycler_image_text) ;
        mImgTextSpin = view.findViewById(R.id.spinner_image_text) ;
        mImgTextEdit = view.findViewById(R.id.edit_image_text) ;
        mCountImgText = view.findViewById(R.id.text_count_img) ;
        mImgTextBtn = view.findViewById(R.id.btn_image_text) ;


        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        mImgTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSentMsgClick();
            }
        });



    }

    //TODO:: LoadSpinnerData
    private void LoadSpinnerData() {
        mSpinnerDataList.clear();
        HashMap<String, String> map = new HashMap<>();

        AppController.getAppController().getAppNetworkController().makeRequest(Config.Get_Work_Packages_Of_A_Project, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("list_tag", "category response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {


                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject mCategoryObject = jsonArray.getJSONObject(i);
                            mSpinnerDataList.add(mCategoryObject.getString("text")) ;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, mSpinnerDataList);
                        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                        mImgTextSpin.setAdapter(adapter);
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


    @Override
    public void onIntentUrl(String URL) {
        if(URL.equalsIgnoreCase(ImageList.size()+"")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            1); // Invokes onRequestPermissionsResult()

                    return;
                }
            }


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mStartForResult.launch(takePictureIntent);
        }else{

        }
    }
}