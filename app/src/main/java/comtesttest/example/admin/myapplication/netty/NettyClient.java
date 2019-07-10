package comtesttest.example.admin.myapplication.netty;

/**
 * Created by wrs on 2019/6/26,11:43
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */


import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import comtesttest.example.admin.myapplication.util.MacCfg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import comtesttest.example.admin.myapplication.model.ConsoleBean;
import comtesttest.example.admin.myapplication.model.HeartBeatBean;
import comtesttest.example.admin.myapplication.model.PartBean;
import comtesttest.example.admin.myapplication.model.PictureBean;
import comtesttest.example.admin.myapplication.model.VideoBean;

//wangnetty
public class NettyClient {
    private static final String TAG = NettyClient.class.getSimpleName();
    private static final long RETRY_TIME = 5 * 1000L; // 定时重新连接时间
    private static final int CONNECT_TIMEOUT = 10 * 1000; // TCP连接超时时间, 10秒
    private boolean isRunning = true;
    private String host;
    private int port;
    private boolean isConnected = false;
    private Channel channel;
    private NettyClientHandler handler;
    private NettyListener listener;
    private MessageListener messageListener;
    private Bootstrap b = null;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning && !isConnected) {
                connectToServer(host, port);
                try {
                    Thread.sleep(RETRY_TIME);
                } catch (Exception e) {

                }
            }
        }
    };

    public void connect() {
        Thread retryThread = new Thread(connectRunnable);
        retryThread.start();
    }


    public void connectToServer(final String host, int port) {
        Log.e(TAG, "开始连接...");
        // NioEventLoopGroup是一个处理I / O操作的多线程事件循环
        EventLoopGroup workerGroup = new NioEventLoopGroup();
       handler =  new NettyClientHandler(messageListener, NettyClient.this);
        try {
            b = new Bootstrap();
            b.group(workerGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //解决TCP粘包拆包的问题，以特定的字符结尾（$_）
//                        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                         //   pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                            //字符串解码和编码
                         pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                         pipeline.addLast("decoder", new StringDecoder());
                         pipeline.addLast("encoder", new StringEncoder());
                            //心跳检测
                            pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                            //客户端的逻辑
                            pipeline.addLast("handler",handler);

                        }
                    });

            //在这个地方进行的回调操作
            ChannelFuture f = b.connect(host, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Log.e(TAG, "连接成功");
                        listener.onConnected();
                    } else {
                        Log.e(TAG, "连接失败");
                        listener.onDisConnect();
                    }
                }
            }).sync();
            isConnected = true;
            channel = f.channel();
            channel.closeFuture().sync();
            listener.onDisConnect();
            isConnected = false;
        } catch (Exception e) {
            isConnected = false;
            Log.e(TAG, "Connect error : " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }



    //1.心跳 APP连接维持检测，每30秒上报一次。
    public void sendHeartBeatData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn", MacCfg.deviceSn);
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.HEARTBEAT, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendHeartBeatData:" + userJson);
        sendData(userJson);
    }

    //2.机巢静态信息  NEST_STATISTICS_INFO
    public void sendNeststationInfo() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("hardwareVersion","1.4.0"); //硬件版本
        data.put("firmwareVersion","1.0.1"); //固件版本
        data.put("longitude",10.51231512);      //机巢所在地理位置经度
        data.put("latitude",20.1391035);       //机巢所在地理位置纬度
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.NEST_STATISTICS_INFO, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendNeststationInfo:" + userJson);
        sendData(userJson);
    }

    //3.机巢气象信息  NEST_METEOROLOGICAL_INFO
    public void sendNestMeteorologicalInfo() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("temperature",23.5); //温度℃
        data.put("relativeHumidity","1.0.1"); //相对湿度%RH
        data.put("windSpeed",5.1);      //风速m/s
        data.put("windDirection","东风");       //风向
        data.put("illumination",132808);       //光照度 Lux
        data.put("rsStatus",true);       // 雨雪，true为正常，false为告警
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.NEST_METEOROLOGICAL_INFO, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendNestMeteorologicalInfo:" + userJson);
        sendData(userJson);
    }

    //4.机巢状态信息  NEST_STATUS_INFO
    public void sendNestStatusInfo() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("doorOpenStatus",true); //机巢门，true为开启，false为关闭
        data.put("droneStatus",true); //无人机在位状态，true为在位，false为不在位
        data.put("droneChargeStatus",true);      //无人机充电状态，true为充电中，false为未充电
        data.put("dronePowerStatus",true);       //无人机电源状态，true为已打开，false为已关闭
        data.put("droneControlStatus",true);       //无人机遥控器， true为已打开，false为已关闭
        data.put("droneChargerStatus",true);       // 无人机充电器， true为已打开，false为已关闭
        data.put("pushFlowStatus",true);       // 是否推流状态，true为已开启，false为已关闭
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.NEST_STATUS_INFO, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendNestStatusInfo:" + userJson);
        sendData(userJson);
    }

    // 5.无人机动态信息 DRONE_DYNAMIC_INFO
    public void sendDroneDynamicInfo() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("roll",25.6); //翻滚角
        data.put("yaw",33.6); //偏航角
        data.put("pitch",45.1);      //俯仰角
        data.put("electricity",78.6);       //电量，百分比
        data.put("voltage",40);       //电压，单位V
        data.put("compassStatus",true);       // 指南针状态，true为正常，false为异常
        data.put("compassException","");       // 指南针状态异常时上报异常原因>
        data.put("imuStatus",true);       //imu状态，true为正常，false为异常
        data.put("imuException","");       //imu状态异常时上报异常原因
        data.put("longitude",17.61316);       // 无人机经度
        data.put("latitude",61.691232);       // 无人机纬度
        data.put("height",39.5);       //高度，单位m
        data.put("droneSpeed",5.1);       //无人机飞行速度，单位m/s
        data.put("remainFlightDuration",24);       // 剩余电量可飞行时间，单位分钟
        data.put("gts",61.5);       // 图传信号，百分比
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.DRONE_DYNAMIC_INFO, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendDroneDynamicInfo:" + userJson);
        sendData(userJson);
    }


    // 6.告警信息 ALARM_INFO
    public void sendAlarmInfo() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("alarmName","告警名称"); //告警名称
        data.put("alarmTime","2019-06-14 15:51:22"); //告警时间
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.ALARM_INFO, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendAlarmInfo:" + userJson);
        sendData(userJson);
    }

    //7.作业执行状态 TASK_EXECUTE_STATUS
    public void sendTaskExecuteStatus() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("taskRecordId",1); //作业记录ID
        data.put("taskStatus","IDLE"); //执行状态，可选值：IDLE（空闲）、PREPARING（准备中）、READY（准备就绪，可以开始作业）、IN_OPERATION（作业中）、PAUSED（作业已暂停）、UPLOADING（上传中）
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.TASK_EXECUTE_STATUS, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendTaskExecuteStatus:" + userJson);
        sendData(userJson);
    }

    //8. 作业附加信息  UPLOAD_TASK_EXTRA
    public void sendUploadTaskExtra() {
        List<VideoBean> listVideo = new ArrayList<>();
        listVideo.add(new VideoBean("http://www.example.com/1.mp4","2019-06-15 15:51:21",51));
        List<PictureBean> pictureVideo = new ArrayList<>();
        pictureVideo.add(new PictureBean("http://www.example.com/1.jpg",15.61321,13.616123));
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("taskRecordId",1); //作业记录ID
        data.put("videos",listVideo);   // 作业视频列表
        data.put("pictures",pictureVideo);  //作业照片列表
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.UPLOAD_TASK_EXTRA, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendUploadTaskExtra:" + userJson);
        sendData(userJson);
    }

    // 9 操作日志  OPERATION_LOG
    public void sendOperationLog() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceSn",MacCfg.deviceSn);   //机巢SN
        data.put("operateTime","2019-06-14 15:31:23"); //操作时间
        data.put("operation","操作内容");  //操作内容
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.OPERATION_LOG, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendOperationLog:" + userJson);
        sendData(userJson);
    }

    //10 打开控制台  CONSOLE_OPEN
    public void sendConsoleOpen() {
        ConsoleBean userSimple = new ConsoleBean(MacCfg.CONSOLE_OPEN);
        Gson gson = new Gson();
        String userJson = gson.toJson(userSimple);
        System.out.println("sendConsoleOpen:" + userJson);
        sendData(userJson);
    }

    //11 退出控制台 CONSOLE_EXIT
    public void sendConsoleExit() {
        ConsoleBean userSimple = new ConsoleBean(MacCfg.CONSOLE_EXIT);
        Gson gson = new Gson();
        String userJson = gson.toJson(userSimple);
        System.out.println("sendConsoleExit:" + userJson);
        sendData(userJson);
    }

    //12.准备作业  TASK_PREPARE  给机巢下发作业，以准备好开始作业
    public void sendTaskPrepare() {
        List<PartBean> listPart = new ArrayList<>();
        listPart.add(new PartBean("1号杆塔",61.61313,17.69131,300.5,25.6,23.5,100));
        HashMap<String, Object> data = new HashMap<>();
        data.put("taskRecordId",1); //作业记录ID
        data.put("parts",listPart);   // 任务航点列表，数组顺序即为航点顺序
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.TASK_PREPARE, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendTaskPrepare:" + userJson);
        sendData(userJson);
    }

    //13. TASK_COMMAND 下发作业指令
    public void sendTaskCommand() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("taskRecordId",1);   //作业记录ID
        data.put("command","START"); //作业指令，可选值：START（开始作业）、PAUSE（暂停作业）、STOP（停止作业）
        HeartBeatBean heartBeatBean = new HeartBeatBean(MacCfg.TASK_COMMAND, data);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("sendTaskCommand:" + userJson);
        sendData(userJson);
    }

    //安装状态
    public void sendInstanller(String downloadFail) {
        sendData(downloadFail);
    }

    public void sendData(String userJson) {
        if (null != handler) {
            handler.sendData(userJson);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setNettyListener(NettyListener listener) {
        this.listener = listener;
    }

    public void start() {
        System.out.println("client start()");
        ChannelFuture f = b.connect(host, port);
        //断线重连
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.err.println("not connect service...");
                            start();
                        }
                    }, 1L, TimeUnit.SECONDS);
                } else {
                    channel = channelFuture.channel();
                    System.err.println("connected...");
                }
            }
        });
    }

}
