package com.example.floriannecas.geocasher;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.example.floriannecas.geocasher.MainActivity.MyPREFERENCES;


public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnChoose, btnUpload;

    public static String BASE_URL = "http://172.30.0.147:5000/postimage";
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
                    btnUpload.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Sending ...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }

            }
        });

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 120000);
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
        String lat = "";
        String longi = "";

        try {
            final ExifInterface exifInterface = new ExifInterface(filePath);
            lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            longi = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

        } catch (IOException e) {
            Log.e("ERROR", "Couldn't read exif info: " + e.getLocalizedMessage());
        }
        Bitmap scaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagePath), w, h, true);
        httpPostImage(getStringImage(scaled),lat, longi);

        btnUpload.setEnabled(true);
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

    void httpPostImage(final String image64, String lat, String longi) {

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("name", null);

        JSONObject postData = new JSONObject();
        try {
            postData.put("nom_equipe", restoredText);
            postData.put("lat", lat);
            postData.put("long", longi);
            postData.put("image", image64);
        } catch (Exception e) {
            // do nothing
        }

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast toast = Toast.makeText(ImageActivity.this, "Photo analysée ! Vérifiez votre liste !", Toast.LENGTH_LONG);
                    toast.show();
                    btnUpload.setEnabled(true);
                } catch (Exception e) {
                    Toast toast = Toast.makeText(ImageActivity.this, "Erreur de lecture de l'image", Toast.LENGTH_LONG);
                    toast.show();
                    btnUpload.setEnabled(true);
                }
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(ImageActivity.this, "Erreur lors de l'envoi de l'image", Toast.LENGTH_LONG);
                toast.show();
                Log.e("Message Recu :","Erreur lors de la requête");
                btnUpload.setEnabled(true);
            }
        };



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, postData, onSuccess, onError);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Log.e("Test", postData.toString());
        mRequestQueue.add(request);
    }

}