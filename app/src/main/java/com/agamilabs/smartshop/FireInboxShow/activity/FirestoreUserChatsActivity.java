package com.agamilabs.smartshop.FireInboxShow.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.agamilabs.smartshop.FireInboxShow.BatiChatMsgModel;
import com.agamilabs.smartshop.FireInboxShow.Class.ImagePicker1;
import com.agamilabs.smartshop.FireInboxShow.adapter.FireStoreUserChatsAdapter2;
import com.agamilabs.smartshop.FireInboxShow.ImageReSizer;
import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.FireInboxShow.adapter.NestedFirestoreUserChatsAdapter;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FirestoreUserChatsActivity extends AppCompatActivity implements OnIntentUrl {

    public static String mChatUserId, mChatUserName;

    private CollectionReference userChatMsgRef;
    private FirebaseFirestore mFirestoreInstance ;
    private DocumentReference userChatDoc, userChatUnseenMsgDoc ;

    private RelativeLayout mRecyclerRelative,mChatMsgRelative, mChatSentMsgRelative;
    private ProgressBar mChatProgressbar, mSentChatProgressbar ;
    private RecyclerView mChatMsgRV, mSelectImageRV ;
    private EditText mChatMsgET;
    private LinearLayoutManager linearLayoutManager;
    protected FireStoreUserChatsAdapter2 mUserChatsAdapter ;
    private List<BatiChatMsgModel> mChatsMsgList = new ArrayList<>();

    private int firstVisiblesItems, dataExistNum=0;
    private boolean loading = true ;

    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;

    //==========  msg
    private String adminId, mSentMsg ;
    Date date = null;
    //=========   sharedprefarence
    SharedPreferences sharedPreferences;
    String SHARED_PREFS = "admin_store";
    String state = "admin_user_id";

    //   for image
    private Uri imageuri;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();

    byte[] mThumbByteData, mRealByteData;
    private ArrayList<byte[]> mThumbListByte = new ArrayList<byte[]>();
    private ArrayList<byte[]> mRealListByte = new ArrayList<byte[]>();

    private StorageReference postStorageRef ;
    private List<String> getThumbUrl = new ArrayList<String>();
    private List<String> getReallUrl = new ArrayList<String>();

    //FrameLayout
    FrameLayout mFrameLayout, mFragmentFrameLayout;
    ImageView mFrameImageView;
    ImageButton mFrameImgBtn, mCameraBtn ;

    ListenerRegistration mChatMsgListener, loadNextDataListener;


    Uri cameraImageUri ;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    //TODO:: onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_user_chats);

        mChatUserId =  getIntent().getStringExtra("chatID");
        mChatUserName =  getIntent().getStringExtra("chat_name");

        Toolbar toolbar = findViewById(R.id.firestore_user_chats_appbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(mChatUserName);

        mFirestoreInstance = FirebaseFirestore.getInstance();
        userChatMsgRef = mFirestoreInstance.collection("batikrom-message-collection").document("chatMessages").collection(mChatUserId);
        userChatDoc = mFirestoreInstance.collection("batikrom-message-collection").document("chats").collection("chats").document(mChatUserId);
        userChatUnseenMsgDoc = mFirestoreInstance.collection("batikrom-message-collection").document("userChats");
        postStorageRef = FirebaseStorage.getInstance().getReference() ;



        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE) ;
        Initialize() ;


        ImagePicker.setMinQuality(600, 600) ;
    }
    //TODO:: loadChatMsgArrayCollection
    private void loadChatMsgArrayCollection() {
        Query first = userChatMsgRef.orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(15);
        mChatMsgListener = first.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                mChatsMsgList.clear();
                if (e != null) {
                    return;
                }
                dataExistNum = queryDocumentSnapshots.size();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            BatiChatMsgModel chatMsgModel = documentSnapshot.toObject(BatiChatMsgModel.class);
//                            List<String> imageList = (List<String>) documentSnapshot.get("imageList");
//                            Log.e("imageList", imageList.toString()+"  "+ imageList.size() );

//                            mChatsMsgList.add(new BatiChatMsgModel(
//                                    documentSnapshot.getId(),
//                                    chatMsgModel.getMessage(),
//                                    chatMsgModel.getSentBy(),
//                                    chatMsgModel.getSentTime(),
//                                    imageList
//                            ));


                    Map<String, Object> batiChatMsgdata = documentSnapshot.getData();
                    HashMap<String, Object> imageMapList = (HashMap<String, Object>) documentSnapshot.get("imageList");
                    List<String> imageRealList = new ArrayList<>();
                    List<String> imageThumbList = new ArrayList<>();
                    if (imageMapList != null) {
                        imageRealList = (List<String>) imageMapList.get("real");
                        imageThumbList = (List<String>) imageMapList.get("thumb");
                    }

                    Log.e("imageList", imageThumbList.toString()) ;
                    mChatsMsgList.add(new BatiChatMsgModel(
                            documentSnapshot.getId(),
                            batiChatMsgdata.get("message").toString(),
                            batiChatMsgdata.get("sentBy").toString(),
                            batiChatMsgdata.get("sentTime"),
                            imageRealList,
                            imageThumbList
                    ));


                }
                if (queryDocumentSnapshots.size() <= 15) {
                    loading = true;
                    loadScrollViewRV(first);
                }

                arrayListAscToDsc(mChatsMsgList);

                initializeAdapter();
                mUserChatsAdapter.notifyDataSetChanged();
                Log.e("msg_list", "mChatsMsgList size 1:"+ mChatsMsgList.size()  ) ;
                linearLayoutManager.scrollToPositionWithOffset(dataExistNum - 1, 0);
            }
        });


    }

    private void arrayListAscToDsc(List<BatiChatMsgModel> mChatsMsgList) {
        Collections.sort(mChatsMsgList, new Comparator<BatiChatMsgModel>() {
            @Override
            public int compare(BatiChatMsgModel lhs, BatiChatMsgModel rhs) {
                return lhs.getSentTime().toString().compareTo(rhs.getSentTime().toString());
            }
        });
    }

    private void loadScrollViewRV(Query next) {
        mChatMsgRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0){
                    firstVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if(firstVisiblesItems == 0 && loading){
                        loading=false;
                        mChatProgressbar.setVisibility(View.VISIBLE);

                        setAutoRefresh(next);
                    }
                }

            }
        });
    }
    private void setAutoRefresh(Query next){
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;

        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
//                   Log.e("trick", "trick: "+ remainingRefreshTime) ;
                }

                @Override
                public void onFinish() {
                    mChatProgressbar.setVisibility(View.GONE);
                    loadNextFirestoreData(next);
                    cancelAutoRefresh() ;

                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }

    //TODO:: loadNextFirestoreData
    private void loadNextFirestoreData(Query next){
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots.size()<=0){
                            return;
                        }
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        Query next =  userChatMsgRef.orderBy("sentTime", Query.Direction.DESCENDING)
                                .startAfter(lastVisible)
                                .limit(5);



