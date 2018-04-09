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

public class Adaper extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private Context mContext;
    private List<Music> mList;
//    private OnMusicItemListener mListener;
    public class MyViewHolder extends RecyclerView.ViewHolder {
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
    public Adaper(List<Music> list,Context context){
        mList=list;
        mContext=context;
  //      mListener=listener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        final Music music=mList.get(position);
        ((MyViewHolder)holder).mPosition.setText(String.valueOf(position+1));
        ((MyViewHolder)holder).mName.setText(music.getName());
        ((MyViewHolder)holder).mSinger.setText(music.getSinger());
        ((MyViewHolder)holder).mDuration.setText(Scan.formatTime(music.getDuration()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnMusicItemListener {
        void onMusicClick(int position, Music music);
    }
}
