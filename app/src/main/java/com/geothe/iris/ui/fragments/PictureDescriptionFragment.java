package com.geothe.iris.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.geothe.iris.ui.activities.ArticleDetailActivity;
import com.geothe.iris.ImageUtils;
import com.geothe.iris.R;
import com.geothe.iris.models.ArticleList;
import com.geothe.iris.network.ConnectAPI;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class PictureDescriptionFragment extends Fragment implements ConnectAPI.ServerAuthenticateListener {
    private final static int CAMERA_RQ = 6969;
    String mCurrentPhotoPath;
    BatchAnnotateImagesResponse batchResponse;
    ConnectAPI connectAPI;
    ArticleList articleList;
    RecyclerView rootArticleRecyclerview;
    AVLoadingIndicatorView spinKitView;
    ImageUtils utils;
    ImageView eyeView;
    Animation zoomout;
    Button dreamStartBtn;
    TextView holderText,description;
    private static final int CAMERA_CODE = 0;
    private static final int EXTERNAL_CODE=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_description, container, false);
        Fresco.initialize(getActivity());
        Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
        rootArticleRecyclerview = view.findViewById(R.id.posts_recycler_view);
        spinKitView = view.findViewById(R.id.spin_kit);
        dreamStartBtn = view.findViewById(R.id.dream_start_btn);
        spinKitView.hide();
        eyeView = view.findViewById(R.id.eye_start);
        holderText = view.findViewById(R.id.holder_text);
        holderText.setTypeface(heading);
        description = view.findViewById(R.id.description);
        description.setTypeface(normal);
        zoomout = AnimationUtils.loadAnimation(getActivity(), R.anim.zoomout);
        eyeView.setAnimation(zoomout);


        zoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
             //   eyeView.startAnimation(zoomout);

            }
        });

        rootArticleRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectAPI = new ConnectAPI();
        connectAPI.setServerAuthenticateListener(this);
        utils = new ImageUtils();
        dreamStartBtn.setTypeface(normal);
        dreamStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStoragePermission();
            }
        });

        return view;
    }



    private void getStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    EXTERNAL_CODE);
        }else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.fillInStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.geothe.iris",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_RQ);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case EXTERNAL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            ex.fillInStackTrace();
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                    "com.geothe.iris",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, CAMERA_RQ);
                        }
                    } else {
                            Toast.makeText(getActivity(), "Sorry this is a required permission",Toast.LENGTH_LONG).show();
                    }

                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_RQ) {
            Log.v("okk", "okk");
            Bitmap path = utils.decodeSampledBitmapFromResource(mCurrentPhotoPath,244,244);
            if (path != null) {
                eyeView.clearAnimation();
                eyeView.setVisibility(View.GONE);
                dreamStartBtn.setVisibility(View.GONE);
                holderText.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                spinKitView.show();
                Vision.Builder visionBuilder = new Vision.Builder(
                        new NetHttpTransport(),
                        new AndroidJsonFactory(),
                        null);

                visionBuilder.setVisionRequestInitializer(
                        new VisionRequestInitializer("AIzaSyA7Kso79pERJufhyrB24Q1MhbuPYgeuU20"));

                final Vision vision = visionBuilder.build();


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                path.compress(Bitmap.CompressFormat.PNG, 70 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                eyeView.setVisibility(View.GONE);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eyeView.setVisibility(View.GONE);
                                }
                            });
                            InputStream inputStream = bs;
                            byte[] photoData = IOUtils.toByteArray(inputStream);
                            Image inputImage = new Image();
                            inputImage.encodeContent(photoData);
                            Feature desiredFeature = new Feature();
                            desiredFeature.setType("TEXT_DETECTION");
                            AnnotateImageRequest request = new AnnotateImageRequest();
                            request.setImage(inputImage);
                            request.setFeatures(Arrays.asList(desiredFeature));
                            BatchAnnotateImagesRequest batchRequest =
                                    new BatchAnnotateImagesRequest();

                            batchRequest.setRequests(Arrays.asList(request));

                            batchResponse = vision.images().annotate(batchRequest).execute();

                            final List<EntityAnnotation> text = batchResponse.getResponses()
                                    .get(0).getTextAnnotations();
                            try {
                                String lines[] = text.get(0).getDescription().split("\\r?\\n");

                                Log.v("ress", lines[0]);
                                //connectAPI.getArticleList(text.get(0).getDescription().trim());

                                connectAPI.getArticleList(lines[0].trim());
                            } catch (Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinKitView.hide();
                                        eyeView.setVisibility(View.VISIBLE);
                                        dreamStartBtn.setVisibility(View.VISIBLE);
                                        holderText.setVisibility(View.VISIBLE);
                                        description.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "Something went wrong please try again..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    spinKitView.hide();
                                    eyeView.setVisibility(View.VISIBLE);
                                    dreamStartBtn.setVisibility(View.VISIBLE);
                                    holderText.setVisibility(View.VISIBLE);
                                    description.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "Something went wrong please try again..", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                });
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onRequestInitiated(int code) {

    }

    @Override
    public void onRequestCompleted(int code, Object result) {
        if (code == ConnectAPI.ARTICLE_LIST_CODE) {
            articleList = (ArticleList) result;
            spinKitView.hide();
            if (articleList != null && articleList.possibleArticles != null && !articleList.possibleArticles.isEmpty()) {
                rootArticleRecyclerview.setAdapter(new RootInfoAdapter());
            } else {
                eyeView.setVisibility(View.VISIBLE);
                dreamStartBtn.setVisibility(View.VISIBLE);
                holderText.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Something went wrong please try again..",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onRequestError(int code, String message) {
        spinKitView.hide();
        eyeView.setVisibility(View.VISIBLE);
        dreamStartBtn.setVisibility(View.VISIBLE);
        holderText.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Something went wrong please try again..",Toast.LENGTH_SHORT).show();
    }

    private class RootInfoAdapter extends RecyclerView.Adapter<RootInfoviewHolder> {
        @Override
        public RootInfoviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_row, parent, false);
            RootInfoviewHolder rootInfoviewHolder = new RootInfoviewHolder(view);
            return rootInfoviewHolder;

        }

        @Override
        public void onBindViewHolder(RootInfoviewHolder holder, final int position) {
            holder.articleName.setText(articleList.possibleArticles.get(position).article_id);
            holder.image.setImageURI(Uri.parse(articleList.possibleArticles.get(position).image));
            holder.description.setText(articleList.possibleArticles.get(position).summary);
            holder.article_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArticleDetailActivity.start(getActivity(),articleList.possibleArticles.get(position).article_id,
                            articleList.possibleArticles.get(position).summary);
                }
            });
        }

        @Override
        public int getItemCount() {
            return articleList.possibleArticles.size();
        }

    }

    private class RootInfoviewHolder extends RecyclerView.ViewHolder {

        TextView articleName,description;
        ImageView image;
        CardView article_container;

        public RootInfoviewHolder(View itemView) {
            super(itemView);
            this.articleName = (TextView) itemView.findViewById(R.id.article_title);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            this.article_container = (CardView) itemView.findViewById(R.id.article_container);
            Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
            Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
            articleName.setTypeface(heading);
            description.setTypeface(normal);
        }
    }
}
