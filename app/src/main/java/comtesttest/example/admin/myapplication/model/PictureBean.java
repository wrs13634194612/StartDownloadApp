package comtesttest.example.admin.myapplication.model;

/**
 * Created by wrs on 2019/6/26,17:55
 * projectName: Testz
 * packageName: com.example.administrator.testz.model
 */
public class PictureBean {
    String pictureUrl;      //作业照片URL
    double longitude;       //照片拍摄地点经度
    double latitude;        //照片拍摄地点纬度

    public PictureBean(String pictureUrl, double longitude, double latitude) {
        this.pictureUrl = pictureUrl;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
