package com.example.mlkit_barcode_ocr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mlkit_barcode_ocr.utility.CameraSource;
import com.example.mlkit_barcode_ocr.utility.CameraSourcePreview;
import com.example.mlkit_barcode_ocr.utility.GraphicOverlay;
import com.example.mlkit_barcode_ocr.RSA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Decrypt extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String SELECT = "Select";
    private static final String TEXT_DETECTION = "Text Detection";
    private static final String BARCODE_DETECTION = "Barcode Detection";
    private static final int PERMISSION_REQUESTS = 1;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private com.example.mlkit_barcode_ocr.utility.GraphicOverlay graphicOverlay;
    private static final String TAG = "Select";
    private String selectedModel = BARCODE_DETECTION;
    String private_key;
    ToggleButton facingSwitch;
    //instantiate RSA object
    com.example.mlkit_barcode_ocr.RSA rsa = new com.example.mlkit_barcode_ocr.RSA();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
       //
        // facingSwitch = findViewById(R.id.facingSwitch);

       // facingSwitch.setOnCheckedChangeListener(this);
        // Hide the toggle button if there is only 1 camera
//        if (Camera.getNumberOfCameras() == 1) {
//            facingSwitch.setVisibility(View.GONE);
//        }


        Spinner spinner = findViewById(R.id.spinner);
        List<String> options = new ArrayList<>();
        options.add(SELECT);
        options.add(BARCODE_DETECTION);
        options.add(TEXT_DETECTION);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style,
                options);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
            startCameraSource();
        } else {
            getRuntimePermissions();
        }

        //send private key to Main Activity
        Intent intent = getIntent();
        private_key = intent.getStringExtra("PrKey");
        Log.i("success", private_key);
        //update private key variable in RSA class
        rsa.get_key(private_key);

    }

    //NOT used
    //button at the bottom of the screen that reverts camera
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (cameraSource != null) {
//            if (isChecked) {
//                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
//            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
           // }
        }
        preview.stop();
        startCameraSource();
    }

    //drop down menu launch
    private void createCameraSource(String choice) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        try {
            switch (choice) {
                case SELECT:
                    //nothing
                    break;
                case BARCODE_DETECTION:
                    cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor());
                    break;
                case TEXT_DETECTION:
                    cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor());
                    break;
                default:
                    Log.e(TAG, "Unknown choice: " + choice);
            }
        }
        catch (Exception e){
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }


    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, (String[]) allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
            startCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public synchronized  void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //retrieve selected item

            selectedModel = parent.getItemAtPosition(position).toString();
            preview.stop();
            if (allPermissionsGranted()) {
                createCameraSource(selectedModel);
                startCameraSource();
            } else {
                getRuntimePermissions();
            }
    }


    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            //nothing
    }
}