package com.agamilabs.smartshop.CameraWork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.agamilabs.smartshop.R;
import com.squareup.picasso.Picasso;

public class DisplayImageActivity extends AppCompatActivity {

    ImageView mDisplayImage ;
    ImageButton mConfirmImgBtn, mCancelImgBtn;

    String imageUri, image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        mDisplayImage = findViewById(R.id.image_display) ;
        mConfirmImgBtn = findViewById(R.id.imgbtn_confirm) ;
        mCancelImgBtn = findViewById(R.id.imgbtn_cancel) ;

        imageUri = getIntent().getStringExtra("imageuri") ;
        image = getIntent().getStringExtra("image") ;

        Log.e("img", "imageUri: "+ imageUri ) ;
        Log.e("img", "image: "+ image ) ;




        Picasso.get().load(imageUri)
                .placeholder(R.drawable.camera_add)
                .into(mDisplayImage);

        mConfirmImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CameraActivity.get
            }
        });

    }
}