package com.example.floriannecas.geocasher;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import android.widget.TextView;

import org.altbeacon.beacon.*;

import java.util.Collection;

public class MainActivity extends Activity implements BeaconConsumer  {
    protected static final String TAG = MainActivity.class.getSimpleName();
    private BeaconManager mBeaconManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        // Detect the main identifier (UID) frame:
        //mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        //mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final TextView v_uuid = (TextView)findViewById(R.id.uuid);
                if (beacons.size() > 0) {
                    v_uuid.setText(beacons.iterator().next().getRssi() + "");
                    Log.w(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    /*@Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

        final TextView v_uuid = (TextView)findViewById(R.id.uuid);
        //final TextView v_distance = (TextView)findViewById(R.id.url);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Beacon beacon: beacons) {
                    //v_distance.setText(beacon.getDistance());
                    v_uuid.setText(beacon.getRssi());
                }
            }

        });
    }*/
}

