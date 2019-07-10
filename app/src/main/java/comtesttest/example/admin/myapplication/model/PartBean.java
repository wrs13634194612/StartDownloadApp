package comtesttest.example.admin.myapplication.model;

/**
 * Created by wrs on 2019/6/26,18:15
 * projectName: Testz
 * packageName: com.example.administrator.model
 */
public class PartBean {
    String towerNo;
    double longitude;
    double latitude;
    double height;
    double courseAngle;
    double pitchAngle;
    int focusLength;

    public PartBean(String towerNo, double longitude, double latitude, double height, double courseAngle, double pitchAngle, int focusLength) {
        this.towerNo = towerNo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.height = height;
        this.courseAngle = courseAngle;
        this.pitchAngle = pitchAngle;
        this.focusLength = focusLength;
    }

    public String getTowerNo() {
        return towerNo;
    }

    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCourseAngle() {
        return courseAngle;
    }

    public void setCourseAngle(double courseAngle) {
        this.courseAngle = courseAngle;
    }

    public double getPitchAngle() {
        return pitchAngle;
    }

    public void setPitchAngle(double pitchAngle) {
        this.pitchAngle = pitchAngle;
    }

    public int getFocusLength() {
        return focusLength;
    }

    public void setFocusLength(int focusLength) {
        this.focusLength = focusLength;
    }
}
