package com.example.geocasher;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.altbeacon.beacon.*;
import org.json.JSONObject;


import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;


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

import java.net.URI;
import java.util.Collection;


public class MainActivity extends AppCompatActivity implements BeaconConsumer{
    private static final int REQUEST_PERMISSION = 1;
    public static final int PICK_IMAGE = 1;
    static final int REQUEST_PERMCAM = 1;

    static final String TAG = "Geocasher";

    private BeaconManager beaconManager;
    RequestQueue mRequestQueue;

    // View Components
    TextView mTextView;
    TextView mTextView2;
    ImageView imageVi;

    // Model Components
    BarcodeDetector mDetector;
    CameraSource mCamera;
    Button clickButton;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView2 = (TextView) findViewById(R.id.textView2);
        imageVi = (ImageView) findViewById(R.id.imageView);
        clickButton = (Button) findViewById(R.id.btnSelectImage);
        button2 = (Button) findViewById(R.id.button2);

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
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
                        }
                    });
        }


        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        button2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoPostHttpRequest();
            }
        });

        mRequestQueue = Volley.newRequestQueue(this);

    }

    /************************* Screen interact *************************/
    void displayText(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(message);
            }
        });
    }

    void displayBeacon(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView2.setText(message);
            }
        });
    }

    void changeBtnImageState(final boolean state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clickButton.setEnabled(state);
            }
        });
    }

    /************************* Activity & Permissions *************************/
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Uri uri = data.getData();
            imageVi.setImageURI(uri);
        }
    }

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

        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    showAlertDialog("Functionality limited",
                            "Since location access has not been granted, this app will not be able to discover beacons when in the background.", null);
                }
                return;
            }
        }
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
            mTextView.setText("Could not set up the detector!\nPlease update or upgrade your tablet (consider an iPhone X).");
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
            mTextView.setText("Failed to start the camera");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            mTextView.setText("Failed to start the camera");
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
                    Log.i(TAG, "Detected beacon : " + beacon.getId1());
                    Log.i(TAG, "Detected beacon @ distance " + beacon.getDistance());
                    changeBtnImageState(true);
                }
            }
        });

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "didEnterRegion");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "didExitRegion");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG, "didDetermineStateForRegion = " + i);
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
    void demoPostHttpRequest() {
        String endpointUrl ="https://polar-bayou-90643.herokuapp.com/inscription";

        JSONObject postData = new JSONObject();
        try {
            postData.put("id_joueur","Bloubi");
        } catch (Exception e) {
            // do nothing
        }

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Success post");
            }
        };

        Response.ErrorListener onError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Erreur berk");
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpointUrl, postData, onSuccess, onError);

        mRequestQueue.add(request);
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
