package com.synologix.zaklamp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Camera camera;
    private Camera.Parameters parameters;
    private ImageButton flashlightButton;
    boolean isFlashLightOn = false;
    //voor android m
    static boolean toggle=false;
    CameraManager cameraManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        flashlightButton = (ImageButton) findViewById(R.id.flashlightButton);
        flashlightButton.setBackgroundColor(Color.DKGRAY);
        flashlightButton.setOnClickListener(new FlashOnOffListener());


        //alleen doen als android lager dan lollipop
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (isFlashSupported()) {
                camera = Camera.open();
                parameters = camera.getParameters();
            } else {
                showNoFlashAlert();
            }
        }



    }

    private class FlashOnOffListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                if (isFlashLightOn) {
                    flashlightButton.setImageResource(R.drawable.flashlightoff);
                    flashlightButton.setBackgroundColor(Color.DKGRAY);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    isFlashLightOn = false;
                } else {
                    flashlightButton.setImageResource(R.drawable.flashlighton);
                    flashlightButton.setBackgroundColor(Color.WHITE);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    isFlashLightOn = true;
                }

            }else{
                toggleFlashLight();
            }
        }
    }

    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage("Your device hardware does not support flashlight!")
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    //voor android m
    @TargetApi(Build.VERSION_CODES.M)
    public void toggleFlashLight() {
        toggle = !toggle;
        try {
            CameraManager cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);

            for (String id : cameraManager.getCameraIdList()) {

                // Turn on the flash if camera has one
                if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {

                    cameraManager.setTorchMode(id, toggle);
                    if (!toggle) {flashlightButton.setBackgroundColor(Color.DKGRAY);
                        flashlightButton.setImageResource(R.drawable.flashlightoff);
                    } else{
                        flashlightButton.setBackgroundColor(Color.WHITE);
                        flashlightButton.setImageResource(R.drawable.flashlighton);
                    }

                }
            }

        } catch (Exception e2) {
            Toast.makeText(getApplicationContext(), "Torch Failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    protected void onDestroy() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }

}





