package com.geothe.iris;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ramkishorevs on 18/03/18.
 */

public class LicsenseFragment extends Fragment {

    TextView description1, description2,description3,description4,description5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_licsense, container, false);
        description1 = view.findViewById(R.id.description1);
        description2 = view.findViewById(R.id.description2);
        description3 = view.findViewById(R.id.description3);
        description4 = view.findViewById(R.id.description4);
        description5 = view.findViewById(R.id.description5);
        Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
        description1.setTypeface(normal);
        description2.setTypeface(normal);
        description3.setTypeface(normal);
        description4.setTypeface(normal);
        description5.setTypeface(normal);
        return view;
    }
}
