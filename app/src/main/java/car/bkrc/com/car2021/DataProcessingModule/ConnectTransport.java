package car.bkrc.com.car2021.DataProcessingModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bkrc.car2019.tesseract.MainActivity;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.tencent.yolov5ncnn.YoloV5Ncnn;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.BitmapUtils;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.CarPlate.carPlate;
import car.bkrc.com.car2021.Utils.CarRgbLight.CarRgbLight;
import car.bkrc.com.car2021.Utils.CarShape.CarShape;
import car.bkrc.com.car2021.Utils.OtherUtil.SerialPort;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.FragmentView.LeftFragment;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.Utils.TrafficRecUtil.ColorRecognition;
import car.bkrc.com.car2021.ViewAdapter.OtherAdapter;
import car.bkrc.com.car2021.yolov5ncnn.Yolov5Fragment;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.Connect_Transport;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.IPCamera;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.toastUtil;
import static car.bkrc.com.car2021.FragmentView.LeftFragment.cameraCommandUtil;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.QrFlag;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.result_qr;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.rgb_result;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.shapeNum;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_x;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_y;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_min;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_min;

/**
 * Socket数据处理类
 */
public class ConnectTransport {
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    public byte[] rbyte = new byte[50];
    private Handler reHandler;
    public short TYPE = 0xAA;
    public short TYPE2 = 0xBB;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM = 0x00;

    public static RightAutoFragment RightAutoFragment;
    public static ColorRecognition ColorRecognition;
    public static LeftFragment left_Fragment;//加载类;
    public static QR_Recognition qr_recognition;
    public  OtherAdapter OtherAdapter;
    public static CarShape carShape;
    public static carPlate carPlate;
    public static CarRgbLight CarRgbLight;
    public static MainActivity tesseract;

    private static OutputStream SerialOutputStream;
    private InputStream SerialInputStream;
    private boolean Firstdestroy = false;  ////Firstactivity 是否已销毁了
    private int retx=0;
    private int rety=0;
    private int w=0;
    private int h=0;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    private static int picShapeNum;
    private static int blacknum,whitenum,rednum,greennum,bluenum,yellownum,purplenum,cyannum;
    private static int tri,rect,square,circle,star,rhomb;
    private static int black_tri, black_rect,black_cir,black_square,black_rhomb,black_star;
    private static int white_tri, white_rect,white_square,white_rhomb,white_star,white_cir;
    private static int red_tri, red_rect,red_square,red_rhomb,red_star,red_cir;
    private static int green_tri, green_rect,green_square,green_rhomb,green_star,green_cir;
    private static int blue_tri, blue_rect,blue_square,blue_rhomb,blue_star,blue_cir;
    private static int yellow_tri, yellow_rect,yellow_square,yellow_rhomb,yellow_star,yellow_cir;
    private static int purple_tri, purple_rect,purple_square,purple_rhomb,purple_star,purple_cir;
    private static int cyan_tri, cyan_rect,cyan_square,cyan_rhomb,cyan_star,cyan_cir;

    private static String PlateNum1=null;//车牌识别结果1
    private static String PlateNum2=null;//车牌识别结果2


    private void shapeZero(){
        picShapeNum=blacknum=whitenum=rednum=greennum=bluenum=yellownum=purplenum=cyannum=0;
        tri=rect=square=circle=star=rhomb=0;
        black_tri=black_rect=black_square=black_rhomb=black_star=black_cir=0;
        white_tri= white_rect=white_square=white_rhomb=white_star=white_cir=0;
        red_tri= red_rect=red_square=red_rhomb=red_star=red_cir=0;
        green_tri= green_rect=green_square=green_rhomb=green_star=green_cir=0;
        blue_tri= blue_rect=blue_square=blue_rhomb=blue_star=blue_cir=0;
        yellow_tri=yellow_rect=yellow_square=yellow_rhomb=yellow_star=yellow_cir=0;
        purple_tri= purple_rect=purple_square=purple_rhomb=purple_star=purple_cir=0;
        cyan_tri=cyan_rect=cyan_square=cyan_rhomb=cyan_star=cyan_cir=0;
    }