//                        next.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onSuccess(QuerySnapshot documentSnapshots) {
//                                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
//                                            BatiChatMsgModel chatMsgModel = documentSnapshot.toObject(BatiChatMsgModel.class);
//
//                                            dataExistNum = documentSnapshots.size() ;
//                                            mChatsMsgList.add(new BatiChatMsgModel(
//                                                    documentSnapshot.getId(),
//                                                    chatMsgModel.getMessage(),
//                                                    chatMsgModel.getSentBy(),
//                                                    chatMsgModel.getSentTime()
//                                            ));
//
//                                            Log.e("dataExistNum", "dataExistNum out:"+ dataExistNum ) ;
//                                        }
//                                        mUserChatsAdapter.notifyDataSetChanged();
//                                        linearLayoutManager.scrollToPositionWithOffset(dataExistNum-1,  0);
//                                    }
//                                });
//
//                        loading = true ;
//                        loadScrollViewRV(next);


                        //TODO:: loadNextFirestoreData

                        Collections.sort(mChatsMsgList, new Comparator<BatiChatMsgModel>() {
                            @Override
                            public int compare(BatiChatMsgModel lhs, BatiChatMsgModel rhs) {
                                return rhs.getSentTime().toString().compareTo(lhs.getSentTime().toString());
                            }
                        });


                        loadNextDataListener = next.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("TAG", "Listen failed.", e);
                                    return;
                                }

                                dataExistNum = querySnapshot.size();
                                Log.e("dataExistNum", "dataExistNum:" + dataExistNum);
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    Map<String, Object> batiChatMsgdata = documentSnapshot.getData();
                                    HashMap<String, Object> imageMapList = (HashMap<String, Object>) documentSnapshot.get("imageList");
                                    List<String> imageRealList = new ArrayList<>();
                                    List<String> imageThumbList = new ArrayList<>();
                                    if (imageMapList != null) {
                                        imageRealList = (List<String>) imageMapList.get("real");
                                        imageThumbList = (List<String>) imageMapList.get("thumb");
                                    }


                                    mChatsMsgList.add(new BatiChatMsgModel(
                                            documentSnapshot.getId(),
                                            batiChatMsgdata.get("message").toString(),
                                            batiChatMsgdata.get("sentBy").toString(),
                                            batiChatMsgdata.get("sentTime"),
                                            imageRealList,
                                            imageThumbList
                                    ));

                                }

                                arrayListAscToDsc(mChatsMsgList);

                                mUserChatsAdapter.notifyDataSetChanged();
                                Log.e("msg_list", "mChatsMsgList size:" + mChatsMsgList.size());
                                linearLayoutManager.scrollToPositionWithOffset(dataExistNum - 1, 0);

                            }
                        });
                        loading = true ;
                        loadScrollViewRV(next);








                    }
                });
    }







    //TODO:: onSentMsgClick
    public void onSentMsgClick(View view) {

        adminId = sharedPreferences.getString(state, "");
        mSentMsg = mChatMsgET.getText().toString() ;

       getCurrentDateTime();

        Map<String, Object> chatMsgData = new HashMap<>();
        chatMsgData.put("message", mSentMsg);
        chatMsgData.put("sentBy", adminId);
        chatMsgData.put("sentTime", date);

        if(ImageList.size()>0){
        mSentChatProgressbar.setVisibility(View.VISIBLE);
        mChatSentMsgRelative.setAlpha((float) 0.1) ;

        for (int upload_count = 0; upload_count < ImageList.size(); upload_count++) {
            final Uri IndivitualImage = ImageList.get(upload_count);
            final StorageReference realImage = postStorageRef.child("RealImageFolder").child(adminId).child(IndivitualImage.getLastPathSegment()) ;
            final StorageReference thumbnilImage = postStorageRef.child("ThumbonilImageFolder").child(adminId).child(IndivitualImage.getLastPathSegment()) ;

            Map<String, Object> imageListMap = new HashMap<>();
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
                                                            if( getThumbUrl.size() == ImageList.size()  ){
                                                                imageListMap.put("real", getReallUrl);
                                                                imageListMap.put("thumb", getThumbUrl);

                                                                chatMsgData.put("imageList", imageListMap) ;

                                                                userChatMsgRef.document()
                                                                        .set(chatMsgData)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                getThumbUrl.clear();
                                                                                getReallUrl.clear();
                                                                                ImageList.clear();
                                                                                selectImageShowRV(getReallUrl.size());
                                                                                mChatMsgET.setText("");
                                                                                mSentChatProgressbar.setVisibility(View.GONE);
                                                                                mChatSentMsgRelative.setAlpha((float) 1);
                                                                                loadChatMsgArrayCollection();

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    });
                                                }
                                            });
                                }
                            }) ;
                        }
                    }) ;

        }
        }
        else{
            if(!mSentMsg.equalsIgnoreCase("")){
                userChatMsgRef.document()
                        .set(chatMsgData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(FirestoreUserChatsActivity.this, "successful...", Toast.LENGTH_SHORT).show();
                                getThumbUrl.clear();
                                ImageList.clear();
                                selectImageShowRV(getThumbUrl.size());
                                loadChatMsgArrayCollection();
                                mChatMsgET.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        }




    }
    private void selectImageShowRV(int count) {
        if(count>0){
            mSelectImageRV.setVisibility(View.VISIBLE);
//            mChatMsgRelative.getLayoutParams().height = 300;
        }else{
            mSelectImageRV.setVisibility(View.GONE);
//            mChatMsgRelative.getLayoutParams().height = 200;

        }
    }

    private void getCurrentDateTime() {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String timeStamp = dateFormat1.format(new Date()) ;

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            date = dateFormat2.parse(timeStamp);
        } catch (ParseException e){
        }


    }



















    //TODO:: onSelectImageClick
    public void onSelectImageClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);



    }



    //TODO::: onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageList.clear();
        mRealListByte.clear();
        mThumbListByte.clear();

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            if(data.getData()!=null){
                Log.e("req_code","1 data: " + data );
                imageuri=data.getData();
                getImageUriResult(imageuri);
            }
            else{
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int CurrentImageSelect = 0;
                    while (CurrentImageSelect < count) {
                        imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        getImageUriResult(imageuri);

                        CurrentImageSelect = CurrentImageSelect + 1;
                    }


                }
            }
          NestedImageViewAdapter(ImageList );
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            cameraImageUri = getImageUri(imageBitmap) ;
            Log.e("req_code","imageUri: " + cameraImageUri );

            getImageUriResult(cameraImageUri);
            NestedImageViewAdapter(ImageList );

        }

    }

    private void NestedImageViewAdapter(ArrayList<Uri> ImageList) {
        selectImageShowRV(ImageList.size());
        List<String> selectImages = new ArrayList<>() ;
        for(int i=0; i<ImageList.size(); i++){
            selectImages.add(selectImages.size(), String.valueOf(ImageList.get(i)));
        }
        NestedFirestoreUserChatsAdapter productAdapter = new NestedFirestoreUserChatsAdapter(getApplicationContext(), selectImages,selectImages, this);
        mSelectImageRV.setAdapter(productAdapter);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false);
        mSelectImageRV.setLayoutManager(manager);
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void getImageUriResult(Uri imageuri) {
        ImageList.add(imageuri);

        Bitmap bitmapSrc = null;
        try{
            bitmapSrc = MediaStore.Images.Media.getBitmap(FirestoreUserChatsActivity.this.getContentResolver(), imageuri) ;
        } catch (Exception e){
            e.printStackTrace();
        }

        //===============   Real size image
        Bitmap RealSizeBitmap = ImageReSizer.reduceBitmapSize(bitmapSrc, 360000) ;
        Log.e("bitmapSrc", " fullSizeBitmap: "+ RealSizeBitmap ) ;
        ByteArrayOutputStream realByteBaos = new ByteArrayOutputStream();
        RealSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, realByteBaos);
        mRealByteData = realByteBaos.toByteArray();
        Log.e("bitmapSrc", " thumbData: "+ mRealByteData) ;
        mRealListByte.add(mRealByteData);


        //========  Thumbonil Image
        Bitmap thumbSizeBitmap = ImageReSizer.generateThumb(bitmapSrc, 6500) ;
        Log.e("bitmapSrc", " thumbImgBitmap: "+ thumbSizeBitmap ) ;
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        thumbSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
        mThumbByteData = baos2.toByteArray();
        Log.e("bitmapSrc", " ImgthumbData: "+ mThumbByteData) ;
        mThumbListByte.add(mThumbByteData);
    }


    private static void disable(ViewGroup layout, boolean state) {
        layout.setEnabled(state);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child, state);
            } else {
                child.setEnabled(state);
            }
        }
    }

    public void onClearFrameClick(View view) {
        mFrameLayout.setVisibility(View.GONE);
        mRecyclerRelative.setAlpha((float) 1.0);
        disable(mRecyclerRelative, true);
    }



    private void convertUnseenMsgByZero() {
        adminId = sharedPreferences.getString(state, "");
        getCurrentDateTime();

        Map<String, Object> chatUnseenMsgData = new HashMap<>();
        chatUnseenMsgData.put("lastupdatetime", date);
        chatUnseenMsgData.put("unseen_message", 0);

        DocumentReference unseenMsgDocument =userChatUnseenMsgDoc.collection(adminId).document(mChatUserId);
        unseenMsgDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    int unseenCount = Integer.parseInt(documentSnapshot.get("unseen_message") + "");
                    if(unseenCount>0){
                        unseenMsgDocument.set(chatUnseenMsgData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                        Log.e("documentSnapshot", " onSuccess i: "+ unseenCount  ) ;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                Log.e("documentSnapshot", " onFailure: "+ unseenCount  ) ;
                            }
                        }) ;
                    }

                } else {
                    return;
                }
            }
        });



    }
    private void Initialize() {
        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;
        mChatMsgET = findViewById(R.id.edit_msgtext) ;
        mChatProgressbar = findViewById(R.id.progress_chat_firestore) ;
        mSelectImageRV = findViewById(R.id.recycler_select_image) ;
        mRecyclerRelative = findViewById(R.id.relative_recycler) ;
        mChatMsgRelative = findViewById(R.id.relative_chat_msg) ;
        mChatSentMsgRelative = findViewById(R.id.relative_chat_msg_sent) ;
        mSentChatProgressbar = findViewById(R.id.progress_sent_chat_msg) ;
        mFrameLayout = findViewById(R.id.frame_layout) ;
        mFragmentFrameLayout = findViewById(R.id.frame_fragment) ;
        mFrameImageView = findViewById(R.id.frame_img_view) ;
        mFrameImgBtn = findViewById(R.id.frame_img_btn) ;
    }
    private void initializeAdapter() {
        mChatMsgRV.setHasFixedSize(true);
        mUserChatsAdapter = new FireStoreUserChatsAdapter2(getApplicationContext(), mChatsMsgList);
        mChatMsgRV.setAdapter(mUserChatsAdapter);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
//        linearLayoutManager.setStackFromEnd(true);
        mChatMsgRV.setLayoutManager(linearLayoutManager);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));
