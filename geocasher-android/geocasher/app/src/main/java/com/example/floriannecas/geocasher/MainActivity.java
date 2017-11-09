package com.example.floriannecas.geocasher;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.altbeacon.beacon.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.RemoteException;
import android.support.v7.app.AlertDialog;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements BeaconConsumer{
    private static final int REQUEST_GPS = 1;
    static final int REQUEST_PERMCAM = 1;
    public static final String MyPREFERENCES = "MyPrefs" ;
    int PERMISSION_ALL = 1;
    String BASE_URL = "https://polar-bayou-90643.herokuapp.com/";

    static final String TAG = "Geocasher";

    private BeaconManager beaconManager;
    RequestQueue mRequestQueue;
    ArrayList<HashMap<String, String>> objectList;

    // View Components
    TextView mQrCodeLabel;
    TextView mBeaconLabel;
    Button mbtnSelectImage;
    Button mbtnSignIn;
    EditText mNameEditor;
    ListView mList;

    // Model Components
    BarcodeDetector mDetector;
    CameraSource mCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectList = new ArrayList<>();

        mList = (ListView) findViewById(R.id.listView);
        mQrCodeLabel = (TextView) findViewById(R.id.QrCodeLabel);
        mBeaconLabel = (TextView) findViewById(R.id.BeaconLabel);
        mbtnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        mbtnSignIn = (Button) findViewById(R.id.btnSignIn);
        mNameEditor = (EditText) findViewById(R.id.nameEditor);


        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("name", null);
        if (restoredText != null) {
            Log.e(TAG, restoredText);
            mNameEditor.setText(restoredText);
            //mbtnSignIn.setEnabled(false);
            //mNameEditor.setEnabled(false);
        }

        if (checkCameraPermissions()) {
            setupDetectorAndCamera();
        } else {
            // will be setup in "onRequestPermissionsResult"
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showAlertDialog("This app needs location access",
                    "Please grant location access so this app can detect beacons.", new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_GPS);
                        }
                    });
        }


        mbtnSelectImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageActivity();
            }
        });

        mbtnSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUser(mNameEditor.getText().toString());
            }
        });


        /*button2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoPostHttpRequest();
            }
        });*/

        mRequestQueue = Volley.newRequestQueue(this);

    }

    /************************* Screen interact *************************/
    void displayText(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQrCodeLabel.setText(message);
            }
        });
    }

    void displayBeacon(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBeaconLabel.setText(message);
            }
        });
    }

    void changeBtnImageState(final boolean state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mbtnSelectImage.setEnabled(state);
            }
        });
    }

    /************************* Activity & Permissions *************************/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMCAM) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupDetectorAndCamera(); // continue the setup
            } else {
                Log.e(TAG, "Permission was denied or request was cancelled.");
                displayText("Access to the camera is REQUIRED.");
            }
        }


        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /************************* CAMERA *************************/
    boolean checkCameraPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMCAM);
            return false;
        } else {
            return true;
        }
    }

    void setupDetectorAndCamera() {
        // build the detector
        mDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        mDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                processBarcodes(barcodes);
            }
        });

        if(!mDetector.isOperational()) {
            mQrCodeLabel.setText("Could not set up the detector!\nPlease update or upgrade your tablet (consider an iPhone X).");
            return;
        }

        // build the camera
        CameraSource.Builder cameraBuilder = new CameraSource.Builder(this, mDetector);
        cameraBuilder.setAutoFocusEnabled(true);
        cameraBuilder.setFacing(CameraSource.CAMERA_FACING_BACK);
        cameraBuilder.setRequestedFps(10);

        mCamera = cameraBuilder.build();

        // start the camera
        try {
            mCamera.start();
        } catch (SecurityException e) { // camera not allowed
            Log.e(TAG, e.getMessage());
            mQrCodeLabel.setText("Failed to start the camera");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            mQrCodeLabel.setText("Failed to start the camera");
        }
    }

    void processBarcodes(SparseArray<Barcode> barcodes) {
        if (barcodes.size() != 0) {
            displayText(barcodes.valueAt(0).displayValue);
        } else {
            displayText("No barcode detected.");
        }
    }

    /************************* BEACONS *************************/
    @Override
    protected void onResume() {
        super.onResume();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        beaconManager.bind(this);
    }

    @Override
    protected void onPause() {
        beaconManager.unbind(this);
        super.onPause();
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "beaconServiceConnected");

        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for(Beacon beacon : collection) {
                    //displayBeacon(Double.toString(beacon.getDistance()));
                    //Log.i(TAG, "Detected beacon : " + beacon.getId1());
                    //Log.i(TAG, "Detected beacon @ distance " + beacon.getDistance());
                    displayBeacon("Beacon distance : " + Double.toString(beacon.getDistance()));
                    changeBtnImageState(true);
                }
            }
        });

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                //Log.i(TAG, "didEnterRegion");
            }

            @Override
            public void didExitRegion(Region region) {
                //Log.i(TAG, "didExitRegion");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                //Log.i(TAG, "didDetermineStateForRegion = " + i);
            }
        });

        try {
            Region region = new Region("Flo", null, null, null);
            beaconManager.startMonitoringBeaconsInRegion(region);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.stop();
        mDetector.release();
    }

    /************************* VOLLEY *************************/
    void signUser(final String user) {
        String endpointUrl = BASE_URL + "inscription";

        JSONObject postData = new JSONObject();
        try {
            postData.put("id_joueur", user );
        } catch (Exception e) {
            // do nothing
        }

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Success post");
                Log.e(TAG, response.toString());
                //mbtnSignIn.setEnabled(false);
                //mNameEditor.setEnabled(false);
                try {
                    fillListView(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                editor.putString("name", user);
                editor.apply();

            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Erreur inscription");
            }
        };

        Log.e(TAG, postData.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpointUrl, postData, onSuccess, onError);

        mRequestQueue.add(request);
    }

    public void fillListView(JSONObject tofill) throws JSONException {

        // Getting JSON Array node
        JSONArray objets = tofill.getJSONArray("objet");

        for (int i = 0; i < objets.length(); i++) {
            String objetToFill = objets.getString(i);
            //Log.e(TAG, objetToFill);

            HashMap<String, String> objectHM = new HashMap<>();
            objectHM.put("name", objetToFill);
            objectHM.put("found", "Not found");
            objectList.add(objectHM);
        }

        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, objectList, R.layout.object_list, new String[]{"name", "found"}, new int[]{R.id.name, R.id.found});

        mList.setAdapter(adapter);

    }





    public void startImageActivity() {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    /************************* ???? *************************/
    private void showAlertDialog(String title, String message, DialogInterface.OnDismissListener onDismissListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        if (onDismissListener != null) builder.setOnDismissListener(onDismissListener);
        builder.show();
    }

}

