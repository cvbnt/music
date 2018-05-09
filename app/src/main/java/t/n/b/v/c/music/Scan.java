package t.n.b.v.c.music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scan {
    public static List<Music> getMusicData(Context context) {        //getMusicData()方法为扫描本地音乐文件，
        List<Music> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);  //query搜索本地音频文件
        if (cursor != null) {
            while (cursor.moveToNext()) {                                  //指针每找到一个音乐文件
                Music music = new Music();                                 //创造单例对象
                music.name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));  //单例对象赋值名字，歌手名，文件路径，歌曲长度，文件大小
                music.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                music.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                music.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                music.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (music.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (music.name.contains("-")) {
                        String[] str = music.name.split("-");
                        music.singer = str[0];
                        music.name = str[1];
                    }
                    list.add(music);            //ArrayList内添加music
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;                           //返回一个ArrayList
    }
    public static String formatTime(long time) {   //时间转换，将毫秒转换为分和秒
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}