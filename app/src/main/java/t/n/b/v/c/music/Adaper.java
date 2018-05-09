package t.n.b.v.c.music;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.List;

public class Adaper extends RecyclerView.Adapter <RecyclerView.ViewHolder>implements View.OnClickListener{
    private Context mContext;
    private List<Music> mList;
    private OnItemClickListener mOnItemClickListener = null;
    @Override
    public void onClick(View view) {                                     //设计点击
        if (mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(view,(int)view.getTag());   //传入view和view的Tag
        }
    }
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {  //设计子项点击监听器
        this.mOnItemClickListener=mOnItemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {     //自定义MyViewHolder()方法绑定子项控件ID
        View MyView;
        TextView mPosition;
        TextView mName;
        TextView mSinger;
        TextView mDuration;
        public MyViewHolder(View itemView) {
            super(itemView);
            MyView=itemView;
            mPosition=itemView.findViewById(R.id.item_music_num_tv);
            mName=itemView.findViewById(R.id.item_music_center_name);
            mSinger=itemView.findViewById(R.id.item_music_center_author);
            mDuration=itemView.findViewById(R.id.item_music_time);
        }
    }
    public Adaper(List<Music> list,Context context){                //构造方法
        mList=list;
        mContext=context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {      //onCreateViewHolder()传入子项布局
   //     return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {  //绑定数据
        final Music music=mList.get(position);                                     //根据position获取数据
        ((MyViewHolder)holder).mPosition.setText(String.valueOf(position+1));      //控件设置显示数据
        ((MyViewHolder)holder).mName.setText(music.getName());
        ((MyViewHolder)holder).mSinger.setText(music.getSinger());
        ((MyViewHolder)holder).mDuration.setText(Scan.formatTime(music.getDuration()));
        ((MyViewHolder)holder).itemView.setTag(position);
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }             //返回ArrayList长度
    public static interface OnItemClickListener {                 //设置点击接口
        void onItemClick(View view , int position);
    }
}
