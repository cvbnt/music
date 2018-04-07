package t.n.b.v.c.music;


import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    private List<Music> mList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        mDrawerLayout=findViewById(R.id.drawer_layout);
        mRecyclerView=findViewById(R.id.musicRecycler);
        mToolBar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);
        }
        mList=Scan.getMusicData(context);
        Adaper mAdapter=new Adaper(mList,context);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}
