package com.geothe.iris.network;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.geothe.iris.AppController;
import com.geothe.iris.models.ArticleList;
import com.geothe.iris.models.StyleDataset;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramkishorevs on 24/02/18.
 */

public class ConnectAPI {
    ServerAuthenticateListener serverAuthenticateListener;

    int socketTimeout = 900000000;
    public static final int ARTICLE_LIST_CODE = 1;
    public static final int STYLE_IMAGES_LIST_CODE =2;

    public ConnectAPI() {

    }

    public void getArticleList(final String text) {
        Log.v("ress", "check1");
        if (serverAuthenticateListener!=null) {
            Log.v("ress", "check2");
            serverAuthenticateListener.onRequestInitiated(ARTICLE_LIST_CODE);

            StringRequest postRequest = new StringRequest(Request.Method.POST, APIConstants.ARTICLES_LIST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("ress", response);
                            Gson gson=new Gson();
                            ArticleList articleList=gson.fromJson(response,ArticleList.class);
                            serverAuthenticateListener.onRequestCompleted(ARTICLE_LIST_CODE,articleList);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("ress", error.getMessage());
                    serverAuthenticateListener.onRequestError(ARTICLE_LIST_CODE, ErrorDefenitions.ERROR_RESPONSE_INVALID);

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("text", text);
                    return params;
                }
            };

            // Adding request to request queue
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(postRequest);
        }
    }

    public void getAllSyleImages() {
        if (serverAuthenticateListener != null) {
            serverAuthenticateListener.onRequestInitiated(STYLE_IMAGES_LIST_CODE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConstants.STYLE_IMAGES_DATASET_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v("ress", response);
                    Gson gson=new Gson();
                    StyleDataset styleDataset=gson.fromJson(response,StyleDataset.class);
                    serverAuthenticateListener.onRequestCompleted(STYLE_IMAGES_LIST_CODE,styleDataset);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    serverAuthenticateListener.onRequestError(STYLE_IMAGES_LIST_CODE,ErrorDefenitions.ERROR_RESPONSE_INVALID);
                }
            });
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }


    public void setServerAuthenticateListener(ServerAuthenticateListener listener) {
        this.serverAuthenticateListener = listener;
    }

    public interface ServerAuthenticateListener {

        /**
         * Called when the network request starts.
         *
         * @param code Event code which specifies, call to which API has been made.
         */
        void onRequestInitiated(int code);

        /**
         * Called when the request is successfully completed and returns the validated response.
         *
         * @param code   Event code which specifies, call to which API has been made.
         * @param result Result Object which needs to be casted to specific class as required
         */
        void onRequestCompleted(int code, Object result);

        /**
         * Called when unexpected error occurs.
         *
         * @param code    Event code which specifies, call to which API has been made.
         * @param message Error description
         */
        void onRequestError(int code, String message);

    }
}
