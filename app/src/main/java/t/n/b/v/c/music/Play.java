package t.n.b.v.c.music;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Play extends AppCompatActivity {
    private SeekBar mSeekBar;
    private ImageButton mPrevious;
    private ImageButton mPlay;
    private ImageButton mNext;
    public TextView mNowTime;
    public TextView mDurationTime;
    public MarqueeTextView mName;
    public MarqueeTextView mSinger;
    private ImageButton mImageButton;
    private android.support.v7.widget.Toolbar mToolBar;
    private MediaPlayer mMediaPlayer=new MediaPlayer();
    private static int DRAWABLE_STATE=0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private final int UP_TIME = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        Intent intent=getIntent();
        String mPath=intent.getStringExtra("extra_path");
        String mGetName=intent.getStringExtra("extra_name").replace(".mp3","");
        String mGetSinger=intent.getStringExtra("extra_singer");
        String mGetDuration=intent.getStringExtra("extra_duration");
        initMediaPlayer(mPath);
        fvbi();
        setSupportActionBar(mToolBar);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Play.this,MainActivity.class));
            }
        });
        mName.setText(mGetName);
        mSinger.setText(mGetSinger);
        mDurationTime.setText(mGetDuration);
        mPlay.setOnClickListener(view -> {
            if (DRAWABLE_STATE==0){
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                DRAWABLE_STATE=1;
                if (!mMediaPlayer.isPlaying()){
                    mMediaPlayer.start();        //播放
                }
            }else{
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
                DRAWABLE_STATE=0;
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();        //暂停
                }
            }
        });
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMediaPlayer.pause();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(mSeekBar.getProgress());
                mMediaPlayer.start();
            }
        });
    }



    private void fvbi() {
        mSeekBar=findViewById(R.id.seekBar);
        mPrevious=findViewById(R.id.previousButton);
        mNext=findViewById(R.id.nextButton);
        mPlay=findViewById(R.id.playButton);
        mNowTime=findViewById(R.id.nowTime);
        mDurationTime=findViewById(R.id.durationTime);
        mToolBar=findViewById(R.id.toolbar);
        mName=findViewById(R.id.tv_name);
        mSinger=findViewById(R.id.tv_singer);
        mImageButton=findViewById(R.id.imageButton);
    }
    private void initMediaPlayer(String string) {
        try{
            mMediaPlayer.setDataSource(string);                               //设置mediaPlayer
            mMediaPlayer.prepare();                                           //让mediaPlayer进入到准备状态
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release(); //APP处于onDestroy时释放MediaPlayer的资源
        }
    }
}
