package com.geothe.iris.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.geothe.iris.R;
import com.wang.avi.AVLoadingIndicatorView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by ramkishorevs on 16/03/18.
 */

public class ContentImageActivity extends AppCompatActivity {

    private SimpleDraweeView imageView;
    String selectedImagePath;
    String databaseId;
    AVLoadingIndicatorView spinKitView;
    TextView content;

    private static final int CAMERA_CODE = 0;
    private static final int EXTERNAL_CODE=1;

    String URL = "http://139.59.87.132:9001";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_content_image);
        Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
        content = findViewById(R.id.holder_text);
        content.setTypeface(heading);
        databaseId = getIntent().getExtras().getString("databaseId");
        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.hide();

        this.imageView = (SimpleDraweeView)this.findViewById(R.id.upload_image);
        imageView.setImageResource(R.drawable.photo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getStoragePermission();
            }
        });


    }


    private void getStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    EXTERNAL_CODE);
        } else {
            new Picker.Builder(ContentImageActivity.this,new MyPickListener(),R.style.MIP_theme).setLimit(1)
                    .build()
                    .startActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {


            case EXTERNAL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    new Picker.Builder(ContentImageActivity.this,new MyPickListener(),R.style.MIP_theme).setLimit(1)
                            .build()
                            .startActivity();
                }
                break;
        }
    }

    private class MyPickListener implements Picker.PickListener {

        @Override
        public void onPickedSuccessfully(final ArrayList<ImageEntry> images) {
            selectedImagePath = images.get(0).path;
            imageView.setVisibility(View.GONE);
            spinKitView.show();
            content.setText("Uploading please wait..");
            upload();
        }

        @Override
        public void onCancel() {
            //User canceled the pick activity
        }
    }


    private void upload()
    {
        String uploadId = "1";


        try {

            MultipartUploadRequest multipart = new MultipartUploadRequest(ContentImageActivity.this,URL+"/upload");

            multipart.addFileToUpload(selectedImagePath,"file");
            multipart.addParameter("style_id", databaseId);

            multipart.setNotificationConfig(new UploadNotificationConfig());
            multipart.setDelegate(new UploadStatusDelegate() {
                @Override
                public void onProgress(UploadInfo uploadInfo) {
                    Log.v("pok","pok");
                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {

                    Log.v("error",exception.getMessage());
                    Log.v("perror","perr");
                    spinKitView.hide();
                    content.setText("Couldn't connect please try again..");

                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                    Log.v("pover","over");
                    Log.v("response",serverResponse.getBodyAsString());
                    spinKitView.hide();
                    String result = null;
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                        result = jsonObject.getString("style_image");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(URL+result);
                    content.setText("Here you go!");
                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {

                    Log.v("pcancel","pcancel");
                    spinKitView.hide();
                }
            });
            multipart.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
