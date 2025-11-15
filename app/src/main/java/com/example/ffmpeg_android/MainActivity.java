package com.example.ffmpeg_android;


import android.util.Log;
import com.arthenica.ffmpegkit.ReturnCode;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.SessionState;
import com.arthenica.ffmpegkit.Statistics;
import com.example.ffmpeg_android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int REQUEST_STORAGE_PERMISSION = 1001;
    
    private LogAdapter logAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ask for storage permission
        requestStoragePermission();
        
        RecyclerView logRecycler = findViewById(R.id.logRecycler);

        logAdapter = new LogAdapter();
        logRecycler.setAdapter(logAdapter);
        logRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.buttonRun.setOnClickListener(v -> {
            String cmd = binding.cmd.getText().toString().trim();
            if (cmd.isEmpty()) {
                Toast.makeText(this, "Please enter an FFmpeg command!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            FFmpegKit.executeAsync(cmd, session ->  {
                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    logOnUI("FFmpeg finished successfully.");
                } else {
                   logOnUI("FFmpeg failed: " + session.getFailStackTrace());
                }
            }, log -> {
                logOnUI(log.getMessage());
            }, stats -> {
                logOnUI(
                    "time=" + stats.getTime() +
                    " fps=" + stats.getVideoFps() +
                    " speed=" + stats.getSpeed()
                );
            });
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void logOnUI(String msg) {
    runOnUiThread(() -> {
            logAdapter.addLog(msg);
            binding.logRecycler.smoothScrollToPosition(logAdapter.getItemCount() - 1);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
