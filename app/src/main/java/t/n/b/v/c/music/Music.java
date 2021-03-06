package t.n.b.v.c.music;

public class Music {    //音乐文件具有相关属性，有名字，歌手，文件路径，文件大小，歌曲长度(单位为毫秒)
    public String name;
    public String singer;
    public String path;
    public long size;
    public int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
