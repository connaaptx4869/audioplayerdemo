package com.parker.audioplayerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean stop;
    private ArrayList<File> playList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startPlayAudio();
                } catch (Exception e) {
                    Log.e("PlayerIOError", "" + e.getMessage());
                }
            }
        });
        findViewById(R.id.tv_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlayAudio();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayAudio();
            }
        });

    }

    private void startPlayAudio() throws Exception {
        int i = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (i == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            return;
        }
        if (mediaPlayer == null) {
            String randomSongPath = getRandomSongPath();
            if (randomSongPath == null) {
                return;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setDataSource(randomSongPath);
            mediaPlayer.prepare();
            stop = false;
        } else if (!mediaPlayer.isPlaying()) {
            if (stop) {
                mediaPlayer.setDataSource(getRandomSongPath());
                mediaPlayer.prepare();
                stop = false;
            } else {
                mediaPlayer.start();
            }
        }

    }

    private String getRandomSongPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + File.separator
                    + "Song";
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                List<File> filesAll = Arrays.asList(files);
                if (filesAll.size() > 0) {
                    if (playList == null || playList.size() == 0) {
                        playList = new ArrayList<>(filesAll);
                    }
                    Random random = new Random();
                    File randomFile = playList.get(random.nextInt(playList.size()));
                    playList.remove(randomFile);
                    return randomFile.getPath();
                }
            }
        }
        return null;

    }

    private void pausePlayAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    private void stopPlayAudio() {
        if (mediaPlayer != null) {
            stop = true;
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    startPlayAudio();
                } catch (Exception e) {
                    Log.e("PlayerIOError", e.getMessage());
                }
            }
        }
    }
}
