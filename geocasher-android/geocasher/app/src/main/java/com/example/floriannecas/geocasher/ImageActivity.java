package com.example.floriannecas.geocasher;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnChoose, btnUpload;
    private ProgressBar progressBar;

    public static String BASE_URL = "https://polar-bayou-90643.herokuapp.com/postimage";
    static final int PICK_IMAGE_REQUEST = 1;
    String filePath;
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        mRequestQueue = Volley.newRequestQueue(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnChoose = (Button) findViewById(R.id.button_choose);
        btnUpload = (Button) findViewById(R.id.button_upload);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath != null) {
                    imageUpload(filePath);
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                imageView.setImageURI(picUri);

            }

        }

    }

    private void imageUpload(final String imagePath) {

        int h = 200; // height in pixels
        int w = 200; // width in pixels
        Bitmap scaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagePath), w, h, true);
        demoPostHttpRequest(getStringImage(scaled));

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    void demoGetHttpRequest() {

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
                    Log.e("Message Recu :", message);
                }
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Message Recu :","Erreur lors de la requête");
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BASE_URL, null, onSuccess, onError);

        mRequestQueue.add(request);
    }

    void demoPostHttpRequest(final String image64) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("image", image64);
        } catch (Exception e) {
            // do nothing
        }

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String message = "";
                try {
                    String res = response.getString("response");
                    Toast toast = Toast.makeText(ImageActivity.this, res, Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(ImageActivity.this, "Erreur de lecture du JSON", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Message Recu :","Erreur lors de la requête");
            }
        };



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, postData, onSuccess, onError);

        //Log.e("Test", postData.toString());
        mRequestQueue.add(request);
    }

}