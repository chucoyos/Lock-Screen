package com.example.lockscreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button lockButton, enableButton, disableButton;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName componentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        componentName = new ComponentName(this, MyAdmin.class);

        lockButton = findViewById(R.id.lockButton);
        enableButton = findViewById(R.id.enableButton);
        disableButton = findViewById(R.id.disableButton);
        lockButton.setOnClickListener(this);
        enableButton.setOnClickListener(this);
        disableButton.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        boolean isActive = devicePolicyManager.isAdminActive(componentName);
        disableButton.setVisibility(isActive ? View.VISIBLE : View.GONE);
        enableButton.setVisibility(isActive ? View.GONE : View.VISIBLE);
    }

    public void onClick(View view){
        if (view == lockButton){
            boolean active = devicePolicyManager.isAdminActive(componentName);
            if (active){
                devicePolicyManager.lockNow();
            } else {
                Toast.makeText(this, "You need to enable the Admin Device Feature", Toast.LENGTH_SHORT).show();
            }
        } else if (view == enableButton){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
            startActivityForResult(intent, RESULT_ENABLE);

        } else if (view == disableButton){
            devicePolicyManager.removeActiveAdmin(componentName);
            disableButton.setVisibility(View.GONE);
            enableButton.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "You have enable the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}