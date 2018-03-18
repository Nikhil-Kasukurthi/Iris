package com.geothe.iris.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.geothe.iris.R;

/**
 * Created by ramkishorevs on 26/02/18.
 *
 */

public class ArticleDetailActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    TextView description;

   public static void start(Context context, String text, String description) {
        Intent starter = new Intent(context, ArticleDetailActivity.class);
        starter.putExtra("text",text);
        starter.putExtra("description", description);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Typeface normal = Typeface.createFromAsset(getResources().getAssets(), "Poppins-Regular.ttf");
        toolbar = findViewById(R.id.toolbar);
        description = findViewById(R.id.description);
        description.setTypeface(normal);
        setdata();
    }

    private void setdata() {
        toolbar.setTitle(getIntent().getExtras().getString("text"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        description.setText(getIntent().getExtras().getString("description"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
