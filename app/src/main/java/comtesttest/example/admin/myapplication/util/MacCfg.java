package comtesttest.example.admin.myapplication.util;


import android.Manifest;

/**
 * Created by wrs on 2019/6/11,15:45
 * projectName: UFI_Machinenest
 * packageName: com.ufi.pdioms.machinenest.mac
 */
public class MacCfg {
    //netty
    public static final String HOST = "10.0.2.208";  //服务器的ip地址
    public static final int PORT = 20803;              ///指定的端口号
   //辅助程序客户端给服务端提交的字段信息
    public static final String DownloadSuccess = "downloadsuccess";  //apk下载成功
    public static final String DownloadFail = "downloadfail";    //apk下载失败
    public static final String InstallSuccess = "installsuccess";  //新版apk安装成功
    public static final String InstallFail = "installfail";  //新版apk安装失败
    // 服务端协议
    public static final String Update = "update";  //服务端更新指令
    public static final String Install = "install";  //服务端安装指令
    public static final String OpenApp = "openapp";  //打开机巢app指令

    public static final String RTMP_URL = "rtmp://118.24.55.19:1935/live/test";        // 无人机推流地址
    //====固定下发指令
    public static final int PROTOCOL_LENGTH_06 = 0x06;        // 协议长度 6
    public static final int PROTOCOL_LENGTH_07 = 0x07;        // 协议长度 7
    public static final int PROTOCOL_LENGTH_10 = 0x0a;        // 协议长度 10
    public static final int PROTOCOL_LENGTH_13 = 0x0d;        // 协议长度 13
    public static final int PROTOCOL_LENGTH_22 = 0x16;        // 协议长度 22


    public static final int WRITE_CLOSE_DATA = 0x55;            // 关闭动作
    public static final int WRITE_OPEN_DATA = 0xAA;             // 打开动作

    public static final int HANGAR = 0x01;            // 机库控制码              下发协议长度7             =应答协议长度7
    public static final int REMOTE_POWER = 0x02;            // 遥控器电源        下发协议长度7        =应答协议长度7
    public static final int DRONE_CHARGER_POWER = 0x03;    //无人机充电器电源   下发协议长度7          =应答协议长度7
    public static final int DRONE_POWER = 0x04;            //无人机机上电源      下发协议长度7        =应答协议长度7
    public static final int HARDWARE_STATE = 0x05;            //硬件控制状态    下发协议长度6       =应答协议长度13
    public static final int DRONE_REIGN_STATE = 0x07;        //无人机在位状态  下发协议长度6          =应答协议长度7
    public static final int WEATHER_DATA = 0x10;            //气象站数据       下发协议长度6           =应答协议长度22
    public static final int HANGAR_ENVIRONMENT_DATA = 0x11;    //机库内部当前温湿度数据   下发协议长度6    =应答协议长度10
    public static final int SELF_TEST = 0x20;            //自检测试   下发协议长度6    =应答协议长度10
    public static final int HEARTBEAT_PROTOCOL = 0xA0;            //心跳协议  下发协议长度7 == 应答协议长度22
    public static final int Alarm_PROTOCOL = 0xF0;            //报警协议 保留

    //三种不同的情况 指令内容和长度都不同
    // generateCommand((byte) 0x03, new byte[]{0x55,0x45});
    //  generateCommand((byte) 0x03, new byte[]{0x55});
    //    generateCommand((byte) 0x01, new byte[]{});

    //netty协议 ACTION
    public static final String HEARTBEAT = "HEARTBEAT";        // 心跳
    public static final String NEST_STATISTICS_INFO = "NEST_STATISTICS_INFO";        // 机巢静态信息
    public static final String NEST_METEOROLOGICAL_INFO = "NEST_METEOROLOGICAL_INFO";        // 机巢气象信息
    public static final String NEST_STATUS_INFO = "NEST_STATUS_INFO";        //机巢状态信息
    public static final String DRONE_DYNAMIC_INFO = "DRONE_DYNAMIC_INFO";        // 无人机动态信息
    public static final String ALARM_INFO = "ALARM_INFO";        // 告警信息
    public static final String TASK_EXECUTE_STATUS = "TASK_EXECUTE_STATUS";        //作业执行状态
    public static final String UPLOAD_TASK_EXTRA = "UPLOAD_TASK_EXTRA";        // 上传作业附加信息
    public static final String OPERATION_LOG = "OPERATION_LOG";        // 操作日志
    public static final String CONSOLE_OPEN = "CONSOLE_OPEN";        //打开控制台
    public static final String CONSOLE_EXIT = "CONSOLE_EXIT";        // 退出控制台
    public static final String TASK_PREPARE = "TASK_PREPARE";        // 准备作业
    public static final String TASK_COMMAND = "TASK_COMMAND";        // 下发作业指令
    public static final String deviceSn = "113126161323";        // 机巢SN 每台机巢对应一个sn


    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
}

