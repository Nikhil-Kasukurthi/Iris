package com.geothe.iris.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ramkishorevs on 24/02/18.
 */

public class ArticleList {

    @SerializedName("Possible Articles")
    public List<PossibleArticles> possibleArticles;

    public class PossibleArticles {

        public String image;

        @SerializedName("article_id")
        public String article_id;

        public String summary;
    }
}
