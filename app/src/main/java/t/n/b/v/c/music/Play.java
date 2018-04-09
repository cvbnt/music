package t.n.b.v.c.music;

import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

public class Play extends AppCompatActivity {
    private SeekBar mSeekBar;
    private ImageButton mPrevious;
    private ImageButton mPlay;
    private ImageButton mNext;
    private TextView mNowTime;
    private TextView mDurationTime;
    private android.support.v7.widget.Toolbar mToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        fvbi();
        setSupportActionBar(mToolBar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void fvbi() {
        mSeekBar=findViewById(R.id.seekBar);
        mPrevious=findViewById(R.id.previousButton);
        mNext=findViewById(R.id.nextButton);
        mPlay=findViewById(R.id.playButton);
        mNowTime=findViewById(R.id.nowTime);
        mDurationTime=findViewById(R.id.durationTime);
        mToolBar=findViewById(R.id.toolbar);
    }
}
