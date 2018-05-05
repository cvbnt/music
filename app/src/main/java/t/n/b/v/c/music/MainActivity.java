package t.n.b.v.c.music;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private Context context;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    private NavigationView navView;
    private List<Music> mList=new ArrayList<>();      //mList为获取到的音乐信息ArrayList
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        fvbi();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                return true;
            }
        });
        setSupportActionBar(mToolBar);   //设置ToolBar
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);              //ToolBar设置一个Home按钮
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);     //Home按钮设置图片
        }
        mList=Scan.getMusicData(context);                          //mList调用Scan类的getMusicData()方法扫描本地文件
        Adaper mAdapter=new Adaper(mList,context);                 //设置mAdapter，参数为mList，context
        mAdapter.setmOnItemClickListener(new Adaper.OnItemClickListener() {            //mAdapter设置子项点击事件
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(MainActivity.this,Play.class); //跳转到Play所在的Activity
                intent.putExtra("extra_position",position);                       //intent附带传递子项的position
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);                                //mRecyclerView设置适配器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));  //mRecyclerView设置布局方向，默认纵向
    }

    private void fvbi() {
        mDrawerLayout=findViewById(R.id.drawer_layout);
        mRecyclerView=findViewById(R.id.musicRecycler);
        mToolBar=findViewById(R.id.toolbar);
        navView=findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_stop);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {      //设置ToolBar的item点击事件
        switch (item.getItemId()){
            case android.R.id.home:                            //点击R.id.home
                mDrawerLayout.openDrawer(GravityCompat.START); //打开DrawerLayout
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
