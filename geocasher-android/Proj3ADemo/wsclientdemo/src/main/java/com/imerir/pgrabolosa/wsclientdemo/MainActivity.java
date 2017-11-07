package com.imerir.pgrabolosa.wsclientdemo;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    RequestQueue mRequestQueue;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestQueue = Volley.newRequestQueue(this);

        mEditText = (EditText)findViewById(R.id.editText);
        Button btnGET = (Button)findViewById(R.id.getButton);
        Button btnPOST = (Button)findViewById(R.id.postButton);

        btnGET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoGetHttpRequest();
            }
        });

        btnPOST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoPostHttpRequest();
            }
        });
    }

    void demoGetHttpRequest() {
        String endpointUrl ="https://perso.imerir.com/pgrabolosa/2016/ducks/";

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String message = "";
                try {
                    JSONArray ducks = response.getJSONArray("images");
                    message = String.format("Il y a %d canards", ducks.length());

                    if (ducks.length() > 0) {
                        message += " et le 1er s'appelle " + ducks.getJSONObject(0).getString("name");
                    }

                } catch (Exception e) {
                    message = "Erreur de lecture du JSON";
                } finally {
                    mEditText.setText(message);
                }
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mEditText.setText("Erreur lors de la requête");
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, endpointUrl, null, onSuccess, onError);

        mRequestQueue.add(request);
    }

    void demoPostHttpRequest() {
        String endpointUrl ="https://perso.imerir.com/pgrabolosa/2017/add/";

        JSONObject postData = new JSONObject();
        try {
            postData.put("a", 5);
            postData.put("b", 10);
        } catch (Exception e) {
            // do nothing
        }

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String message = "";
                try {
                    int sum = response.getInt("sum");
                    Toast toast = Toast.makeText(MainActivity.this, "Sum = " + sum, Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(MainActivity.this, "Erreur de lecture du JSON", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mEditText.setText("Erreur lors de la requête");
            }
        };



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpointUrl, postData, onSuccess, onError);

        mRequestQueue.add(request);
    }
}
