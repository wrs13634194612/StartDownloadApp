package comtesttest.example.admin.myapplication.model;

/**
 * Created by wrs on 2019/6/26,17:51
 * projectName: Testz
 * packageName: com.example.administrator.testz.model
 */
public class VideoBean {
    String videoUrl;        // 作业视频URL
    String shootingTime;    //拍摄时间
    int videoDuration;      //视频时长，单位秒

    public VideoBean(String videoUrl, String shootingTime, int videoDuration) {
        this.videoUrl = videoUrl;
        this.shootingTime = shootingTime;
        this.videoDuration = videoDuration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getShootingTime() {
        return shootingTime;
    }

    public void setShootingTime(String shootingTime) {
        this.shootingTime = shootingTime;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }
}
