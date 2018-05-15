package t.n.b.v.c.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.EventListener;
import java.util.Random;

import static java.lang.StrictMath.abs;

public class Play extends AppCompatActivity {
    String m_Path;
    String m_Name;
    String m_Singer;
    String m_Duration;
    String m_Path_Lrc;
    int m_size;
    private Music music;
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
    private static int DRAWABLE_STATE=0;          //用于切换播放键的图片的状态码
    private static int SHUFFLE_STATE=0;           //用于切换随机按键的图片的状态码
    private static int REPEAT_STATE=0;            //用于切换不同循环状态的图片的状态码
    private Handler mHandler;
    private Context context;
    private LyricView lyricView;
    private int INTERVAL=45;                       //歌词每行的间隔
    private static final String TS=".ts";         //支持的音乐文件后缀大多数为.XXX，对于特殊的后缀需要分别去掉后缀显示音乐名
    private static final String FLAC=".flac";
    private static final String MXMF=".mxmf";
    private static final String RTTTL=".rtttl";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        mMediaPlayer=new MediaPlayer();              //MediaPlayer对象用于操作播放
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> checkState());  //mMediaPlayer设置播放歌曲结束后的动作，此处设为检查循环或随机按钮的状态码采取不同的播放模式
        context=Play.this;
        m_size=Scan.getMusicData(context).size();                           //获取歌曲数组列表长度
        Intent intent=getIntent();                                          //获取intent
        mGetPosition = intent.getExtras().getInt("extra_position");    //获取从MainActivity传递过来的intent中的position值
        fvbi();
        refresh();                                                           //refresh()包含一系列必须重复操作
        initMediaPlayer(m_Path);                                             //载入MediaPlayer
        searchLrc();                                                         //寻找歌曲是否有歌词
        drawable=mShuffle.getDrawable();                                     //为了转换随机按钮的图片，对随机按钮的图片进行操作
        setSupportActionBar(mToolBar);                                       //设置ToolBar
        Handler mHandler=new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {                                              //设计子线程处理LrcView，SeekBar，mNowTime动态显示
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition(); //获取当前歌曲播放进度
                    mSeekBar.setProgress(mCurrentPosition);                   //设置SeekBar进度为当前歌曲进度
                    mNowTime.setText(Scan.formatTime(mCurrentPosition));      //设置mNowTime为当前歌曲播放进度的时间
                    lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());  //lyricView设置OffestY为原始OffestY减去滚动的纵坐标偏移量
                    lyricView.SelectIndex(mMediaPlayer.getCurrentPosition());             //lyricView设置歌词显示
                }
               mHandler.postDelayed(this, 1000);                 //设置每1000毫秒循环执行上述操作一次
                mHandler.post(mUpdateResults);
            }
       });
        mImageButton.setOnClickListener(view -> startActivity(new Intent(Play.this,MainActivity.class)));   //左上角ToolBar最左边箭头图标设置点击事件回退到MainActivity
        mPlay.setOnClickListener(view -> {                                 //播放按钮设置点击事件
            if (DRAWABLE_STATE==0){                                        //根据当前图片的状态转换播放状态
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));  //当前播放按钮如果为Play，则设置为pause图片
                DRAWABLE_STATE=1;                                          //状态码切换为1
                if (!mMediaPlayer.isPlaying()){                            //如果mMediaPlayer为暂停状态
                    mMediaPlayer.start();                                  //播放
                    lyricView.setOffsetY(220 - lyricView.SelectIndex(mMediaPlayer.getCurrentPosition())
                            * (lyricView.getSIZEWORD() + INTERVAL-1));     //设置lyricView
                }
                }else{
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
                DRAWABLE_STATE=0;
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();        //暂停
                }
            }
        });
        mPrevious.setOnClickListener(view -> {
            mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));  //当前播放按钮如果为Play，则设置为pause图片
            DRAWABLE_STATE=1;
            if ((REPEAT_STATE==1)&&(SHUFFLE_STATE==0)){
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
            }else if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==1)){
                mMediaPlayer.stop();
                int mRandomPosition=new Random().nextInt(1000);
                mRandomPosition=abs(mRandomPosition)%m_size;
                if (mRandomPosition==mGetPosition){
                    mRandomPosition=mRandomPosition+1;
                }
                music=Scan.getMusicData(context).get(mRandomPosition);
                m_Path=music.getPath();
                if (m_Path.contains(TS)){                               //以下为为不同歌曲名后缀处理，获得相对应的路径和名字
                    m_Name=music.getName().substring(0,music.getName().length()-3);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-3)+".lrc";
                }else if (m_Path.contains(FLAC)||m_Path.contains(MXMF)){
                    m_Name=music.getName().substring(0,music.getName().length()-5);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-5)+".lrc";
                }else if (m_Path.contains(RTTTL)){
                    m_Name=music.getName().substring(0,music.getName().length()-6);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-6)+".lrc";
                }
                m_Path_Lrc=m_Path.substring(0,m_Path.length()-4)+".lrc";
                m_Name=music.getName().replace(".mp3","");
                m_Singer=music.getSinger();
                m_Duration=Scan.formatTime(music.getDuration());
                mName.setText(m_Name);
                mSinger.setText(m_Singer);
                mDurationTime.setText(m_Duration);
                initMediaPlayer(m_Path);
                searchLrc();
                mMediaPlayer.start();
            }else {
                playPreviousSong();
            }
        });                //previous按钮设置为播放上一个歌曲
        mNext.setOnClickListener((View view) -> {
            mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));  //当前播放按钮如果为Play，则设置为pause图片
            DRAWABLE_STATE=1;
            if ((REPEAT_STATE==1)&&(SHUFFLE_STATE==0)){
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
            }else if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==1)){
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                int mRandomPosition=new Random().nextInt(1000);
                mRandomPosition=abs(mRandomPosition)%m_size;
                if (mRandomPosition==mGetPosition){
                    mRandomPosition=mRandomPosition+1;
                }
                music=Scan.getMusicData(context).get(mRandomPosition);
                m_Path=music.getPath();
                if (m_Path.contains(TS)){                               //以下为为不同歌曲名后缀处理，获得相对应的路径和名字
                    m_Name=music.getName().substring(0,music.getName().length()-3);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-3)+".lrc";
                }else if (m_Path.contains(FLAC)||m_Path.contains(MXMF)){
                    m_Name=music.getName().substring(0,music.getName().length()-5);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-5)+".lrc";
                }else if (m_Path.contains(RTTTL)){
                    m_Name=music.getName().substring(0,music.getName().length()-6);
                    m_Path_Lrc=m_Path.substring(0,m_Path.length()-6)+".lrc";
                }
                m_Path_Lrc=m_Path.substring(0,m_Path.length()-4)+".lrc";
                m_Name=music.getName().replace(".mp3","");
                m_Singer=music.getSinger();
                m_Duration=Scan.formatTime(music.getDuration());
                mName.setText(m_Name);
                mSinger.setText(m_Singer);
                mDurationTime.setText(m_Duration);
                initMediaPlayer(m_Path);
                searchLrc();
                mMediaPlayer.start();
            }else {
                playNextSong();
            }
        });                        //next按钮设置为播放下一个歌曲
        mRepeat.setOnClickListener(view -> {                                     //重复按钮设置为在单曲循环播放和顺序播放间切换
            if (REPEAT_STATE==0){                                                //同时切换图片
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
        mShuffle.setOnClickListener(view -> {                                    //按下随机播放按钮，则开始随机播放，图片设置为粉红色
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
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {    //设置SeekBar拖动事件
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {       //拖动过程中lyricView同步移动
                lyricView.setOffsetY(220 - lyricView.SelectIndex(i)
                        * (lyricView.getSIZEWORD() + INTERVAL-1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {                    //开始拖动时播放按钮切换为pause
                mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {                     //停止拖动时，跳转到当前位置继续播放
                mMediaPlayer.seekTo(mSeekBar.getProgress());
                mMediaPlayer.start();
            }
        });
    }

    private void searchLrc() {
        LyricView.read(m_Path_Lrc);                             //调用LyricView的read方法通过文件路径
        lyricView.SetTextSize();                                //设置歌词字体适配
        lyricView.setOffsetY(350);                              //设置OffsetY值
    }

    private void refresh(){
        music=Scan.getMusicData(context).get(mGetPosition);     //通过获取到的position值获得对应position的music单例
        m_Path=music.getPath();                                 //获得歌曲路径
        if (m_Path.contains(TS)){                               //以下为为不同歌曲名后缀处理，获得相对应的路径和名字
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
        m_Singer=music.getSinger();                            //获取歌手名
        m_Duration=Scan.formatTime(music.getDuration());       //获取歌曲长度，并调用Scan.java的formatTime()方法转换成分秒
        mName.setText(m_Name);                                 //ToolBar中间设置歌曲名字
        mSinger.setText(m_Singer);                             //ToolBar右侧设置歌手名字
        mDurationTime.setText(m_Duration);                     //SeekBar右侧设置成歌曲长度
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
            mSeekBar.setMax(mMediaPlayer.getDuration());                      //SeekBar长度设置为当前歌曲长度
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
        mMediaPlayer.start();
    }

    private void playNextSong() {                                    //播放下一首哥事件
        mMediaPlayer.stop();                                         //停止当前mMediaPlayer
        mMediaPlayer.reset();                                        //重置mMediaPlayer
        mGetPosition = mGetPosition+1;                               //position值+1
        if (mGetPosition>=0){                                        //如果position值+1超出总歌曲数量，则绝对值除余处理
            mGetPosition=abs(mGetPosition)%m_size;
        }
        refresh();                                                    //刷新操作
        initMediaPlayer(m_Path);                                      //根据文件路径载入歌曲
        searchLrc();                                                  //寻找歌词
        mMediaPlayer.start();                                         //开始播放
    }
    private void checkState(){                                        //当歌曲播放结束后，检查底部三个播放模式按钮的状态，根据不同状态进行不同播放
        if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==0)){
            playNextSong();
        }else if ((REPEAT_STATE==1)&&(SHUFFLE_STATE==0)){           //单曲循环
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
            mMediaPlayer.start();
        }else if ((REPEAT_STATE==0)&&(SHUFFLE_STATE==1)){           //随机播放采用随机数赋值给POSITION
            mMediaPlayer.reset();
            int mRandomPosition=new Random().nextInt(1000);
            mRandomPosition=abs(mRandomPosition)%m_size;
            if (mRandomPosition==mGetPosition){
                mRandomPosition=mRandomPosition+1;
            }
            music=Scan.getMusicData(context).get(mRandomPosition);
            m_Path=music.getPath();
            if (m_Path.contains(TS)){                               //以下为为不同歌曲名后缀处理，获得相对应的路径和名字
                m_Name=music.getName().substring(0,music.getName().length()-3);
                m_Path_Lrc=m_Path.substring(0,m_Path.length()-3)+".lrc";
            }else if (m_Path.contains(FLAC)||m_Path.contains(MXMF)){
                m_Name=music.getName().substring(0,music.getName().length()-5);
                m_Path_Lrc=m_Path.substring(0,m_Path.length()-5)+".lrc";
            }else if (m_Path.contains(RTTTL)){
                m_Name=music.getName().substring(0,music.getName().length()-6);
                m_Path_Lrc=m_Path.substring(0,m_Path.length()-6)+".lrc";
            }
            m_Path_Lrc=m_Path.substring(0,m_Path.length()-4)+".lrc";
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
    protected void onDestroy() {                   //onDestroy()销毁mMediaPlayer和mHandler
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
