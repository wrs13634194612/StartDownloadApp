package comtesttest.example.admin.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import comtesttest.example.admin.myapplication.installer.AutoInstaller;
import comtesttest.example.admin.myapplication.model.BatteryBox;
import comtesttest.example.admin.myapplication.model.SlotDynamicInfo;
import comtesttest.example.admin.myapplication.model.SlotStaticInfo;
import comtesttest.example.admin.myapplication.model.StringEvent;
import comtesttest.example.admin.myapplication.netty.MessageListener;
import comtesttest.example.admin.myapplication.netty.NettyClient;
import comtesttest.example.admin.myapplication.netty.NettyListener;
import comtesttest.example.admin.myapplication.service.DaemonService;
import comtesttest.example.admin.myapplication.service.JobWakeUpService;
import comtesttest.example.admin.myapplication.service.WorkService;
import comtesttest.example.admin.myapplication.util.MacCfg;
import comtesttest.example.admin.myapplication.util.PermissionsChecker;
import comtesttest.example.admin.myapplication.util.ProgressDialogUtils;
import comtesttest.example.admin.myapplication.util.SPUtils;


public class MainActivity extends AppCompatActivity implements MessageListener, NettyListener, View.OnClickListener {
    private ProgressDialog mProgressDialog;
    String apkName = "test.apk";
    final String FILE_DIR = "TestApk";
    String apkPath = Environment.getExternalStoragePublicDirectory(FILE_DIR) + "/" + apkName;
    //  path:/storage/emulated/0/TestApk/test.apk
    //  install msg is 	pkg: /storage/emulated/0/TestApk/test.apk
    private NettyClient mNettyClient;
    private Button button0, button1, button2, btn_install;
    public static String PACKAGE_NAME = "package";
    public static final String url = "https://raw.githubusercontent.com/wrs13634194612/Image/master/raw/test.apk"; //服务器新版apk地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        btn_install = (Button) findViewById(R.id.btn_install);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在下载");

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        btn_install.setOnClickListener(this);

        nettyConnected();  //启动netty客户端
        initService();  //多进程常驻后台
    }



    private void nettyConnected() {
        mNettyClient = new NettyClient(MacCfg.HOST, MacCfg.PORT);
        mNettyClient.setNettyListener(this);
        mNettyClient.setMessageListener(this);
        if (!mNettyClient.isConnected()) {
            mNettyClient.connect();
        }
    }


    private void initService() {
        startService(new Intent(this, WorkService.class));
        startService(new Intent(this, DaemonService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startService(new Intent(this, JobWakeUpService.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button0:
                // 1.心跳
                mNettyClient.sendHeartBeatData();
                break;
            case R.id.button1:
                // 2.机巢静态信息
                mNettyClient.sendNeststationInfo();
                break;
            case R.id.button2:
                // 3.机巢气象信息
                mNettyClient.sendNestMeteorologicalInfo();
                break;
        }
    }

    @Override
    public void onGetBoxInfo(BatteryBox boxInfo) {

    }

    @Override
    public void onGetSlotStaticData(SlotStaticInfo slotInfo) {

    }

    @Override
    public void onGetSlotDynamicData(SlotDynamicInfo dynamicSlotInfo) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //绑定eventbus
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
        getPermission(); //动态权限
    }

    @Subscribe
    public void showTextEvent(StringEvent stringEvent) {
        System.out.println("MainActivity EventBus: " + stringEvent.getTexto());
        switch (stringEvent.getTexto()) {
            case MacCfg.Update:
                //开始下载
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNewApk();
                    }
                });
                break;
            case MacCfg.Install:
                //开始安装
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getInstall();
                    }
                });
                break;
            case MacCfg.OpenApp:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getOpenApp();
                    }
                });
                break;
        }
    }

    public void getNewApk() {
        final File saveFile = new File(apkPath);
        HttpRequest.download(url, saveFile, new FileDownloadCallback() {
            //开始下载
            @Override
            public void onStart() {
                super.onStart();
                System.out.println("start");
                ProgressDialogUtils.getInstance().show(
                        MainActivity.this,
                        "版本更新");
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                System.out.println("process:" + progress);
                ProgressDialogUtils.getInstance().setNum(progress);
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
                System.out.println("fail");
                Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.getInstance().dismiss();
                mNettyClient.sendInstanller(MacCfg.DownloadFail);
            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                super.onDone();
                System.out.println("success");
                Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.getInstance().dismiss();
                mNettyClient.sendInstanller(MacCfg.DownloadSuccess);
            }
        });


    }

    //静默安装新版apk
    public void getInstall() {
        /* 方案一: 默认安装器 */
        AutoInstaller installer = AutoInstaller.getDefault(MainActivity.this);
        installer.install(apkPath);
//        installer.installFromUrl(APK_URL);
        installer.setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
            @Override
            public void onStart() {
                System.out.println("start install");
                mProgressDialog.show();
            }

            @Override
            public void onComplete() {
                System.out.println("close dialog");
                mProgressDialog.dismiss();
                mNettyClient.sendInstanller(MacCfg.InstallSuccess);
            }

            @Override
            public void onNeed2OpenService() {
                Toast.makeText(MainActivity.this, "请打开辅助功能服务", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void needPermission() {
                Toast.makeText(MainActivity.this, "需要申请存储空间权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //检查所需的全部权限
    public boolean getPermission() {
        PermissionsChecker mPermissionsChecker = new PermissionsChecker(MainActivity.this);
        if (mPermissionsChecker.lacksPermissions(MacCfg.PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, MacCfg.PERMISSIONS, 0x12);
            return false;
        }
        return true;
    }

  /*  这个是销毁activity方法
  @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this); //解绑
        super.onDestroy();
    }*/


    //打开指定app
    public void getOpenApp() {
        if (SPUtils.get(this, PACKAGE_NAME, "").equals("")) {
            //默认包名为网易云音乐   com.netease.cloudmusic
            //qq  com.tencent.mobileqq
            SPUtils.put(this, PACKAGE_NAME, "top.wuhaojie.installer");
        }
        try {
            Toast.makeText(getApplicationContext(), "启动APP成功", Toast.LENGTH_SHORT).show();
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(SPUtils.get(getApplicationContext(),
                    MainActivity.PACKAGE_NAME, "").toString());
            startActivity(LaunchIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "启动APP失败", Toast.LENGTH_SHORT).show();
        }
    }

}
