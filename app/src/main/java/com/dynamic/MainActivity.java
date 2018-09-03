package com.dynamic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private Button download;
    private Button start;

    private int mySessionId;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download = findViewById(R.id.download);
        start = findViewById(R.id.start);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDynamicModule();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.dynamic", "com.dynamic.module.DynamicActivity");
                startActivity(intent);
            }
        });

    }

    private void downloadDynamicModule() {
        SplitInstallManager splitInstallManager =
                SplitInstallManagerFactory.create(this);

        SplitInstallRequest request =
                SplitInstallRequest
                        .newBuilder()
                        .addModule("dynamic-module")
                        .build();

        SplitInstallStateUpdatedListener listener = new SplitInstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
                if(splitInstallSessionState.sessionId() == mySessionId) {
                    switch (splitInstallSessionState.status()) {
                        case SplitInstallSessionStatus.INSTALLED:
                            Log.d(TAG, "Dynamic Module downloaded");
                            Toast.makeText(MainActivity.this, "Dynamic Module downloaded", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        splitInstallManager.registerListener(listener);

        splitInstallManager.startInstall(request)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Exception: " + e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer sessionId) {
                        mySessionId = sessionId;
                    }
                });
    }
}