    public void destory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                bInputStream.close();
                bOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void connect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            Firstdestroy = false;
            int port = 60000;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            if (!inputDataState) {
                reThread();
            }
            EventBus.getDefault().post(new DataRefreshBean(3));
        } catch (SocketException ignored) {
            EventBus.getDefault().post(new DataRefreshBean(4));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void serial_connect(Handler reHandler) {
        this.reHandler = reHandler;
        try {
            int baudrate = 115200;
            String path = "/dev/ttyS4";
            SerialPort mSerialPort = new SerialPort(new File(path), baudrate, 0);
            SerialOutputStream = mSerialPort.getOutputStream();
            SerialInputStream = mSerialPort.getInputStream();
            //new Thread(new SerialRunnable()).start();
            //reThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XcApplication.executorServicetor.execute(new SerialRunnable());
        //new Thread(new serialRunnable()).start();
    }

    byte[] serialreadbyte = new byte[50];

    class SerialRunnable implements Runnable {
        @Override
        public void run() {
            while (SerialInputStream != null) {
                try {
                    int num = SerialInputStream.read(serialreadbyte);
                    // String  readserialstr =new String(serialreadbyte);
                    String readserialstr = new String(serialreadbyte, 0, num, "utf-8");
                    Log.e("----serialreadbyte----", "******" + readserialstr);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = serialreadbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean inputDataState = false;

    private void reThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto1-generated method stub
                while (socket != null && !socket.isClosed()) {
                    if (Firstdestroy == true)  //Firstactivity 已销毁了
                    {
                        break;
                    }
                    try {
                        inputDataState = true;
                        bInputStream.read(rbyte);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = rbyte;//接收小车的命令
                        reHandler.sendMessage(msg);
                    } catch (SocketException ignored) {
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (UnsupportedOperationException ignored) {
                        inputDataState = false;
                    }
                }
            }
        }).start();//开启线程
    }



    private void send() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(sbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {

            }
    }


    public Handler cameraSet = new Handler(){//控制摄像头
        @Override
        public void handleMessage(@NonNull Message msg) {
            XcApplication.executorServicetor.execute(new Runnable() {
                public void run() {
                    switch (msg.what) {
                        //上下左右转动
                        case 1:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 0, 1);  //向上
                            break;
                        case 2:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 2, 1);  //向下
                            break;
                        case 3:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 4, 1);  //向左
                            break;
                        case 4:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 6, 1);  //向右
                            break;
                        case 5:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 32, 0); // 设置预设位1
                            break;
                        case 6:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0); // 调用预设位1
                            break;
                        case 7:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 31, 0); // 摄像头复位
                            break;
                        case 8:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 36, 0); // 设置预设位2
                            break;
                        case 9:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 37, 0); // 调用预设位2
                            break;
                        case 10:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 38, 0); // 设置预设位3
                            break;
                        case 11:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 39, 0); // 调用预设位3
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    };



    private void sendSecend() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE2, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(sbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void send_voice(final byte[] textbyte) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(textbyte, 0, textbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(textbyte, 0, textbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(textbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
                Log.e("UART:", "unline");
            }
    }


    // 前进
    public void go(int sp_n, int en_n) {
        MAJOR = 0x02;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    // 后退
    public void back(int sp_n, int en_n) {
        MAJOR = 0x03;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }


    //左转
    public void left(int sp_n) {
        MAJOR = 0x04;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
    }


    // 右转
    public void right(int sp_n) {
        MAJOR = 0x05;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
    }

    // 停车
    public void stop() {
        MAJOR = 0x01;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    public static byte[] qr_resultByt;
    public static String[] qr_resultArr;
    public static String rec_result = "red";
    public static String qr_result = null;
    public static String qr_result1 = null;
    public static String qr_result2 = null;
    public static String qr_result3 = null;
    public static String qr_result4 = null;
    public static int[] Pic=null;
    private Message message;
    // 程序自动执行
    public static int mark = 0;
    public void autoDrive() {
          switch(mark)
        {

            case 1://红绿灯识别
                //以下代码通过yolov5方式，不会用到。
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                CarShape.ret_x=0;
                CarShape.ret_y=0;
                CarShape.dis_x=0;
                CarShape.dis_y=0;
                rgbReadNum = 0;
                //以上代码通过yolov5方式，不会用到。
                message = new Message();
                message.what = 1;
                task_handler.sendMessage(message);//红绿灯识别
                //task_handler.sendEmptyMessageDelayed(1, 100);//红绿灯识别，基于opencv
                mark=0;
                break;
            case 2://二维码识别
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                //RightAutoFragment.QrFlag=true;
                message = new Message();
                message.what = 2;
                task_handler.sendMessage(message);//二维码识别
                mark=0;
                break;
            case 3://形状识别
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                retx=0;
                rety=0;
                w=0;
                h=0;
                shapeZero();//形状统计数字归0
                shapeReadNum=0;
                message = new Message();
                message.what = 3;
                task_handler.sendMessage(message);//形状及颜色识别，基于opencv
                mark=0;
                break;

            case 4://车牌识别
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                CarShape.ret_x=0;
                CarShape.ret_y=0;
                CarShape.dis_x=0;
                CarShape.dis_y=0;
                shapeReadNum=0;
                message = new Message();
                message.what = 4;
                task_handler.sendMessage(message);//形状及颜色识别，基于opencv
                mark=0;
                break;


            case 20:
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                task_handler.sendEmptyMessageDelayed(20, 100);//红绿灯识别，基于opencv
                mark=0;
                break;
            default:
                break;
        }

    }

    public short[]  qr_resultSho;
    public byte[] order_data = new byte[6];
    public void Sw_algorithm(int num,String Qrvalue)//解密算法
    {
        switch(num)
        {
            case 1:				//RSA
                new algorithm().RSA_Code(Qrvalue,order_data);
                break;
            case 2:				//CRC
                new algorithm().CRC_Code(Qrvalue,order_data);
                break;
            case 3:				//仿射码
                new algorithm().Affine(Qrvalue,order_data);
                break;
        }
    }

    private int imgReadNum = 0;
    private int shapeReadNum = 0;
    private int rgbReadNum = 0;
    private int QRReadNum = 0;

    private Bitmap image1 = null;
    private Bitmap image2 = null;
    private Bitmap image3 = null;
    private Bitmap bitmapRgb = null;





    private String shapedata;
    private String[] shapedataarr;
    private short[] shapedata_sho;
    Message msg = new Message();
    @SuppressLint("HandlerLeak")
    public Handler task_handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case 1://红绿灯识别:基于yolov5
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(1000);
                    Log.d("rgb","已发送识别指令");
                    Bitmap  BitmaptrfficLight;
                    if (left_Fragment.bitmap == null) {
                        BitmaptrfficLight= Yolov5Fragment.yourSelectedImage;
                    }
                    else{
                        BitmaptrfficLight= left_Fragment.bitmap.copy(ARGB_8888, true);
                        Yolov5Fragment.iv.setImageBitmap(BitmaptrfficLight);//显示//获取更新后的图片
                    }
                    BitmapUtils.saveBitmap(BitmaptrfficLight,"/DCIM/Car/",".png");//保存图片
                    //大量训练
                    YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(BitmaptrfficLight, false);

                    for(int i=0;i<objects.length;i++)
                    {
                        if("green".equals(objects[i].label)||"green_cir".equals(objects[i].label))
                        {
                            rec_result="green";//将识别的结果放入res
                        }
                        if("red".equals(objects[i].label)||"red_cir".equals(objects[i].label))
                        {
                            rec_result="red";//将识别的结果放入res
                        }
                        if("yellow".equals(objects[i].label)||"yellow_cir".equals(objects[i].label))
                        {
                            rec_result="yellow";//将识别的结果放入res
                        }
                    }
                    switch (rec_result)
                    {
                        case "red":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            Log.d("rgb", "发送指令red");
                            break;
                        case "green":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            Log.d("rgb", "发送指令green");
                            break;

                        case "yellow":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            Log.d("rgb", "发送指令yellow");
                            break;

                        default:
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//其他情况都按红色发送识别
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//其他情况都按红色发送识别
                            break;
                    }

                    break;

                case 2://二维码识别
                    int qrnum=0;//用来存储二维码数量
                    Bitmap QRpic;//用来识别前的图片
                    Bitmap QRpic1,QRpic2,QRpic3,QRpic4;//用来存储裁切好的二维码图片
                    if (LeftFragment.bitmap!=null){
                        QRpic = LeftFragment.bitmap;
                    }
                    else{
                        QRpic= Yolov5Fragment.yourSelectedImage;
                    }

                    objects = yolov5ncnn.Detect(QRpic, false);
                    for(int i=0;i<objects.length;i++)
                    {
                        Log.d("qr", "识别结果："+objects[i].label);
                        if("QRCode".equals(objects[i].label))
                        {
                            retx=(int)objects[i].x;
                            rety=(int)objects[i].y;
                            w=(int)objects[i].w;
                            h=(int)objects[i].h;
                            qrnum++;
                            Log.d("qr", "识别的二维码范围为x:"+retx+"y:"+rety+"w:"+w+"h:"+h);
                        }
                        switch (qrnum){
                            case 1:
                                QRpic1 = CarShape.opencvCutmap(QRpic,retx,rety,w,h);//对前一张图片进行裁切
                                qr_result1=RightAutoFragment.QRReconBitmap(QRpic1);
                                Log.d("qr", "二维码识别结果为"+qr_result1);
                                break;
                            case 2:
                                QRpic2 = CarShape.opencvCutmap(QRpic,retx,rety,w,h);//对前一张图片进行裁切
                                qr_result2=RightAutoFragment.QRReconBitmap(QRpic2);
                                break;
                            case 3:
                                QRpic3 = CarShape.opencvCutmap(QRpic,retx,rety,w,h);//对前一张图片进行裁切
                                qr_result3=RightAutoFragment.QRReconBitmap(QRpic3);
                                break;
                            case 4:
                                QRpic4 = CarShape.opencvCutmap(QRpic,retx,rety,w,h);//对前一张图片进行裁切
                                qr_result4=RightAutoFragment.QRReconBitmap(QRpic4);
                                break;
                            default:
                                break;
                        }
                    }
                    qrnum=0;
//                    Log.d("yolov", "x:"+retx+"y:"+rety+"w:"+w+"h:"+h+"长度"+objects.length);
//                    RightAutoFragment.image_show.setImageBitmap(bitmap_cut);//显示裁切后的图片

//                    if (QRReadNum<5){
//                        RightAutoFragment.QrFlag=true;
//
//                        qr_result= RightAutoFragment.result_qr;
//                        QRReadNum++;
//                        if (qr_result!=null){
//                            QRReadNum=6;
//                        }
//                        task_handler.sendEmptyMessageDelayed(2, 500);//重新进入case2
//                    }
//                    else{
//                        RightAutoFragment.QrFlag=false;
                        if (qr_result1!=null) {//二维码识别的结果为字符串
                            Log.d("qr", "二维码识别结果"+qr_result1);
                            qr_resultArr=algorithm.S2Arr(qr_result1);//需要对字符串进行处理，得到字符串数组
                            Log.d("qr", "qr字符串数组"+qr_resultArr);
                            //此处，在比赛时候需要根据要求修改S2Arr里面的处理方法
                            qr_resultSho=algorithm.Arr2Sho(qr_resultArr);//字符串数组转为short数组
                            //字符串数组转化为short数组
                            Log.d("qr", "qr_resultSho"+qr_resultSho);

                            TYPE=0xAA;
                            MAJOR = 0x10;//根据题目灵活更改
                            FIRST = qr_resultSho[0];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                            SECOND = qr_resultSho[1];
                            THRID = qr_resultSho[2];
                            send();
                            Log.d("qr", "前三位已发送");
                            yanchi(500);
                            MAJOR = 0x11;//根据题目灵活更改
                            FIRST = qr_resultSho[3];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                            SECOND =qr_resultSho[4];
                            THRID = qr_resultSho[5];
                            send();
                            Log.d("qr", "后三位已发送");
                        }
//                    }

                    //Sw_algorithm(2,result_qr);					// 二维码算法选择
                    //qr_result="{0x03,0x05,0x14,0x45,0xDE,0x92}";

                    break;

                case 3://利用yolov直接识别图片信息，包含形状个数及车牌位置，准确率高
                    if(shapeReadNum<1)//有几张图片就进入几次
                    {
                        Log.d("yolov5", "进入形状识别 ");
                        Bitmap SelectedImage=null;
                        carShape=new CarShape();
                        // long current = System.currentTimeMillis();
                        if (left_Fragment.bitmap!=null){
                            SelectedImage = left_Fragment.bitmap.copy(ARGB_8888, true);
                        }
                        else{
                            SelectedImage=Yolov5Fragment.yourSelectedImage;
                         }
                        Yolov5Fragment.iv.setImageBitmap(SelectedImage);
                        objects = yolov5ncnn.Detect(SelectedImage, false);
                        Log.d("yolov", "长度"+objects.length);
                        shapeReadNum++;
                        for(int i=0;i<objects.length;i++)
                        {
                            Log.d("yolov", "标签为："+objects[i].label);
                            switch (objects[i].label){
                                /*车牌识别结果处理
                                 *适合未知车牌图形库，经过训练可较为准确提取处车牌位置
                                 * */
                                case "plate"://裁切出车牌区域,采用OCR进行识别，功能暂未实现
                                    Log.d("yolov", "x"+objects[i].x);
                                    retx=(int)objects[i].x;
                                    rety=(int)objects[i].y;
                                    w=(int)objects[i].w;
                                    h=(int)objects[i].h;
                                    Log.d("yolov", "x:"+retx+"y:"+rety+"w:"+w+"h:"+h+"长度"+objects.length);
                                    Bitmap bitmap_cut = CarShape.opencvCutmap(SelectedImage,retx,rety,w,h);;//对前一张图片进行裁切
                                    Yolov5Fragment.iv2.setImageBitmap(bitmap_cut);//显示裁切后的图片
                                    //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
                                    //tesseract=new MainActivity();
                                    //Log.d("yolov", "开始识别车牌");
                                    //String plate=tesseract.doc(bitmap_cut,"eng");
                                    //Log.d("yolov", "plate: "+plate);
                                    break;

                                    /*形状识别结果处理 */

                                    case "black_tri":     black_tri++;      break;
                                    case "black_rect":    black_rect++;     break;
                                    case "black_square":  black_square++;   break;
                                    case "black_rhomb":   black_rhomb++;    break;
                                    case "black_star":    black_star++;     break;
                                    case "black_cir":     black_cir++;      break;
                                    case "white_tri":     white_tri++;      break;
                                    case "white_rect":    white_rect++;     break;
                                    case "white_square":  white_square++;   break;
                                    case "white_rhomb":   white_rhomb++;    break;
                                    case "white_star":    white_star++;     break;
                                    case "white_cir":     white_cir++;      break;
                                    case "red_tri":     red_tri++;      break;
                                    case "red_rect":    red_rect++;     break;
                                    case "red_square":  red_square++;   break;
                                    case "red_rhomb":   red_rhomb++;    break;
                                    case "red_star":    red_star++;     break;
                                    case "red_cir":     red_cir++;      break;
                                    case "green_tri":     green_tri++;      break;
                                    case "green_rect":    green_rect++;     break;
                                    case "green_square":  green_square++;   break;
                                    case "green_rhomb":   green_rhomb++;    break;
                                    case "green_star":    green_star++;     break;
                                    case "green_cir":     green_cir++;      break;
                                    case "blue_tri":     blue_tri++;      break;
                                    case "blue_rect":    blue_rect++;     break;
                                    case "blue_square":  blue_square++;   break;
                                    case "blue_rhomb":   blue_rhomb++;    break;
                                    case "blue_star":    blue_star++;     break;
                                    case "blue_cir":     blue_cir++;      break;
                                    case "yellow_tri":     yellow_tri++;      break;
                                    case "yellow_rect":    yellow_rect++;     break;
                                    case "yellow_square":  yellow_square++;   break;
                                    case "yellow_rhomb":   yellow_rhomb++;    break;
                                    case "yellow_star":    yellow_star++;     break;
                                    case "yellow_cir":     yellow_cir++;      break;
                                    case "purple_tri":     purple_tri++;      break;
                                    case "purple_rect":    purple_rect++;     break;
                                    case "purple_square":  purple_square++;   break;
                                    case "purple_rhomb":   purple_rhomb++;    break;
                                    case "purple_star":    purple_star++;     break;
                                    case "purple_cir":     purple_cir++;      break;
                                    case "cyan_tri":     cyan_tri++;      break;
                                    case "cyan_rect":    cyan_rect++;     break;
                                    case "cyan_square":  cyan_square++;   break;
                                    case "cyan_rhomb":   cyan_rhomb++;    break;
                                    case "cyan_star":    cyan_star++;     break;
                                    case "cyan_cir":     cyan_cir++;      break;

                                    case "turnRight":
                                        //交通标志物代码可以放在此处
                                        break;

                                /*车牌识别结果处理
                                 *适合已知车牌图形库，经过训练结果较为准确场景
                                * */
                                    case "B880U0"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="B880U0"; }
                                        else{ PlateNum2="B880U0"; }
                                        break;
                                    case "G660D9"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="G660D9"; }
                                        else{ PlateNum2="G660D9"; }
                                        break;
                                    case "G696G6"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="G696G6"; }
                                        else{ PlateNum2="G696G6"; }
                                        break;
                                    case "I100U5"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="I100U5"; }
                                        else{ PlateNum2="I100U5"; }
                                        break;
                                    case "L101I3"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="L101I3"; }
                                        else{ PlateNum2="L101I3"; }
                                        break;
                                    case "P996Y6"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="P996Y6"; }
                                        else{ PlateNum2="P996Y6"; }
                                        break;
                                    case "Q564E3"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="Q564E3"; }
                                        else{ PlateNum2="Q564E3"; }
                                        break;
                                    case "Q687F3"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="Q687F3"; }
                                        else{ PlateNum2="Q687F3"; }
                                        break;
                                    case "T159B8"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="T159B8"; }
                                        else{ PlateNum2="T159B8"; }
                                        break;
                                    case "U010I1"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="U010I1"; }
                                        else{ PlateNum2="U010I1"; }
                                        break;
                                    case "Y568Y0"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="Y568Y0"; }
                                        else{ PlateNum2="Y568Y0"; }
                                        break;
                                    case "Z987X1"://识别车牌号1
                                        if (PlateNum1==null) { PlateNum1="Z987X1"; }
                                        else{ PlateNum2="Z987X1"; }
                                        break;
                                    default:
                                        break;

                            }
                        }
                        tri=black_tri+white_tri+red_tri+green_tri+blue_tri+yellow_tri+purple_tri+ cyan_tri;
                        rect=black_rect+white_rect+red_rect+green_rect+blue_rect+yellow_rect+purple_rect+cyan_rect;
                        square=black_square+white_square+red_square+green_square+blue_square+yellow_square+purple_square+cyan_square;
                        rhomb=black_rhomb+white_rhomb+red_rhomb+green_rhomb+blue_rhomb+yellow_rhomb+purple_rhomb+cyan_rhomb;
                        star=black_star+white_star+red_star+green_star+blue_star+yellow_star+purple_star+cyan_star;
                        circle=black_cir+white_cir+red_cir+green_cir+blue_cir+yellow_cir+purple_cir+cyan_cir;
                        picShapeNum= tri+rect+rhomb+star+circle;
                        if(picShapeNum<6){
                            shapeZero();
                        }
                        else{
                            //形状识别结果处理函数
                            blacknum =black_tri+black_rect+black_rhomb+black_star+black_cir+black_square;
                            whitenum=white_tri+ white_rect+white_rhomb+white_star+white_cir+white_square;
                            rednum=red_tri+ red_rect+red_rhomb+red_star+red_cir+red_square;
                            greennum=green_tri+ green_rect+green_rhomb+green_star+green_cir+green_square;
                            bluenum=blue_tri+ blue_rect+blue_rhomb+blue_star+blue_cir+blue_square;
                            yellownum=yellow_tri+yellow_rect+yellow_rhomb+yellow_star+yellow_cir+yellow_square;
                            purplenum=purple_tri+ purple_rect+purple_rhomb+purple_star+purple_cir+purple_square;
                            cyannum=cyan_tri+ cyan_rect+cyan_rhomb+cyan_star+cyan_cir+cyan_square;
                            carShape.triResultnum=tri;
                            carShape.rectResultnum=rect;
                            carShape.rhombResultnum=rhomb;
                            carShape.squareResultnum=square;
                            carShape.starResultnum=star;
                            carShape.circleResultnum=circle;
                            carShape.shapeResultNum=picShapeNum;
                        }
                        tftDown();//先让小车发送下翻图片
                        Log.d("yolov5", "三角形: "+tri+"矩形: "+rect+"菱形: "+rhomb+"五角星: "+star+"圆形: "+circle);
                        Log.d("yolov5", "车牌1为: "+PlateNum1+"车牌2为: "+PlateNum2);
                        task_handler.sendEmptyMessageDelayed(3, 2500);//重新进入case3
                    }//图片数据已读完
                    //识别的数据处理，如过有需要在这里处理的话
                    else{
                        imgReadNum = 0;
                        shapedata="F"+rednum+","+"F"+greennum+","+"F"+bluenum;
                        Log.d("yolov5", "shape识别结果为: "+shapedata);
                        Log.d("yolov5", "车牌1识别结果为: "+PlateNum1);
                        Log.d("yolov5", "车牌2识别结果为: "+PlateNum2);
                        shapedataarr=shapedata.split(",");
                        int[] num=new int[shapedataarr.length];
                        try {
                            for (int i=0;i<shapedataarr.length;i++)
                            {
                                num[i] = algorithm.OxStringtoInt(shapedataarr[i]);//将16进制字符串转为10进制的int
                            }
                            shapedata_sho=new short[num.length];
                            shapedata_sho=algorithm.shortint2hex(num);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        TYPE=0xAA;
                        MAJOR = 0x40;
                        FIRST =  shapedata_sho[0];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                        SECOND =  shapedata_sho[1];
                        THRID =  shapedata_sho[2];
                        send();
                        yanchi(100);
                        send();
                        //车牌处理的代码可以放在此处
                    }
                    break;


                case 4://任务三已拿到车牌结果
                    Log.d("auto","进入车牌处理任务");


                    break;

                case 30://红绿灯识别:基于相似度进行自动裁切，裁切准确度70%左右，受环境影响较大，弃用
//                    if (rgbReadNum<3) {//先获取三张图片
//                        switch (rgbReadNum)//获取tft中图片的范围，以便裁切，得到retx，rety，disx，disy四个值
//                        {
//                            case 0:
//                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//第一次获取图片
//                                Log.d("rgb", "获取第一张图片" );
//                                break;
//                            case 1:
//                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的需要对比的图片
//                                Log.d("rgb", "获取第二张图片" );
//                                break;
//                            case 2:
//                                image3 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
//                                Log.d("rgb", "获取第三张图片" );
//                                break;
//                        }
//                        rgbReadNum++;
//                        task_handler.sendEmptyMessageDelayed(1, 600);//重新进入case3
//                    }
//                    else {
//                        if (rgbReadNum < 6)//再循环获取3次，获取多张照片，避免遗漏灯，增加准确度
//                        {
//                            Mat src1 = new Mat();
//                            Mat src2 = new Mat();
//                            Mat src3 = new Mat();
//                            Utils.bitmapToMat(image1, src1);
//                            Utils.bitmapToMat(image2, src2);
//                            Utils.bitmapToMat(image3, src3);
//                            CarRgbLight = new CarRgbLight();
//                            CarRgbLight.imageSubtract3(src1, src2, src3);//做图片对比，找出不同的区域，并画出轮廓
//                            Log.d("rgb", "处理第" + rgbReadNum + "张图片");
//                            switch (rgbReadNum) {
//                                case 3:
//                                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src1 = new Mat();
//                                    Utils.bitmapToMat(image1, src1);
//                                    break;
//                                case 4:
//                                    image2 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src2 = new Mat();
//                                    Utils.bitmapToMat(image2, src2);
//                                    break;
//                                case 5:
//                                    image3 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src3 = new Mat();
//                                    Utils.bitmapToMat(image3, src3);
//                                    break;
//                                case 6:
//                                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src3 = new Mat();
//                                    Utils.bitmapToMat(image1, src1);
//                                    break;
//                                case 7:
//                                    image2 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src3 = new Mat();
//                                    Utils.bitmapToMat(image2, src2);
//                                    break;
////                                case 8:
////                                    image3 = left_Fragment.bitmap.copy(ARGB_8888, true);
////                                    src3 = new Mat();
////                                    Utils.bitmapToMat(image3, src3);
////                                    break;
//                                default:
//                                    break;
//                            }
//                            rgbReadNum++;
//                            task_handler.sendEmptyMessageDelayed(1, 600);//重新进入case3
//                        }
//                        else {
//                            //3秒左右后成功获取红绿灯坐标，进入任务
//                            rgbReadNum = 0;
//                            traffic_control(0x0E, 0x01, 0x00);//发送开始识别指令，比赛最好通过主车发送
//                            Log.d("rgb","已发送识别指令");
//                            yanchi(500);
//                            traffic_control(0x0E, 0x01, 0x00);//再发以便，避免主车未收到
//                            yanchi(500);
//                            bitmapRgb = left_Fragment.bitmap.copy(ARGB_8888, true);
//                            Log.d("rgb","最终坐标："+ "x:"+CarShape.ret_x+"y:"+ CarShape.ret_y+ "disx:"+dis_x+"disy:"+ dis_y);
//                            Bitmap res = CarRgbLight.cutRgbPic(bitmapRgb, CarShape.ret_x, CarShape.ret_y, CarShape.dis_x, CarShape.dis_y);//裁切结果
//                            RightAutoFragment.rec_image_show.setImageBitmap(res);//显示
//                            CarRgbLight.trfficLight(res);//根据相似度得到红绿灯结果
//                            rec_result=CarRgbLight.trffictResult;//将识别的结果放入res
//                            Log.d("rgb","识别结果为："+rec_result);
//                            switch (rec_result)
//                            {
//                                case "red":
//                                    traffic_control(0x0E ,0x02,0x01);
//
//                                    Log.d("rgb", "red");
//                                    break;
//                                case "green":
//                                    traffic_control(0x0E ,0x02,0x02);
//                                    Log.d("rgb", "green");
//                                    break;
//
//                                case "yellow":
//                                    traffic_control(0x0E ,0x02,0x03);
//                                    Log.d("rgb", "yellow");
//                                    break;
//
//                                default:
//                                    traffic_control(0x0E ,0x02,0x01);//其他情况都按红色发送识别
//                                    break;
//                            }
//                        }
//                    }
//
//                    break;


                case 31://基于翻页确定tft位置,可实现，tft定位准确度80%左右，弃用
//                    Log.d("shape","进入形状识别"+imgReadNum);
//                    if (shapeReadNum<4) {
//                        switch (shapeReadNum)//获取tft中图片的范围，以便裁切，得到retx，rety，disx，disy四个值
//                        {
//                            case 0:
//                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//第一次获取图片
//                                break;
//                            case 1:
//                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的需要对比的图片
//                                // CarShape.autoCut(image1, image2);//对比得出不同区域，得到四个重要的参数
//                                break;
//                            case 2:
//                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
//                                CarShape.autoCut(image1, image2);//再次对比，提高准确性
//                                break;
//                            case 3:
//                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
//                                CarShape.autoCut(image1, image2);//再次对比，提高准确性
//                                break;
//                        }
//                        shapeReadNum++;
//                        tftDown();//先让小车发送下翻图片
//                        task_handler.sendEmptyMessageDelayed(31, 2500);//重新进入case3
//                    }
//                    else {//根据四个重要参数，对图片逐一识别
//                        if(imgReadNum<7){
//                            imgReadNum++;
//                            carShape=new CarShape();
//                            // long current = System.currentTimeMillis();
//                            Bitmap bitmapShape = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
//                            Bitmap bitmap_opencv_cut = CarShape.opencvCutmap( bitmapShape,CarShape.ret_x,CarShape.ret_y,CarShape.dis_x,CarShape.dis_y);;//对前一张图片进行裁切
//                            RightAutoFragment.image_show.setImageBitmap(bitmap_opencv_cut);//显示裁切后的图片
//                            carShape.colorAndShape(bitmap_opencv_cut);//对裁切后的图片进行形状的识别
//                            //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
//                            RightAutoFragment.rec_image_show.setImageBitmap(CarShape.rebitmap_opencv);//显示轮廓图像
//                            if ( carShape.shapeResultNum>9)
//                            {
//                                picShapeNum=imgReadNum;
//                                rednum=carShape.red_num;
//                                greennum=carShape.green_num;
//                                bluenum=carShape.blue_num;
//                            }
//                            tftDown();//先让小车发送下翻图片
//                            task_handler.sendEmptyMessageDelayed(3, 2000);//重新进入case3
//                        }else{
//                            imgReadNum = 0;
//                            shapedata="F"+rednum+","+"F"+greennum+","+"F"+bluenum;
//                            shapedataarr=shapedata.split(",");
//                            int[] num=new int[shapedataarr.length];
//                            try {
//                                for (int i=0;i<shapedataarr.length;i++)
//                                {
//                                    num[i] = algorithm.OxStringtoInt(shapedataarr[i]);//将16进制字符串转为10进制的int
//                                }
//                                shapedata_sho=new short[num.length];
//                                shapedata_sho=algorithm.shortint2hex(num);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            TYPE=0xAA;
//                            MAJOR = 0x40;
//                            FIRST =  shapedata_sho[0];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
//                            SECOND =  shapedata_sho[1];
//                            THRID =  shapedata_sho[2];
//                            send();
//                            yanchi(100);
//                            send();
//                        }
//                    }
                    break;


                case 32://红绿灯识别:基于霍夫圆进行区域提取，结合相似度对红绿灯进行判断,裁切准确率80%左右，识别准确率95%，可选
                    int[] Point=null;
                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
                    bitmapRgb = left_Fragment.bitmap.copy(ARGB_8888, true);
                    CarRgbLight = new CarRgbLight();
                    Point=CarRgbLight.HoughCircles(image1);
                    int getCircle=5;
                    while(getCircle!=0){
                        if (CarRgbLight.HoughCircles_num<3){
                            Point=CarRgbLight.HoughCircles(image1);
                            getCircle--; //多次获取
                        }
                        else{
                            getCircle=0;
                        }
                    }
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//再发一遍，避免主车未收到
                    yanchi(1000);
                    Log.d("rgb","已发送识别指令");
                    Bitmap  bitmapRgbsrc= left_Fragment.bitmap.copy(ARGB_8888, true);
                    RightAutoFragment.image_show.setImageBitmap(bitmapRgbsrc);//显示
                    Log.d("rgb","最终坐标："+ "x:"+Point[0]+"y:"+ Point[1]+ "disx:"+Point[2]+"disy:"+ Point[3]);
                    CarRgbLight=new CarRgbLight();
                    Bitmap  bitmapRgbdst=CarRgbLight.cutRgbPic(bitmapRgbsrc,Point[0],Point[1],Point[2],Point[3]);
                    RightAutoFragment.rec_image_show.setImageBitmap( bitmapRgbdst);//显示
                    BitmapUtils.saveBitmap(bitmapRgbdst,"/DCIM/Car/",".png");//保存图片
                    CarRgbLight.trfficLight( bitmapRgbdst);//根据相似度得到红绿灯结果
                    rec_result=CarRgbLight.trffictResult;//将识别的结果放入res

                    Log.d("rgb","识别结果为："+rec_result);
                    switch (rec_result)
                    {
                        case "red":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            Log.d("rgb", "发送指令red");
                            break;
                        case "green":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            Log.d("rgb", "发送指令green");
                            break;

                        case "yellow":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            Log.d("rgb", "发送指令yellow");
                            break;

                        default:
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//其他情况都按红色发送识别
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//其他情况都按红色发送识别
                            break;
                    }
                    break;
                case 41://形状识别，基于yolovs找出截出tft区域，利用滤色寻找出形状数量,适合yolovs未训练情况下使用，准确度与录入的hsv模型关联
                    if(shapeReadNum<1)//第一次进入
                    {
                        Bitmap SelectedImage=null;
                        Log.d("shape","进入形状识别"+imgReadNum);
                        carShape=new CarShape();
                        // long current = System.currentTimeMillis();
                        if (left_Fragment.bitmap == null) {
                            SelectedImage= Yolov5Fragment.yourSelectedImage;
                        }
                        else{
                            SelectedImage = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
                        }

                        objects = yolov5ncnn.Detect(SelectedImage, false);
                        Log.d("yolov", "长度"+objects.length);
                        for(int i=0;i<objects.length;i++)
                        {
                            Log.d("yolov", "长度"+objects[i].label);
                            if("tft".equals(objects[i].label))
                            {
                                Log.d("yolov", "x"+objects[i].x);
                                retx=(int)objects[i].x;
                                rety=(int)objects[i].y;
                                w=(int)objects[i].w;
                                h=(int)objects[i].h;
                            }
                            Log.d("yolov", "x:"+retx+"y:"+rety+"w:"+w+"h:"+h);
                        }
                        Log.d("yolov", "x:"+retx+"y:"+rety+"w:"+w+"h:"+h+"长度"+objects.length);
                        Bitmap bitmap_cut = CarShape.opencvCutmap(SelectedImage,retx,rety,w,h);;//对前一张图片进行裁切
                        RightAutoFragment.image_show.setImageBitmap(bitmap_cut);//显示裁切后的图片
                        carShape.colorAndShape(bitmap_cut);//对裁切后的图片进行形状的识别
                        //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
                        RightAutoFragment.rec_image_show.setImageBitmap(CarShape.rebitmap_opencv);//显示轮廓图像
                        shapeReadNum++;
                        tftDown();//先让小车发送下翻图片
                        task_handler.sendEmptyMessageDelayed(3, 2500);//重新进入case3
                    }
                    else{
                        if(shapeReadNum<7){
                            Bitmap SelectedImage=null;
                            carShape=new CarShape();
                            // long current = System.currentTimeMillis();
                            if (left_Fragment.bitmap == null) {
                                SelectedImage= Yolov5Fragment.yourSelectedImage;
                            }
                            else{
                                SelectedImage = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
                            }
                            Yolov5Fragment.iv.setImageBitmap(SelectedImage);//显示轮廓图像
                            Bitmap bitmap_cut = CarShape.opencvCutmap(SelectedImage,retx,rety,w,h);;//对前一张图片进行裁切
                            carShape.colorAndShape(bitmap_cut);//对裁切后的图片进行形状的识别
                            if ( carShape.shapeResultNum>9)//判断识别的形状是否大于9个
                            {
                                picShapeNum=imgReadNum;
                                rednum=carShape.red_num;
                                greennum=carShape.green_num;
                                bluenum=carShape.blue_num;
                            }
                            //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
                            Yolov5Fragment.iv2.setImageBitmap(CarShape.rebitmap_opencv);//显示轮廓图像
                            shapeReadNum++;
                            tftDown();//先让小车发送下翻图片
                            task_handler.sendEmptyMessageDelayed(3, 2500);//重新进入case3
                        }
                        else{//识别的数据处理
                            imgReadNum = 0;
                            shapedata="F"+rednum+","+"F"+greennum+","+"F"+bluenum;
                            shapedataarr=shapedata.split(",");
                            int[] num=new int[shapedataarr.length];
                            try {
                                for (int i=0;i<shapedataarr.length;i++)
                                {
                                    num[i] = algorithm.OxStringtoInt(shapedataarr[i]);//将16进制字符串转为10进制的int
                                }
                                shapedata_sho=new short[num.length];
                                shapedata_sho=algorithm.shortint2hex(num);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            TYPE=0xAA;
                            MAJOR = 0x40;
                            FIRST =  shapedata_sho[0];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                            SECOND =  shapedata_sho[1];
                            THRID =  shapedata_sho[2];
                            send();
                            yanchi(100);
                            send();
                        }
                    }

                    break;

                case 51://二维码识别
                   if (QRReadNum<5){
                        RightAutoFragment.QrFlag=true;
                        RightAutoFragment.QRRecon();
                        qr_result= RightAutoFragment.result_qr;
                        QRReadNum++;
                        if (qr_result!=null){
                            QRReadNum=6;
                        }
                        task_handler.sendEmptyMessageDelayed(2, 500);//重新进入case2
                    }
                    else{
                        RightAutoFragment.QrFlag=false;
                        if (qr_result!=null) {//二维码识别的结果为字符串
                            Log.d("qr", "二维码识别结果1"+qr_result);
                            qr_resultArr=algorithm.S2Arr(qr_result);//需要对字符串进行处理，得到字符串数组
                            Log.d("qr", "qr字符串数组"+qr_resultArr);
                            //此处，在比赛时候需要根据要求修改S2Arr里面的处理方法
                            qr_resultSho=algorithm.Arr2Sho(qr_resultArr);//字符串数组转为short数组
                            //字符串数组转化为short数组
                            Log.d("qr", "qr_resultSho"+qr_resultSho);

                            TYPE=0xAA;
                            MAJOR = 0x10;//根据题目灵活更改
                            FIRST = qr_resultSho[0];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                            SECOND = qr_resultSho[1];
                            THRID = qr_resultSho[2];
                            send();
                            Log.d("qr", "前三位已发送");
                            yanchi(500);
                            MAJOR = 0x11;//根据题目灵活更改
                            FIRST = qr_resultSho[3];//二维码读取到的数据，字符串转为了BYTE，此处将第一位发给小车
                            SECOND =qr_resultSho[4];
                            THRID = qr_resultSho[5];
                            send();
                            Log.d("qr", "后三位已发送");
                        }
                    }

                    //Sw_algorithm(2,result_qr);					// 二维码算法选择
                    //qr_result="{0x03,0x05,0x14,0x45,0xDE,0x92}";

                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };



    private Resources getResources() {
        // TODO Auto-generated method stub
        Resources mResources = null;
        mResources = getResources();
        return mResources;
    }

//处理结束



    // 循迹
    public void line(int sp_n) {  //寻迹
        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //清除码盘值
    public void clear() {
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void stateChange(final int i) {//主从车状态转换
        final short temp = TYPE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {//从车状态
                    TYPE = 0x02;
                    MAJOR = 0x80;
                    FIRST = 0x01;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    yanchi(500);

                    TYPE = (byte) 0xAA;
                    MAJOR = 0x80;
                    FIRST = 0x01;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    TYPE = 0x02;
                } else if (i == 2) {// 主车状态
                    TYPE = 0x02;
                    MAJOR = 0x80;
                    FIRST = 0x00;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    yanchi(500);

                    TYPE = (byte) 0xAA;
                    MAJOR = 0x80;
                    FIRST = 0x00;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    TYPE = 0xAA;
                }
                TYPE = temp;
            }
        }).start();
    }

    // 红外
    public void infrared(final byte one, final byte two, final byte thrid, final byte four, final byte five,
                         final byte six) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MAJOR = 0x10;
                FIRST = one;
                SECOND = two;
                THRID = thrid;
                send();
                yanchi(500);
                MAJOR = 0x11;
                FIRST = four;
                SECOND = five;
                THRID = six;
                send();
                yanchi(500);
                MAJOR = 0x12;
                FIRST = 0x00;
                SECOND = 0x00;
                THRID = 0x00;
                send();
                yanchi(1000);
            }
        }).start();
    }

    //TFT上翻页
    public void tftUp() {
        TYPE = (byte) 0xAA;
        MAJOR = 0x50;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }


    //TFT下翻页
    public void tftDown() {
        TYPE = (byte) 0xAA;
        MAJOR = 0x51;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 双色led灯
    public void lamp(byte command) {
        MAJOR = 0x40;
        FIRST = command;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 指示灯
    public void light(int left, int right) {
        if (left == 1 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 1 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
    }


    // 蜂鸣器
    public void buzzer(int i) {
        if (i == 1)
            FIRST = 0x01;
        else if (i == 0)
            FIRST = 0x00;
        MAJOR = 0x30;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    /**
     * 从车二维码识别
     */
    public void qr_rec(int state) {
        FIRST = 0x92;
        MAJOR = (byte) state;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void gear(int i) {// 加光照档位
        if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //立体显示
    public void infrared_stereo(final short[] data) {
        MAJOR = 0x10;
        FIRST = 0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(700);
    }

    //立体显示
    public void infrared_dis(final short[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MAJOR = 0x10;
                FIRST = 0xff;
                SECOND = data[0];
                THRID = data[1];
                send();
                yanchi(500);
                MAJOR = 0x11;
                FIRST = data[2];
                SECOND = data[3];
                THRID = data[4];
                send();
                yanchi(500);
                MAJOR = 0x12;
                FIRST = 0x00;
                SECOND = 0x00;
                THRID = 0x00;
                send();
                yanchi(500);
            }
        }).start();
    }


    //智能交通灯
    public void traffic_control(int type, int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    /**
     * 舵机角度控制
     *
     * @param major 左侧舵机
     * @param first 右侧舵机
     */
    public void rudder_control(int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x0C;
        MAJOR = (byte) 0x08;
        FIRST = (byte) major;
        SECOND = (byte) first;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    //立体车库控制
    public void garage_control(int type, int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void gate(int major, int first, int second, int third) {// 闸门
        byte temp = (byte) TYPE;
        TYPE = 0x03;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) second;
        THRID = (byte) third;
        send();
        TYPE = temp;
    }

    //LCD 显示标志物进入计时模式
    public void digital_close() {//数码管关闭
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_open() {//数码管打开
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_clear() {//数码管清零
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_dic(int dis) {//LCD显示标志物第二排显示距离

        byte temp = (byte) TYPE;
        int a = 0, b = 0, c = 0;
        a = (dis / 100) & (0xF);
        b = (dis % 100 / 10) & (0xF);
        c = (dis % 10) & (0xF);
        b = b << 4;
        b = b | c;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (short) (a);
        THRID = (short) (b);
        send();
        TYPE = temp;
    }

    public void digital(int i, int one, int two, int three) {// 数码管
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {//数据写入第一排数码管
            MAJOR = 0x01;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        } else if (i == 2) {//数据写入第二排数码管
            MAJOR = 0x02;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        }
        send();
        TYPE = temp;
    }

    public void VoiceBroadcast()  //语音播报随机指令
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x06;
        MAJOR = (short) 0x20;
        FIRST = (byte) 0x01;
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
        TYPE = temp;
    }

    public void TFT_LCD(int type, int MAIN, int KIND, int COMMAD, int DEPUTY)  //tft lcd
    {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY) //磁悬浮
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    // 沉睡
    public void yanchi(int time) {

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }

}