//
//    }

    private void loadBatiChatsCollection() {
        userChatDoc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                List<String> usersList = (List<String>) documentSnapshot.get("users");
                List<String> userId = new ArrayList<>() ;
                AppController.getAppController().getInAppNotifier().log("checking", " id: " + documentSnapshot.getId());
                for (int j = 0; j < usersList.size(); j++) {
                    if(!usersList.get(j).equalsIgnoreCase(adminId)){
                        userId.add(usersList.get(j));
                    }
                }
                loadUserChatsCollection(userId);

            }
        });


    }
    private void loadUserChatsCollection(List<String> userIdList) {
        for(int i=0; i<userIdList.size(); i++ ) {
            String userId = userIdList.get(i);
            userChatUnseenMsgDoc.collection(userId).document(mChatUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        int unseenCount = Integer.parseInt(documentSnapshot.get("unseen_message") + "");
                        updateUnseenMsgDocument(userId, unseenCount);
                    } else {
                        updateUnseenMsgDocument(userId, 0);
                    }
                }
            });
        }
    }
    private void updateUnseenMsgDocument(String userId,int unseenCount) {
        Map<String, Object> chatUnseenMsgData = new HashMap<>();
        chatUnseenMsgData.put("lastupdatetime", date);
        chatUnseenMsgData.put("unseen_message", unseenCount+1);
        userChatUnseenMsgDoc.collection(userId).document(mChatUserId) ;
    }


    //TODO::  onResume
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state", "onResume    2") ;

        loadChatMsgArrayCollection() ;
        initializeAdapter();


        convertUnseenMsgByZero();
    }
    @Override
    protected void onPause() {
        super.onPause();


        if(mChatMsgListener != null){
            mChatMsgListener.remove();
        }

        if(loadNextDataListener != null){
            loadNextDataListener.remove();
        }
    }

    //TODO::onIntentUrl
    @Override
    public void onIntentUrl(String URL) {

        mFrameLayout.setVisibility(View.VISIBLE);
//        mRecyclerRelative.setAlpha((float) 0.1);
        disable(mRecyclerRelative, false);

        AppImageLoader.loadImageInView(URL, R.drawable.profile_image, (ImageView) mFrameImageView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);






    }


    //TODO:: onSelectCameraClick
    public void onSelectCameraClick(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }



    }
}