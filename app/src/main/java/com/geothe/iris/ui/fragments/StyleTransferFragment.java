package com.geothe.iris.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.geothe.iris.ui.activities.ContentImageActivity;
import com.geothe.iris.R;
import com.geothe.iris.models.StyleDataset;
import com.geothe.iris.network.ConnectAPI;

import link.fls.swipestack.SwipeStack;

/**
 * Created by ramkishorevs on 13/03/18.
 */

public class StyleTransferFragment extends Fragment implements ConnectAPI.ServerAuthenticateListener, SwipeStack.SwipeStackListener {

    SwipeStack swipeStack;
    ConnectAPI connectAPI;
    StyleDataset styleDataset;
    SwipeStackAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(getActivity());
        View view = inflater.inflate(R.layout.fragment_style_transfer, container, false);
        init(view);
        setInit();
        return view;
    }

    private void init(View view) {
        swipeStack = view.findViewById(R.id.swipeStack);
        connectAPI = new ConnectAPI();
    }

    private void setInit() {
        swipeStack.setListener(this);
        connectAPI.setServerAuthenticateListener(this);
        connectAPI.getAllSyleImages();
    }

    @Override
    public void onRequestInitiated(int code) {

    }

    @Override
    public void onRequestCompleted(int code, Object result) {
        if (code == ConnectAPI.STYLE_IMAGES_LIST_CODE) {
            styleDataset = (StyleDataset) result;
            Log.v("check1",styleDataset.images.get(1).title);
            adapter = new SwipeStackAdapter();
            swipeStack.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestError(int code, String message) {

    }

    @Override
    public void onViewSwipedToLeft(int position) {

    }

    @Override
    public void onViewSwipedToRight(int position) {

    }

    @Override
    public void onStackEmpty() {
        swipeStack.resetStack();
        adapter.notifyDataSetChanged();
    }

    public class SwipeStackAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return styleDataset.images.size();
        }

        @Override
        public StyleDataset.Images getItem(int i) {
            return styleDataset.images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
            Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
            Log.v("called","called");
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_style_images_row, viewGroup, false);
            SimpleDraweeView image = view.findViewById(R.id.image);
            TextView title = view.findViewById(R.id.title);
            title.setTypeface(heading);
            //image.setImageURI(Uri.parse("https://lh4.googleusercontent.com/SukmPtcdfNzX4WsV0agDa-NDbSfHdEFOztLK2i54KtueImfcPF9DMV02ivW3pIQ8SErCaykK8-5ZWQ=w2560-h1452-rw"));
            image.setImageURI(Uri.parse(styleDataset.images.get(i).image));
            title.setText(styleDataset.images.get(i).title);
            android.support.design.widget.FloatingActionButton useImage = view.findViewById(R.id.use_image);
            useImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ContentImageActivity.class);
                    intent.putExtra("databaseId",styleDataset.images.get(i).databaseID);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}