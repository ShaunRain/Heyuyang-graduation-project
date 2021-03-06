package com.example.yang.myapplication.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.yang.myapplication.R;
import com.example.yang.myapplication.data.AlarmData;

/**
 * 闹钟响铃界面
 */
public class WakeUpActivity extends Activity {

    TextView mTag;
    TextView mContent;
    MediaPlayer mPlayer;
    Ringtone ringtone;
    Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_wake_up);

        wakeUpAndUnlock(this);
        KeyguardManager km =
                (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isLocked = km.inKeyguardRestrictedInputMode();
        if (isLocked) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        initViews();

        AlarmData alarmData = (AlarmData) getIntent().getSerializableExtra("alarmData");

        if (alarmData != null) {
            mTag.setText(alarmData.getName());
            mContent.setText(alarmData.getDetails());
            if (alarmData.isVib()) {
                vibrate();
            }
            playRing(alarmData);
        }
//        wakeUpAndUnlock(this);
    }

    private void playRing(AlarmData alarmData) {
        RingtoneManager manager = new RingtoneManager(WakeUpActivity.this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
//        Uri ringUri = manager.getRingtoneUri(alarmData.getRing());
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(WakeUpActivity.this, ringUri);
        ringtone.play();
//        mPlayer = MediaPlayer.create(WakeUpActivity.this, ringtone);
//        try {
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Toast.makeText(WakeUpActivity.this, "ring error", Toast.LENGTH_SHORT).show();
//            finish();
//        }
    }

    private void vibrate() {
        vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(new long[]{500, 500}, 0);
    }

    private void initViews() {
        mTag = (TextView) findViewById(R.id.wake_tag);
        mContent = (TextView) findViewById(R.id.wake_content);
    }

    public void close(View view) {

//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//            mPlayer.stop();
//            mPlayer.release();
//        }
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        if (vib != null) {
            vib.cancel();
        }
        finish();
    }

    public static void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

}
