package com.geothe.iris.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ramkishorevs on 15/03/18.
 */

public class StyleDataset {
    public List<Images> images;

    public class Images {

        @SerializedName("Title")
        public String title;

        @SerializedName("Database ID")
        public String databaseID;

        @SerializedName("Link")
        public String image;
    }
}
