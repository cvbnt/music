package t.n.b.v.c.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

import static java.lang.StrictMath.abs;

public class Play extends AppCompatActivity {
    String m_Path;
    String m_Name;
    String m_Singer;
    String m_Duration;
    String m_Path_Lrc;
    int m_size;
    Music music;
    int mGetPosition;
    private SeekBar mSeekBar;
    private ImageButton mPrevious;
    private ImageButton mPlay;
    private ImageButton mNext;
    private ImageButton mShuffle;
    private ImageButton mRepeat;
    private Drawable drawable;
    public TextView mNowTime;
    public TextView mDurationTime;
    public MarqueeTextView mName;
    public MarqueeTextView mSinger;
    private ImageButton mImageButton;
    private android.support.v7.widget.Toolbar mToolBar;
    private MediaPlayer mMediaPlayer;
    private static int DRAWABLE_STATE=0;
    private static int SHUFFLE_STATE=0;
    private static int REPEAT_STATE=0;
    private Handler mHandler;
    private Context context;
    private LyricView lyricView;
    private int INTERVAL=45;//歌词每行的间隔
    private static final String TS=".ts";
    private static final String FLAC=".flac";
    private static final String MXMF=".mxmf";
    private static final String RTTTL=".rtttl";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        mMediaPlayer=new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> checkState());
        context=Play.this;
        m_size=Scan.getMusicData(context).size();
        Intent intent=getIntent();
        mGetPosition = intent.getExtras().getInt("extra_position");
        fvbi();
        refresh();
        initMediaPlayer(m_Path);
        searchLrc();
        drawable=mShuffle.getDrawable();
        setSupportActionBar(mToolBar);
        Handler mHandler=new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                    mSeekBar.setProgress(mCurrentPosition);
                    mNowTime.setText(Scan.formatTime(mCurrentPosition));
                    lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
                    lyricView.SelectIndex(mMediaPlayer.getCurrentPosition());
                }
               mHandler.postDelayed(this, 1000);
                mHandler.post(mUpdateResults);
            }
       });
        mImageButton.setOnClickListener(view -> startActivity(new Intent(Play.this,MainActivity.class)));
        mPlay.setOnClickListener(view -> {
            if (DRAWABLE_STATE==0){
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                DRAWABLE_STATE=1;
                if (!mMediaPlayer.isPlaying()){
                    mMediaPlayer.start();        //播放
                    lyricView.setOffsetY(220 - lyricView.SelectIndex(mMediaPlayer.getCurrentPosition())
                            * (lyricView.getSIZEWORD() + INTERVAL-1));
                }
                }else{
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
                DRAWABLE_STATE=0;
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();        //暂停
                }
            }
        });
        mPrevious.setOnClickListener(view -> playPreviousSong());
        mNext.setOnClickListener(view -> playNextSong());
        mRepeat.setOnClickListener(view -> {
            if (REPEAT_STATE==0){
                REPEAT_STATE=1;
                SHUFFLE_STATE=0;
                mRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat_one));
                drawable.setTint(Color.BLACK);
            }else {
                REPEAT_STATE=0;
                SHUFFLE_STATE=0;
                mRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                drawable.setTint(Color.BLACK);
            }
        });
        mShuffle.setOnClickListener(view -> {
            if (SHUFFLE_STATE==0) {
                SHUFFLE_STATE = 1;
                REPEAT_STATE = 0;
                mRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                drawable.setTint(Color.parseColor("#FF4081"));
            }else {
                SHUFFLE_STATE=0;
                REPEAT_STATE=0;
                mRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                drawable.setTint(Color.BLACK);
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lyricView.setOffsetY(220 - lyricView.SelectIndex(i)
                        * (lyricView.getSIZEWORD() + INTERVAL-1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(mSeekBar.getProgress());
                mMediaPlayer.start();
            }
        });
    }

    private void searchLrc() {
        LyricView.read(m_Path_Lrc);
        lyricView.SetTextSize();
        lyricView.setOffsetY(350);
    }

    private void refresh(){
        music=Scan.getMusicData(context).get(mGetPosition);
        m_Path=music.getPath();
        if (m_Path.contains(TS)){
            m_Name=music.getName().substring(0,music.getName().length()-3);
            m_Path_Lrc=m_Path.substring(0,m_Path.length()-3)+".lrc";
        }else if (m_Path.contains(FLAC)||m_Path.contains(MXMF)){
            m_Name=music.getName().substring(0,music.getName().length()-5);
            m_Path_Lrc=m_Path.substring(0,m_Path.length()-5)+".lrc";
        }else if (m_Path.contains(RTTTL)){
            m_Name=music.getName().substring(0,music.getName().length()-6);
            m_Path_Lrc=m_Path.substring(0,m_Path.length()-6)+".lrc";
        }
        m_Name=music.getName().substring(0,music.getName().length()-4);
        m_Path_Lrc=m_Path.substring(0,m_Path.length()-4)+".lrc";
        m_Singer=music.getSinger();
        m_Duration=Scan.formatTime(music.getDuration());
        mName.setText(m_Name);
        mSinger.setText(m_Singer);
        mDurationTime.setText(m_Duration);
    }
    private void fvbi() {
        lyricView=findViewById(R.id.lrcView);
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
        mShuffle=findViewById(R.id.shuffleButton);
        mRepeat=findViewById(R.id.repeatButton);
    }
    private void initMediaPlayer(String string) {
        try{
            mMediaPlayer.setDataSource(string);                               //设置mediaPlayer
            mMediaPlayer.prepare();                                           //让mediaPlayer进入到准备状态
            mSeekBar.setMax(mMediaPlayer.getDuration());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void playPreviousSong() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mGetPosition = mGetPosition-1;
        if (mGetPosition<0){
            mGetPosition=m_size-(abs(mGetPosition)%m_size);
        }
        refresh();
        initMediaPlayer(m_Path);
        searchLrc();
        mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        mMediaPlayer.start();
    }

    private void playNextSong() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mGetPosition = mGetPosition+1;
        if (mGetPosition>=0){
            mGetPosition=abs(mGetPosition)%m_size;
        }
        refresh();
        initMediaPlayer(m_Path);
        searchLrc();
        mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        mMediaPlayer.start();
    }
    private void checkState(){
        if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==0)){
            playNextSong();
        }else if ((REPEAT_STATE==1)&&(SHUFFLE_STATE==0)){
            mMediaPlayer.start();
        }else if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==1)){
            int mRandomPosition=new Random().nextInt(1000);
            mRandomPosition=abs(mRandomPosition)%m_size;
            if (mRandomPosition==mGetPosition){
                mRandomPosition=mRandomPosition+1;
            }
            music=Scan.getMusicData(context).get(mRandomPosition);
            m_Path=music.getPath();
            m_Name=music.getName().replace(".mp3","");
            m_Singer=music.getSinger();
            m_Duration=Scan.formatTime(music.getDuration());
            mName.setText(m_Name);
            mSinger.setText(m_Singer);
            mDurationTime.setText(m_Duration);
            mMediaPlayer.reset();
            initMediaPlayer(m_Path);
            searchLrc();
            mMediaPlayer.start();
        }
    }

    Runnable mUpdateResults = new Runnable() {
        public void run() {
            lyricView.invalidate(); // 更新视图
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer!=null){
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.release(); //APP处于onDestroy时释放MediaPlayer的资源
            mMediaPlayer=null;//回收资源
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
