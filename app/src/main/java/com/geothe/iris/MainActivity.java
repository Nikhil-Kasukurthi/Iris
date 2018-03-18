package com.geothe.iris;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.Color;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    SlidingRootNav slidingRootNav;
    TextView name,welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        Typeface heading = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Bold.ttf");
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();
        toolbar.setTitle("Dashboard");
        toolbar.setTitleTextColor(android.graphics.Color.BLACK);
        toolbar.setTitleMarginTop(45);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(heading);
                    break;
                }
            }
        }
        name = findViewById(R.id.name);
        welcome = findViewById(R.id.welcome);
        name.setTypeface(normal);
        welcome.setTypeface(heading);
        Button dreamViewer = (Button) findViewById(R.id.dream_viewer);
        dreamViewer.setTypeface(normal);
        dreamViewer.setOnClickListener(this);
        Button dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(this);
        dashboard.setTypeface(normal);
        Button styleTransfer = findViewById(R.id.style_transfer);
        styleTransfer.setTypeface(normal);
        styleTransfer.setOnClickListener(this);
        Button license = findViewById(R.id.license);
        license.setOnClickListener(this);
        license.setTypeface(normal);
        addFragment(new DashBoardFragment());

    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        slidingRootNav.closeMenu(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dream_viewer) {
            toolbar.setTitle("Dream Viewer");
            toolbar.setTitleTextColor(android.graphics.Color.BLACK);
            showFragment(new PictureDescriptionFragment());
        } else if (view.getId() == R.id.style_transfer) {
            toolbar.setTitle("Style Transfer");
            toolbar.setTitleTextColor(android.graphics.Color.BLACK);
            showFragment(new StyleTransferFragment());
        } else if (view.getId() == R.id.dashboard) {
            toolbar.setTitle("Dashboard");
            toolbar.setTitleTextColor(android.graphics.Color.BLACK);
            showFragment(new DashBoardFragment());
        } else if (view.getId() == R.id.license) {
            toolbar.setTitle("License");
            toolbar.setTitleTextColor(android.graphics.Color.BLACK);
            showFragment(new LicsenseFragment());
        }
    }
}
