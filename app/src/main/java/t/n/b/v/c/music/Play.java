package t.n.b.v.c.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.StrictMath.abs;

public class Play extends AppCompatActivity {
    String m_Path;
    String m_Name;
    String m_Singer;
    String m_Duration;
    int m_size;
    Music music;
    int mGetPosition;
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
    private Handler mHandler;
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        context=Play.this;
        m_size=Scan.getMusicData(context).size();
        Intent intent=getIntent();
        mGetPosition = intent.getExtras().getInt("extra_position");
        fvbi();
        refresh();
        initMediaPlayer(m_Path);
        m_size=Scan.getMusicData(context).size();
        mSeekBar.setMax(mMediaPlayer.getDuration());
        setSupportActionBar(mToolBar);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Play.this,MainActivity.class));
            }
        });
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
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetPosition = mGetPosition-1;
                if (mGetPosition<0){
                    mGetPosition=m_size-(abs(mGetPosition)%m_size);
                }
                refresh();
                initMediaPlayer(m_Path);
                mSeekBar.setMax(mMediaPlayer.getDuration());
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetPosition = mGetPosition+1;
                if (mGetPosition>=0){
                    mGetPosition=abs(mGetPosition)%m_size;
                }
                refresh();
                initMediaPlayer(m_Path);
                mSeekBar.setMax(mMediaPlayer.getDuration());
            }
        });
        mHandler=new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                    mSeekBar.setProgress(mCurrentPosition);
                    mNowTime.setText(Scan.formatTime(mCurrentPosition));
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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
    private void refresh(){
        music=Scan.getMusicData(context).get(mGetPosition);
        m_Path=music.getPath();
        m_Name=music.getName().replace(".mp3","");
        m_Singer=music.getSinger();
        m_Duration=Scan.formatTime(music.getDuration());
        mName.setText(m_Name);
        mSinger.setText(m_Singer);
        mDurationTime.setText(m_Duration);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release(); //APP处于onDestroy时释放MediaPlayer的资源
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
