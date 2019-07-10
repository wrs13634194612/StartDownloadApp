package comtesttest.example.admin.myapplication;


import android.app.Application;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * Created by wrs on 2019/6/27,19:00
 * projectName: AppUpdateUtil-master
 * packageName: com.maiml.updatedemo
 * 不会阻塞  只是初始化  传一下值 用于下载文件
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
    }
}
