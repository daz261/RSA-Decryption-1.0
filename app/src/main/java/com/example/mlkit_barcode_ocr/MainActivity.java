package com.example.mlkit_barcode_ocr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
        {

    String private_key;
    Button private_key_scan;
    Button decrypt;
    TextView debug;
    private static final int PERMISSION_REQUESTS = 1;
    private static final Class<?>[] CLASSES =
            new Class<?>[] {ScanPrivateKey.class, Decrypt.class};

    private static final int[] DESCRIPTION_IDS = new int[] {R.string.title_activity_scan_key, R.string.title_activity_decrypt};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        private_key_scan = (Button) findViewById(R.id.private_key_scan);
        debug = (TextView) findViewById(R.id.debug);
        decrypt = (Button) findViewById(R.id.decrypt);


       // ListView listView = findViewById(R.id.testActivityListView);

//        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
//        adapter.setDescriptionIds(DESCRIPTION_IDS);
//
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(this);
        private_key_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ScanPrivateKey.class);
                startActivity(myIntent);
            }
        });

        //get private key from ScanKey activity
        Intent intent = getIntent();
        private_key = intent.getStringExtra("private");

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent = new Intent(MainActivity.this, Decrypt.class);
                rIntent.putExtra("PrKey", private_key);
                startActivity(rIntent);
            }
        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

    }

//    /**
//     * Callback method to be invoked when an item in this AdapterView has
//     * been clicked.
//     * <p>
//     * Implementers can call getItemAtPosition(position) if they need
//     * to access the data associated with the selected item.
//     *
//     * @param parent   The AdapterView where the click happened.
//     * @param view     The view within the AdapterView that was clicked (this
//     *                 will be a view provided by the adapter)
//     * @param position The position of the view in the adapter.
//     * @param id       The row id of the item that was clicked.
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position==0){
//            Class<?> clicked = CLASSES[0];
//            startActivity(new Intent(this, clicked));
//        }
//
//        //get private key from ScanKey activity
//        Intent intent = getIntent();
//        private_key = intent.getStringExtra("private");
//        Log.i("received", private_key);
//
//        if (position == 1){
//            Class<?> clicked = CLASSES[1];
//            Intent rIntent = new Intent(this, clicked);
//            rIntent.putExtra("PrKey", private_key);
//            startActivity(rIntent);
//        }
//
//    }

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

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //design launcher activity
//    private static class MyArrayAdapter extends ArrayAdapter<Class<?>> {
//        private final Context context;
//        private final Class<?>[] classes;
//        private int[] descriptionIds;
//
//        public MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
//            super(context, resource, objects);
//
//            this.context = context;
//            classes = objects;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = convertView;
//
//            if (convertView == null) {
//                LayoutInflater inflater =
//                        (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
//            }
//
//
//            ((TextView) view.findViewById(android.R.id.text2)).setText(descriptionIds[position]);
//            ((TextView) view.findViewById(android.R.id.text1)).setText(classes[position].getSimpleName());
//
//            return view;
//        }
//
//        public void setDescriptionIds(int[] descriptionIds) {
//            this.descriptionIds = descriptionIds;
//        }
//    }






}
