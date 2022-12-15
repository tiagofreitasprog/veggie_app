package com.example.veggie;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Api {
    public Context getCtx() {
        return ctx;
    }
    private Boolean validado;
    private final Context ctx;
    private String response;
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }

    public Api(String url,Context ctx) {
        this.url = url;
        this.ctx = ctx;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;
    public boolean request(Context ctx){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resposta) {
                        // Display the first 500 characters of the response string.
                        Log.d("API request:", resposta);
                        setResponse(resposta);
                        validado = true;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setResponse(response);
                validado = false;
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return validado;
    }
    public boolean postDataUsingVolley(String url) {
        // url to post our data
        final Boolean[] validado = {true};

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            Boolean validado = true;
            @Override
            public void onResponse(String response) {
                Log.d("Resposta",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Resposta",response);
            }
        });
        // below line is to make
        // a json object request.
        queue.add(request);
        return true;
    }
}
