package com.geothe.iris;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by ramkishorevs on 26/02/18.
 */

public class DashBoardFragment extends Fragment {

    VideoView videoView;
    TextView holder_text,description,styleTransfer,diveIn;
    CardView styleHodler,diveInHolder;
    android.support.v7.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
        videoView =(VideoView)view.findViewById(R.id.videoView);
        videoView.setClickable(false);

        styleTransfer = view.findViewById(R.id.style_transfer_btn);
        diveIn = view.findViewById(R.id.dive_in_btn);
        description = view.findViewById(R.id.description);
        holder_text = view.findViewById(R.id.holder_text);
        toolbar = getActivity().findViewById(R.id.toolbar);

        styleHodler = view.findViewById(R.id.style_transfer_holder);
        diveInHolder = view.findViewById(R.id.dive_in_holder);

        styleTransfer.setTypeface(normal);

        diveIn.setTypeface(normal);

        holder_text.setTypeface(heading);
        description.setTypeface(normal);

        //specify the location of media file
        String uri = "android.resource://" + "com.geothe.iris" + "/" + R.raw.logoplay;

        //Setting MediaController and URI, then starting the videoView
        videoView.setVideoURI(Uri.parse(uri));
        videoView.setMediaController(null);
        videoView.requestFocus();

        videoView.seekTo(2);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

        styleHodler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new StyleTransferFragment();
                toolbar.setTitle("Style Transfer");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.root, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        diveInHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PictureDescriptionFragment();
                toolbar.setTitle("Dive In");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.root, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
