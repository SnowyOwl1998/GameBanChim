package com.example.gamebanchim;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isMuteSound, isMuteMusic, isPauseMusic = false;
    private boolean mIsBound = false;
    private MusicService mServ;
    private AlertDialog helpBoard;
    private LinearLayout layoutHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutHelp = findViewById(R.id.layoutHelp);
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        TextView highScoreTxt = findViewById(R.id.highScoreTxt);

        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        highScoreTxt.setText("HighScore: " + prefs.getInt("highscore", 0));

        isMuteSound = prefs.getBoolean("isMuteSound", false);
        final ImageView volumeCtrl = findViewById(R.id.volumeButton);
        if(isMuteSound)
            volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
        else
            volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24);

        volumeCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMuteSound = !isMuteSound;
                if(isMuteSound)
                    volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
                else
                    volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMuteSound", isMuteSound);
                editor.apply();
            }

        });

        Intent music = new Intent();
        music.setClass(this ,MusicService.class);
        startService(music);

        doBindService();

        final ImageView musicCtrl = findViewById(R.id.musicButton);
        if(isMuteMusic)
            musicCtrl.setImageResource(R.drawable.ic_baseline_music_off_24);
        else
            musicCtrl.setImageResource(R.drawable.ic_baseline_music_note_24);
        musicCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMuteMusic = !isMuteMusic;
                if(isMuteMusic) {
                    musicCtrl.setImageResource(R.drawable.ic_baseline_music_off_24);
                    mServ.muteMusic();
                }
                else {
                    musicCtrl.setImageResource(R.drawable.ic_baseline_music_note_24);
                    mServ.unmuteMusic();
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMuteMusic", isMuteMusic);
                editor.apply();
            }

        });

    }

    @Override
    protected void onPause() {
        if(!isPauseMusic){
            mServ.pauseMusic();
            isPauseMusic = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(isPauseMusic){
            mServ.resumeMusic();
            isPauseMusic = false;
        }
        super.onResume();
    }

    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    private void showHelp(){
        if (helpBoard == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_help,
                    (ViewGroup) findViewById(R.id.layoutHelp)
            );
            builder.setView(view);

            helpBoard = builder.create();
            if (helpBoard.getWindow() != null){
                helpBoard.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            helpBoard.dismiss();
        } helpBoard.show();
    }
}