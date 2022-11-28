package car.bkrc.com.car2021.FragmentView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.whitelife.library.Util;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.ActivityView.LoginActivity;
import car.bkrc.com.car2021.DataProcessingModule.QR_Recognition;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.BitmapUtils;
import car.bkrc.com.car2021.Utils.CarPlate.PlateDetector;
import car.bkrc.com.car2021.Utils.CarShape.CarShape;
import car.bkrc.com.car2021.Utils.TrafficRecUtil.ColorRecognition;
import car.bkrc.com.car2021.DataProcessingModule.ConnectTransport;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.Connect_Transport;


public class RightAutoFragment extends Fragment  {

    /*这部分实现与滑动界面绑定*/
    public static final String TAG = "RightAutoFragment";
    private View view = null;
    public static  ImageView image_show = null;
    public static ImageView rec_image_show = null;
    //    private byte[] img2;
    private Button car_trffic,car_color,car_shape;
    private Button savePicBtn,cameraSet1,cameraSet2,cameraSet3;

    private Bitmap green00,green01,green02,green03,green04,red00,red01,red02,red03,red04,yellow00,yellow01,yellow02,yellow03,yellow04;
    public static Mat matgreen0,matgreen1,matgreen2,matgreen3,matgreen4,matred0,matred1,matred2,matred3,matred4,matyellow0,matyellow1,matyellow2,matyellow3,matyellow4;
    public void rgblight(){
      green00 = BitmapFactory.decodeResource(getResources(), R.drawable.green);
      green01 = BitmapFactory.decodeResource(getResources(), R.drawable.green01);
      green02 = BitmapFactory.decodeResource(getResources(), R.drawable.green02);
      green03 = BitmapFactory.decodeResource(getResources(), R.drawable.green03);
        // green04 = BitmapFactory.decodeResource(getResources(), R.drawable.green04);
        red00 = BitmapFactory.decodeResource(getResources(),R.drawable.red);
       red01 = BitmapFactory.decodeResource(getResources(),R.drawable.red01);
       red02 = BitmapFactory.decodeResource(getResources(), R.drawable.red02);
       red03 = BitmapFactory.decodeResource(getResources(), R.drawable.red03);
       red04 = BitmapFactory.decodeResource(getResources(), R.drawable.red04);
        yellow00 = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
       yellow01 = BitmapFactory.decodeResource(getResources(), R.drawable.yellow01);
        yellow02 = BitmapFactory.decodeResource(getResources(),R.drawable.yellow02);
      yellow03 = BitmapFactory.decodeResource(getResources(), R.drawable.yellow03);
        yellow04 = BitmapFactory.decodeResource(getResources(), R.drawable.yellow04);;

        matgreen0=new Mat();
        matgreen1=new Mat();
        matgreen2=new Mat();
        matgreen3=new Mat();
        matgreen4=new Mat();
        matred0=new Mat();
        matred1=new Mat();
        matred2=new Mat();
        matred3=new Mat();
        matred4=new Mat();
        matyellow0=new Mat();
        matyellow1=new Mat();
        matyellow2=new Mat();
        matyellow3=new Mat();
        matyellow4=new Mat();
        Utils.bitmapToMat(green00, matgreen0); Utils.bitmapToMat(green01, matgreen1);  Utils.bitmapToMat(green02, matgreen2);
        Utils.bitmapToMat(green03, matgreen1); //Utils.bitmapToMat(green01, matgreen1);
        Utils.bitmapToMat(red00, matred0); Utils.bitmapToMat(red01, matred1); Utils.bitmapToMat(red02, matred2);
        Utils.bitmapToMat(red03, matred3); Utils.bitmapToMat(red04, matred4);
        Utils.bitmapToMat(yellow00, matyellow0);Utils.bitmapToMat(yellow01, matyellow1); Utils.bitmapToMat(yellow02, matyellow2);
        Utils.bitmapToMat(yellow03, matyellow3);Utils.bitmapToMat(yellow04, matyellow4);
    }


    private int read_img_int=0;
    private int car_color_int=0;
    private int car_shape_int=0;

    private EditText progress_edit = null;
    private EditText brightness_edit = null;
    private EditText blur_edit = null;
    public TextView show_news;
    public TextView rec_result;

    private static Bitmap bitmap;
    private static Bitmap rebitmap;//保存一份需要处理的位图

    private static int[] red_1={0,43,46,10,255,255};
    private static int[] red_2={156,43,46,180,255,255};
    private static int[] yellow_1={26,43,46,34,255,255};//黄色
    private static int[] yellow_2={11,43,46,25,255,255};//橙色
    private static int[] green={35,43,46,77,255,255};

    //hsv标准颜色
    public static int[][] HSV_VALUE_standard = {
            {0,0,0,180,255,46},//黑
            {0,0,46,180,43,220},//灰
            {0,0,221,180,30,255},//白
            {0,43,46,10,255,255},//红1
            {156,43,46,180,255,255},//红2
            {11,43,46,25,255,255},//橙色
            {26,43,46,34,255,255},//黄色
            {35,43,46,77,255,255},//绿色
            {78,43,46,99,255,255},//青色
            {100,43,46,124,255,255},//蓝色
            {125,43,46,155,255,255}//紫色
    };


    public static boolean TrfficFlag = false;//通过像素识别红绿灯
    public static boolean RgbOpencvFlag = false;//通过Opencv识别红绿灯
    public static boolean QrFlag = false;//二维码识别开关
    public static boolean ColorFlag = false;//颜色识别开关
    public static boolean ShapeFlag = false;//形状识别开关
    public static int shapeNum=0;


    public static RightAutoFragment getInstance() {
        return RightAutoFragment.RightAutoFragmentHolder.sInstance;
    }



    private static class RightAutoFragmentHolder {
        private static final RightAutoFragment sInstance = new RightAutoFragment();
    }
    /*这部分实现与滑动界面绑定*/

    public static LeftFragment left_Fragment;//加载类
    public static ColorRecognition Color_Recognition;//加载类
    public static ConnectTransport ConnectTransport;//加载类
    public static PlateDetector PlateDetector;//加载类


    /**
     * 页面加载，绑定滑动界面
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        /*这部分实现与滑动界面绑定*/
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            //判断是否为平板，本处保留，但展现的是同一个界面
            if (LoginActivity.isPad(getActivity()))
                view = inflater.inflate(R.layout.right_auto_fragment, container, false);
            else
                view = inflater.inflate(R.layout.right_auto_fragment, container, false);
        }
        initView(view);
        task();
        iniLoadOpenCV();
        bitmap = LeftFragment.bitmap2;//获取摄像头位图文件（跨文件获取）
        rgblight();
        return view;

        /*这部分实现与滑动界面绑定*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /***按键事件***/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        car_trffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if( read_img_int>1) {
//                    read_img_int = 0;
//                }
//
//                if ( read_img_int==0) {
//                    cleanflag();
//                    read_img_int++;}
//                else if( read_img_int==1) {
//                    cleanflag();
//                    RgbOpencvFlag = true;
//                    read_img_int++;
//                }
                ConnectTransport.tftDown();
            }
        });

        car_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if( car_color_int>1) {
//                    car_color_int = 0;
//                }
//                if (car_color_int==0) {
//                    cleanflag();
//                    car_color_int++;}
//                else if(car_color_int==1) {
//                    cleanflag();
//                    ShapeFlag = true;
//                    car_color_int++;
//                }


//                CarShape.ret_x=0;
//                CarShape.ret_y=0;
//                CarShape.dis_x=0;
//                CarShape.dis_y=0;
//                int shapeNum=0;
//                Message message = new Message();
//                message.what = 20;
//                CSHandler.sendMessage(message);
                CarPlate();
            }
        });

        car_shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if( read_img_int>1) {
//                    read_img_int = 0;
//                }
//                if (car_shape_int==0) {
//                        cleanflag();
//                    car_shape_int++;}
//                else if(car_shape_int==1) {
//                        cleanflag();
//                        QrFlag = true;
//                    car_shape_int++;
//                    }
//
//                //保存图片
//                Bitmap bitmap_save=left_Fragment.bitmap;
//                //bitmap_save = Bitmap.createBitmap(bitmap_save.getWidth(), bitmap_save.getHeight(), Bitmap.Config.ARGB_8888);
//                BitmapUtils bitmapUtils=new BitmapUtils();
//                bitmapUtils.saveBitmap(bitmap_save,"/car/",".png");
                CarShape carShape=new CarShape();
                // long current = System.currentTimeMillis();
                Bitmap bitmapShape = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
                Bitmap bitmap_opencv_cut = CarShape.opencvCutmap( bitmapShape,CarShape.ret_x,CarShape.ret_y,CarShape.dis_x,CarShape.dis_y);;//对前一张图片进行裁切
                image_show.setImageBitmap(bitmap_opencv_cut);//显示裁切后的图片
                carShape.colorAndShape(bitmap_opencv_cut);//对裁切后的图片进行形状的识别
                //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
                rec_image_show.setImageBitmap(CarShape.rebitmap_opencv);//显示轮廓图像
            }
        });
        savePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmapSave = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
                BitmapUtils.saveBitmap(bitmapSave,"/DCIM/Car/",".png");
            }
        }
        );

        cameraSet1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Message message = new Message();
                  message.what = 5;
                  Connect_Transport.cameraSet.sendMessage(message);
              }
           }
        );
        cameraSet2.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Message message = new Message();
                                              message.what = 8;
                                              Connect_Transport.cameraSet.sendMessage(message);
                                          }
                                      }
        );

        cameraSet3.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Message message = new Message();
                                              message.what = 10;
                                              Connect_Transport.cameraSet.sendMessage(message);
                                          }
                                      }
        );
    }
    /***以上为按键事件***/



        private void iniLoadOpenCV(){
        boolean success= OpenCVLoader.initDebug();
        if (success){
            Log.e("cmd", "成功加载opencv");
        }else {
            Log.e("cmd", "加载opencv失败");
        }
    }

    public void task(){//放在主线程里运行
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                if( RgbOpencvFlag == true)
                {
                    Log.e("cmd", "判断进入Rgbopencv");
                 //   RgbLightOpencv();
                    bitmap_opencv=left_Fragment.bitmap;
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);//红绿灯识别，基于opencv

                }

                if( QrFlag == true)
                {
                    qrBitmap = LeftFragment.bitmap;
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);//二维码识别
                }
                if( TrfficFlag == true)
                {
                    TrfficFlag=false;
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);//红绿灯识别，基于像素点
                }
                if( ShapeFlag == true)
                {
                    ShapeFlag=false;
                    bitmap_shape=left_Fragment.bitmap;
                    Log.d("auto","进入形状识别");
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);;//形状及颜色识别
                }

                // cancel();//取消定时器
            }
        },0,100);//定时器 100ms执行一次
    }

    public void CarPlate()
    {
        if (bitmap_carPlate==null)
        {
            bitmap_carPlate= BitmapFactory.decodeResource(getResources(), R.drawable.plate);
        }
        PlateDetector = new PlateDetector();
        PlateDetector.getPlate(bitmap_carPlate);
        show_news.setText(planteNum);
        image_show.setImageBitmap(PlateDetector.bitmapPlate);
        rec_image_show.setImageBitmap(PlateDetector.rebitmapPlate);
       // PlateDetector.plateDetector(bitmap_carPlate);
//        PlateDetector PlateDetector=new PlateDetector();



    }


    private static int red_num,green_num,yellow_num;
    public static String rgb_result = "red";
    public void RgbLightOpencv()
    {
        int i=15;    int j=15;  int aremin=20; int aremax=1000; double esp=0.035; //运算核默认15*15，可修改
        Log.e("cmd", "成功进入opencv函数");
            //bitmap_opencv=left_Fragment.bitmap;
            //if (bitmap_opencv==null)
           // {
               bitmap_opencv= BitmapFactory.decodeResource(getResources(), R.drawable.rgb);
            //}
        Log.e("cmd", "获取图片成功");
            opencvCutmap(bitmap_opencv,0.5f,0.05f,0.50f,0.25f);//裁切
            rec_image_show.setImageBitmap(rebitmap_opencv);
            bitmap_opencv_cut=rebitmap_opencv;//裁切之后的图片
            matcolor(bitmap_opencv_cut,2,red_1,red_2);//滤色，红色
            kernel(hsvmat,i,j);   //确定运算核，类似于卷积核
            shape(aremin,aremax,esp);
            red_num=circle;

            Log.e("cmd","red_num"+red_num);
            circle= contoursCounts=tri= rect=star=0;

            matcolor(bitmap_opencv_cut,2,yellow_1,yellow_2);//滤色，黄色
            kernel(hsvmat,i,j);   //确定运算核，类似于卷积核
            shape(aremin,aremax,esp);
            yellow_num=circle;
            Log.e("cmd","yellow_num"+yellow_num);
            circle= contoursCounts=tri= rect=star=0;

            matcolor(bitmap_opencv_cut,1,green,null);//滤色，绿色
            kernel(hsvmat,i,j);   //确定运算核，类似于卷积核
            shape(aremin,aremax,esp);
            green_num=circle;
            Log.e("cmd","green_num"+green_num);
            circle= contoursCounts=tri= rect=star=0;

            if((green_num!=0) )
            {  rgb_result="green"; }
            else if((red_num!=0))
            {  rgb_result="red"; }
            else if((yellow_num!=0))
            {  rgb_result="yellow"; }

            rec_result.setText("轮廓："+contoursCounts+"圆形："+circle+"三角形："+tri+"\n矩形："+rect+"五角星："+star
                    +"绿色："+green_num+"黄色："+yellow_num+"红色："+red_num+"识别结果："+rgb_result);

    }


    private int colornum=0;
    public static int  r_num,g_num,blue_num,black_num,y_num,s_num,c_num,white_num;
    public static int  black_tri, black_rect,black_rhomb,black_star,black_cir;
    public static int  white_tri, white_rect,white_rhomb,white_star,white_cir;
    public static int  red_tri, red_rect,red_rhomb,red_star,red_cir;
    public static int  green_tri, green_rect,green_rhomb,green_star,green_cir;
    public static int  blue_tri, blue_rect,blue_rhomb,blue_star,blue_cir;
    public static int  yellow_tri, yellow_rect,yellow_rhomb,yellow_star,yellow_cir;
    public static int  purple_tri, purple_rect,purple_rhomb,purple_star,purple_cir;


    public static int[][] HSV_Color_cloudy = {//阴天荧光灯下
            {0,0,0,180,255,95},//黑色0
            {0,0,220,180,42,255},//白色1
            {156,43,46,180,255,255},//正红色2
            {35,43,46,77,255,255},//绿色3
            {100,208,200,126,255,255},//蓝色4，变化较大
            {26,48,46,34,255,255},//黄色5
            {140,43,46,155,255,255},//紫色6
    };
//    public static int[][] HSV_VALUE_standard = {//标准色
//            {0,0,0,180,255,46},//黑
//            {0,0,46,180,43,220},//灰
//            {0,0,221,180,30,255},//白
//            {0,43,46,10,255,255},//红1
//            {156,43,46,180,255,255},//红2
//            {11,43,46,25,255,255},//橙色
//            {26,43,46,34,255,255},//黄色
//            {35,43,46,77,255,255},//绿色
//            {78,43,46,99,255,255},//青色
//            {100,43,46,124,255,255},//蓝色
//            {125,43,46,155,255,255}//紫色
//    };

    private void colorAndShape(){

        int i=3;    int j=3;  int aremin=380;  int aremax=3000; double esp=0.030;//运算核默认15*15，可修改
        Log.d("shape", "成功进入colorAndShape函数");
        Log.d("auto","进入colorandshape函数");
        bitmap_shape=left_Fragment.bitmap2;
        if ( bitmap_shape==null)
         {
             bitmap_shape= BitmapFactory.decodeResource(getResources(), R.drawable.shape_car2);
        }
        Log.d("shape", "获取图片成功");
        opencvCutmap(bitmap_shape,0.42f,0.12f,0.34f,0.36f);//裁切
        rec_image_show.setImageBitmap(rebitmap_opencv);
        bitmap_opencv_cut=rebitmap_opencv;//裁切之后的图片
         for(colornum=0; colornum<7; colornum++) {
            // circle= contoursCounts=tri= rect=star=0;
             matcolor(bitmap_opencv_cut, 1,HSV_Color_cloudy[ colornum], null);//
             kernel(hsvmat, i, j);   //确定运算核，类似于卷积核
             shape(aremin, aremax,esp);
             switch ( colornum)
             {
                 case 0:
                     Log.d("shape","黑色0:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     black_tri=tri; black_rect=rect;black_rhomb=rhomb;black_star=star;black_cir=circle;
                     break;
                 case 1:
                     Log.d("shape","白色1:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     white_tri=tri; white_rect=rect;white_rhomb=rhomb;white_star=star;white_cir=circle;

                     break;
                 case 2:
                     Log.d("shape","红色2:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     red_tri=tri; red_rect=rect;red_rhomb=rhomb;red_star=star;red_cir=circle;

                     break;
                 case 3:
                     Log.d("shape","绿色3:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     green_tri=tri; green_rect=rect;green_rhomb=rhomb;green_star=star;green_cir=circle;

                     break;
                 case 4:
                     Log.d("shape","蓝色4:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     blue_tri=tri; blue_rect=rect;blue_rhomb=rhomb;blue_star=star;blue_cir=circle;

                     break;
                 case 5:
                     Log.d("shape","黄色5:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     yellow_tri=tri; yellow_rect=rect;yellow_rhomb=rhomb;yellow_star=star;yellow_cir=circle;

                     break;
                 case 6:
                     Log.d("shape","紫色6:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                             " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                     purple_tri=tri; purple_rect=rect;purple_rhomb=rhomb;purple_star=star;purple_cir=circle;

                     break;
                 case 7:
                     break;
                 case 8:
                     break;
                 case 9:
                     break;
                 case 10:

             }
             //结果统计及处理
             tri=black_tri+white_tri+red_tri+green_tri+blue_tri+yellow_tri+purple_tri;
             rect=black_rect+white_rect+red_rect+green_rect+blue_rect+yellow_rect+purple_rect;
             rhomb=black_rhomb+white_rhomb+red_rhomb+green_rhomb+blue_rhomb+yellow_rhomb+purple_rhomb;
             star=black_star+white_star+red_star+green_star+blue_star+yellow_star+purple_star;
             circle=black_cir+white_cir+red_cir+green_cir+blue_cir+yellow_cir+purple_cir;
             contoursCounts=tri+rect+rhomb+star+circle;
             shapeNum=contoursCounts;
             //结果显示
             rec_result.setText("轮廓："+contoursCounts+"个。三角形："+tri+"个。 矩形："+rect+"个。 菱形："+rhomb+"个。 五角星："+star+"个。圆形:"+circle+"个。");
         }

    }


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case 1:
                    RgbLightOpencv();//红绿灯识别，opencv
                    break;

                case 2:
                    QRRecon(); //二维码识别
                    Log.e("cmd","进入二维码识别");
                    show_news.setText(result_qr);//结果显示
                    rec_result.setText(result_qr);//结果显示
                    break;
                case 3:
                    traffic_light();//通过像素检测红绿灯，二选一
                    result = Color_Recognition.onSuccess;//存储交通灯识别结果
                    result2 = ConnectTransport.rec_result+","+ConnectTransport.qr_result+sharp_result;//存储二维码识别结果
                    show_news.setText(result+","+result_qr+sharp_result);//结果显示
                    rec_result.setText(result2);//结果显示
                    break;

                case 4://形状及颜色识别
                    bitmap_shape = LeftFragment.bitmap;
                    Log.d("auto","进入handle4  shape");
                    colorAndShape();

                    break;

                case 5://车牌识别
                    CarPlate();//
                    result2 = ConnectTransport.rec_result+","+ConnectTransport.qr_result+sharp_result;//存储二维码识别结果
                    show_news.setText(result+","+result_qr+sharp_result);//结果显示
                    rec_result.setText(result2);//结果显示
                    break;
            }
            super.handleMessage(msg);
        }
    };



    public Handler flagHandlaer = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    cleanflag();

                case 1:
                    RgbOpencvFlag=true;
                    Log.e("cmd", "RgbOpencvFlag值为"+RgbOpencvFlag);
                case 2:
                    QrFlag=true;
            }
        }
    };

    public void cleanflag(){
        TrfficFlag = false;
        RgbOpencvFlag = false;
        QrFlag = false;
        ColorFlag = false;
        ShapeFlag = false;
    }

    public String result;
    public String result2;
    public void traffic_light() {   //通过像素处理红绿灯
        Message message = new Message();
        if( TrfficFlag == true)
        {
            for (int i = 0; i < 2; i++) {
                getimg();//跨页面获取位图，然后显示
                Color_Recognition.PictureProcessing(getActivity(), bitmap, getBrightness(), getProgress(), getblur());//几个关键数据可写死
                //处理后的图像显示
                rebitmap = Color_Recognition.callBitmap;//处理后的图片
                rec_image_show.setImageBitmap(rebitmap);//显示处理后图片的结果
                //显示结果
            }
            result = Color_Recognition.onSuccess;//存储交通灯识别结果
            result2 = ConnectTransport.rec_result+","+ConnectTransport.qr_result+sharp_result;//存储二维码识别结果
            show_news.setText(result+","+result_qr+sharp_result);//结果显示
            rec_result.setText(result2);//结果显示

        }
    }

    /**
     * 页面控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        image_show = view.findViewById(R.id.image_show);
        rec_image_show = view.findViewById(R.id.rec_image_show);
        image_show.setImageBitmap(bitmap);
        progress_edit = view.findViewById(R.id.progress_data);
        brightness_edit = view.findViewById(R.id.brightness_data);
        blur_edit = view.findViewById(R.id.blur_data);
        show_news = view.findViewById(R.id.show_news);
        rec_result=view.findViewById(R.id.recdata);
        car_trffic= view.findViewById(R.id.trfficBtn);
        car_color= view.findViewById(R.id.car_color);
        car_shape= view.findViewById(R.id.car_shape);
        savePicBtn=  view.findViewById(R.id.savePicBtn);
        cameraSet1=view.findViewById(R.id.dy1Btn);
        cameraSet2=view.findViewById(R.id.dy2Btn);
        cameraSet3=view.findViewById(R.id.dy3Btn);
        //savePicBtn= getActivity().findViewById(R.id.savePicBtn);


        left_Fragment = new LeftFragment();//实例化
        Color_Recognition = new ColorRecognition();
        ConnectTransport = new ConnectTransport();
    }



    private void getimg() {
        bitmap = LeftFragment.bitmap;//跨页面获取位图
        image_show.setImageBitmap(bitmap);//图像显示
    }

    /**
     * 获取页面的亮度、饱和度、模糊数据
     * getProgress() 获取图像饱和度
     * getBrightness() 获取图像亮度
     * getblur()  获取图像模糊范围
     */
    private int getProgress() {
        String src = progress_edit.getText().toString();
        int progress = 10;
        if (!src.equals("")) {
            progress = Integer.parseInt(src);
        } else {
//            toastUtil.ShowToast("请输入图像饱和度，默认为 +10");
        }
        return progress;
    }

    private int getBrightness() {
        String src = brightness_edit.getText().toString();
        int brightness = 80;
        if (!src.equals("")) {
            brightness = Integer.parseInt(src);
        } else {
//            toastUtil.ShowToast("请输入图像亮度，默认为 +80");
        }
        return brightness;
    }

    private int getblur() {
        String src = blur_edit.getText().toString();
        int brightness = 5;
        if (!src.equals("")) {
            brightness = Integer.parseInt(src);
        } else {
//            toastUtil.ShowToast("请输入图像模糊范围，默认为 5");
        }
        return brightness;
    }




    public static String result_qr;
    private Bitmap qrBitmap;
    private Bitmap bitmap_carPlate;
    public static String planteNum;
    private boolean qrRecState = false;
    // 二维码、车牌处理
//    @SuppressLint("HandlerLeak")
//    Handler qrHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 10:
//
////                    else toastUtil.ShowToast("没有连接到摄像头，请连接到摄像头后再试！");
//                    break;
//                case 20:
////                    toastUtil.ShowToast(result_qr);
//                    break;
//                case 30:
////                    toastUtil.ShowToast("未检测到二维码！");
//                    break;
//                case 40:
////                    autoDriveAction();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    public void QRRecon(){
        if (QrFlag==true)  {
            qrBitmap = LeftFragment.bitmap;
//            if (qrBitmap==null)
//            {
//                qrBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.erweima);
//            }
            image_show.setImageBitmap(qrBitmap);
            if (qrBitmap != null){
                new Thread(() -> {
                    Result result;
                    QR_Recognition rSource = new QR_Recognition(
                            bitmap2Gray(qrBitmap));
                    try {
                        BinaryBitmap binaryBitmap = new BinaryBitmap(
                                new HybridBinarizer(rSource));
                        Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                        hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                        QRCodeReader reader = new QRCodeReader();
                        result = reader.decode(binaryBitmap, hint);
                        if (result.toString() != null) {
                            result_qr = result.toString();
//                            qrHandler.sendEmptyMessage(20);
                        }
                        System.out.println("正在识别");
                    } catch (NotFoundException e) {
                        e.printStackTrace();
//                        qrHandler.sendEmptyMessage(30);
                    } catch (ChecksumException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    }
                }).start();
        }
        }
    }

    /**
     * 图像灰度化
     * @param bmSrc
     * @return
     */
    public Bitmap bitmap2Gray(Bitmap bmSrc) {
        // 得到图片的长和宽
        if (bmSrc == null)
            return null;
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        // 创建目标灰度图像
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // 创建画布
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        return bmpGray;
    }


    /**
     * 颜色识别
     */

    private void CarColor() {
        Bitmap bitmap_color = LeftFragment.bitmap;//跨页面获取位图
        if (bitmap_color != null) {
            convertToBlack_Col(bitmap_color);// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
        }
    }

    private int[] colorNum = new int[8];//红、绿、蓝、黄、品、青、黑色个数
    private int blackMax = 255; //黑色最大RGB值和
    private int RGBMax = 365;   //红绿蓝最大RGB值和
    private int noiseMax = 510; //黄品青最大RGB值和

    private Bitmap convertToBlack_Col(Bitmap bip) {// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);       // 把二维图片的每一行像素颜色值读取到一个一维数组中
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int rgb = r + g + b;
                if (rgb < blackMax)                //黑色
                {
                    pl[offset + x] = pixel;
                    colorNum[7]++;
                } else if (rgb < RGBMax) {        // 红绿蓝
                    pl[offset + x] = pixel;
                    if (r > g && r > b)
                        colorNum[1]++;            //红色
                    else if (g > b)
                        colorNum[2]++;            //绿色
                    else
                        colorNum[3]++;            //蓝色

                } else if (rgb < noiseMax) {      //黄、品和青
                    pl[offset + x] = pixel;
                    if (b < r && b < g)
                        colorNum[4]++;             //黄色
                    else if (g < r)
                        colorNum[5]++;             //品色
                    else
                        colorNum[6]++;            //青色
                } else {
                    pl[offset + x] = 0xffffffff;// 白色
                }
            }
        }
        CSHandler.sendEmptyMessage(40);
        Bitmap bitmap_result = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);//把颜色值重新赋给新建的图片 图片的宽高为以前图片的值
        bitmap_result.setPixels(pl, 0, width, 0, 0, width, height);
        return bitmap_result;
    }

    // 显示图片

    public static String sharp_result;
    private Bitmap bitmap_shape;
    private Bitmap bitmap_shape_rec;
    private Bitmap image1,image2;
    public Handler CSHandler = new Handler() {
        public void handleMessage(Message msg) {
          switch(msg.what)
          {
              case 10:
                  rec_image_show.setImageBitmap(bitmap_shape_rec);//形状颜色识别显示处理后的图像
                  break;

              case 20:
                  if (shapeNum<3) {
                      switch (shapeNum)//获取tft中图片的范围，以便裁切，得到retx，rety，disx，disy四个值
                      {
                          case 0:
                              image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//第一次获取图片
                              break;
                          case 1:
                              image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的需要对比的图片
                              CarShape.autoCut(image1, image2);//对比得出不同区域，得到四个重要的参数
                              break;
                          case 2:
                              image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//获取更新后的图片
                              CarShape.autoCut(image1, image2);//再次对比，提高准确性
                              break;
                      }
                      shapeNum++;
                      ConnectTransport.tftDown();
                      CSHandler.sendEmptyMessageDelayed(20, 2000);//重新进入case3
                  }
                  break;
              case 30:

                  break;

          }
        }
    };


    private Mat srcmat,dstmat,hsvmat,outma,mat_cut,hsvmat_1,dstmat_2;
    private Bitmap bitmap_opencv ;
    private Bitmap bitmap_opencv_cut;
    private Bitmap rebitmap_opencv;


    private void opencvCutmap(Bitmap bitmap_op,float retx,float rety,float x,float y) {
        //可以将原始图片部分识别区域切割出来，去除干扰的画面
        //切割图片可以自动切割和手动切割
        //自动切割的原理是对整个图进行轮廓搜索，将搜索出来的结果按轮廓面积进行分析，
        // 将面积最合适的那个作为识别区与进行切割。这种方法的有点在于不用手动去确定切割点坐标，缺点是由于画面内容比较复杂，
        // 找到的轮廓可能会很多，分析轮廓的运算较大对Android设备带来较大的运算负担，造成处理速度慢。
          image_show.setImageBitmap(bitmap_op);
            int pic_w = bitmap_op.getWidth(); // 得到图片的宽，高
            int pic_h = bitmap_op.getHeight();
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap_op, mat);  //通过org.opencv.android.Utils来实现Bitmap和Mat的相互转换
        Rect rect = new Rect(Math.round(pic_w*retx),Math.round(pic_h*rety),Math.round(pic_w*x),Math.round(pic_h*y));//裁切尺寸
        mat_cut = new Mat();
//                Bitmap bitmap2 = image_show.setImageBitmap(bitmap);
        if(bitmap_op == null){
            dstmat = new Mat(srcmat,rect);
            //转换为RGB
            Imgproc.cvtColor(dstmat,dstmat,Imgproc.COLOR_BGR2RGB);
        }
        else {
            Utils.bitmapToMat(bitmap_op,mat_cut);
            dstmat = new Mat(mat_cut,rect);
        }
            //save_pic(dstmat,2);//保存形状图片到手机
            rebitmap_opencv = Bitmap.createBitmap(dstmat.width(), dstmat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dstmat, rebitmap_opencv);//mat转化为bitmap
        //recImage_show.setImageBitmap(bitmap1);
         // saveBitmap(rebitmap_opencv,2);
        //saveBitmap(rebitmap_opencv);
            dstmat.release();
            mat_cut.release();

    }

    private Bitmap outline_bitmap;
    private void matcolor(Bitmap cut_bitmap_op,int way,int[] way_1,int[] way_2){  //颜色检测
        hsvmat = new Mat();
        hsvmat_1 = new Mat();
        //颜色转换，转换为HSV
        dstmat_2 = new Mat();
        outline_bitmap=cut_bitmap_op;
        Utils.bitmapToMat(outline_bitmap, dstmat_2);//mat原图
        //dstmat = new Mat();
        Imgproc.cvtColor(dstmat_2,hsvmat,Imgproc.COLOR_RGB2HSV);//hsvmat HSV格式mat原图
        Imgproc.cvtColor(dstmat_2,hsvmat_1,Imgproc.COLOR_RGB2HSV);//hsvmat HSV格式mat原图
        /******************************************************
         * 使用cvtColor(Mat src,Mat dst,Imgproc.COlOR_RGB2HSV)进行颜色模式转换
         * 颜色码	功能
         * Imgproc.COLOR_BGR2RGB	颜色空间转换
         * Imgproc.COLOR_BGR2GRAY	BGR转换到灰度空间
         * Imgproc.COLOR_GRAY2RGB	灰度转换到RGB
         * Imgproc.COLOR_RGB2HSV	RGB转换到HSV
         * Imgproc.COLOR_RGB2RGBA	添加alpha通道
         ********************************************/
        //颜色分割
        if(way==1)
        {
            Core.inRange(hsvmat,new Scalar(way_1[0],way_1[1],way_1[2]),new Scalar(way_1[3],way_1[4],way_1[5]),hsvmat);
        }
        else if(way==2)
        {
            Core.inRange(hsvmat,new Scalar(way_1[0],way_1[1],way_1[2]),new Scalar(way_1[3],way_1[4],way_1[5]),hsvmat);//范围1
            Core.inRange(hsvmat_1,new Scalar(way_2[0],way_2[1],way_2[2]),new Scalar(way_2[3],way_2[4],way_2[5]),hsvmat_1);//范围2
            Core.add(hsvmat,hsvmat_1,hsvmat);//合并，颜色处理完之后输出的是hsvmat
        }
        /*****************************************************
         * 颜色检测
         * Core.inRange(imgHSV,new Scalar(lowH,lowS,lowV),new Scalar(heighH.heighS,heighV),imgThresholded);
         *imgHSV HSV的Mat格式原图
         * Scalar lowHSV HSV范围下限
         * Scalar heigHSV HSV范围上线
         * imgThresholded 输出Mat格式图片，
         * 检测imgHSV图像的每一个像素是不是在lowHSV和heigHSV之间，如果是就设置为255，否则为0，保存在imgThresholded中
         * 演示代码：Core.inRange(src,new Scalar(0,255,144),new Scalar(0,255,255),dst);
         * 当有时候我们在得到的图片上会看到一些白色的噪点，或者轮廓不连续有断开。这种情况我们需要执行下开运算和闭运算
         *******************************************************/
       // kernel(hsvmat,15,15);   //确定运算核，类似于卷积核
    }


    private void kernel(Mat src,int i,int j){
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(i,j));//这就是一个运算核，一个ixj的矩阵
        //在执行开运算和闭运算之前我们要确定一个运算核,这个运算核是一个小矩阵。
        // 腐蚀运算就是在整张图像上计算给定内核区域的局部最小值,用最小值替换对应的像素值,而膨胀运算就是在整张图像上计算
        // 给定内核区域的局部最大值,用最大值替换对应的像索值。
        //开、闭运算
        //开运算的原理是通过先进行腐蚀操作,再进行膨胀操作得到。我们在移除小的对象时候很有用(假设物品是亮色,前景色是黑色),
        // 开运算可以去除噪声,消除小物体;在纤细点处分离物体;平滑较大物体的边界的同时并不明显改变其面积。
        // 比如在二值化图像没处理好的时候会有一些白色的噪点,可以通过开运算进行消除。
        //闭运算是开运算的一个相反的操作,具体是先进行膨胀然后进行腐蚀操作。
        // 通常是被用来填充前景物体中的小洞,或者抹去前景物体上的小黑点。
        // 因为可以想象,其就是先将白色部分变大,把小的黑色部分挤掉,然后再将一些大的黑色的部分还原回来，
        // 整体得到的效果就是:抹去前景物体上的小黑点了。
        //我们在优化图象时，可以先执行开运算消除背景上的白色噪点，在执行闭运算消除前景色上的黑色杂色。
        Imgproc.morphologyEx(src,src,Imgproc.MORPH_OPEN,kernel);//进行开运算
        Imgproc.morphologyEx(src,src,Imgproc.MORPH_CLOSE,kernel);//进行闭运算
        Utils.matToBitmap(src,outline_bitmap);
        rec_image_show.setImageBitmap(outline_bitmap);
        outline();
    }

    private List<MatOfPoint> contours;
    private int contoursCounts;

    private void  outline(){
        outma = new Mat();
        //轮廓识别
        contours= new ArrayList<>();
        Imgproc.findContours(hsvmat,contours,outma,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        /*******************
         * public static void findContours(Mat image,List<MatOfPoint> contours,Mat hierarchy,int mode,int method)
         image 一张二值图（即位图）。如果使用的mode是RETR_CCOMP或者RETR_FLOODFILL,那么输入的图像类型也可以是32位单通道整型,即CV_32SC1
         contours 检测到的轮廓。一个MatOfPoint保存一个轮廓,所有轮廓放在List中。
         hierarchy 可选的输出。包含轮廓之间的联系。4通道矩阵,元素个数为轮廓数量。通道 [ 0 ] ~通道 [ 3 ]对应保存:后个轮廓下标,
         前一个轮廓下标,父轮廓下标,内嵌轮廓下标。如 果没有后一个,前一个,父轮廓,内嵌轮廓,那么该通道的值为-1.
         mode 轮廓检索模式。
         标识符 含义     RETR_EXTERNAL 只检测最外围的轮廓
         RETR_LIST 检测所有轮廓，不建立等级关系，彼此独立
         RETR_CCOMP 检测所有轮廓，但所有轮廓只建立两个等级关系
         RETR_TREE 检测所有轮廓，并且所有轮廓建立一个树结构，层级完整
         method 轮廓近似法
         标识符 含义： CHAIN_APPROX_NONE 保存物体边界上所有连续的轮廓点
         CHAIN_APPROX_SIMPLE 压缩水平方向，垂直方向，对角线方向的元素，只保留该方向的终点坐标，例如一个矩形轮廓只需要4个点来保存轮廓信息
         CV_CHAIN_APPROX_TC89_L1 使用Teh-Chin链近似算法
         CV_CHAIN_APPROX_TC89_KCOS 使用Teh-Chin链近似算法
       ******************************************************************/
        contoursCounts  = contours.size();//轮廓数量
        //轮廓绘制
        Imgproc.drawContours(dstmat_2,contours,-1 , new Scalar(0,255,0),1);//绘制多边形
        /******************************************************************
         *public static void drawContours(Mat src,List<MatOfPoint> contours, int contourIdx,Scalar & color,int thickness)
         *src 目标图像
         *contours 所有轮廓信息，用findContours来得到轮廓信息
         *contoursIdx 指定绘制轮廓的下标，如果为负数，则绘制所有轮廓
         *color 绘制轮廓颜色
         *thickness 绘制轮廓的线宽度，如果为负数，则填充
         ********************************************************************/
        Utils.matToBitmap(dstmat_2,outline_bitmap);
        rec_image_show.setImageBitmap(outline_bitmap);
        outma.release();
        hsvmat.release();
        hsvmat_1.release();
    }

    public static int tri,rect,circle,star,rhomb;
    private void  shape(int aremin,int aremax,double eps) {
        MatOfPoint2f contour2f;
        MatOfPoint2f approxCurve;
        MatOfPoint2f contourlength;
        double epsilon;
        tri = rect = circle = star=rhomb= 0;
        for (int i= 0;i<contoursCounts;i++) {
            Log.d("cmd","第"+i+"个轮廓面积为："+Imgproc.contourArea(contours.get(i))+"");
            if (Imgproc.contourArea(contours.get(i)) > aremin&& Imgproc.contourArea(contours.get(i)) <aremax) {//面积筛选
                //某一个点的集合
             //  Log.d("cmd","第"+i+"个轮廓面积为："+Imgproc.contourArea(contours.get(i))+"");
                contour2f = new MatOfPoint2f(contours.get(i).toArray());
                epsilon = eps * Imgproc.arcLength(contour2f, true);
                //epsilon = a * Imgproc.arcLength(curve,true);
                //其中arcLength是计算轮廓点的个数，也就是周长。a 可按不同的图像测试取得最佳值
                //其中逼近精度epsilon可以手动指定，也可以通过curve轮廓点的个数进行计算
                approxCurve = new MatOfPoint2f();
                //多边形拟合
                Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
                /***************
                 * public static void approxPolyDP(MatOfpoint2f curve,
                 *                                MatOfPoint2f approxCurve,
                 *                                double epsilon,
                 *                                boolean closed)
                 * curve 输入的轮廓点集合
                 * approxCurve 输出的轮廓点集合。最小包容指定点集，保存的是多边形的顶点
                 * epsilon 拟合的精度，原始曲线和拟合曲线间的最大值
                 * closed 是否为封闭曲线，true为封闭，反之其中逼近精度epsilon可以手动指定，也可以通过curve轮廓点的个数进行计算
                 * ************/
                System.out .println("数量："+approxCurve.rows());
                Rect rect1 = Imgproc.boundingRect((Mat) approxCurve);
                Imgproc.rectangle(dstmat_2,new Point(rect1.x,rect1.y),new Point(rect1.x+rect1.width,rect1.y+rect1.height),new Scalar(255,255,0),4);
                //rectangle(Mat img,Point pt1,Point pt2,Scalar color,int thickness)，矩形绘制
                //img 需要绘制的图像Mat，pt1 矩形左上角，pt2 矩形右下角，color 直线颜色，thickness 直线的宽度，如果为负值，表示填充。
                //圆形绘制：circle(Mat img,Point center,int radius,Scalar color,int thickness)
                //img 需要绘制的图像Mat，center 圆心坐标，radius 圆半径，color 直线颜色
                //thickness 直线宽度，如果是封闭的图像，负值也可以填充颜色
                Utils.matToBitmap(dstmat_2,outline_bitmap);
                rec_image_show.setImageBitmap(outline_bitmap);
                /***形状的识别
                 * 形状识别的方法有很多种,本案例采用最简单的一种就是直接根据多边形顶点的个数进行判断。
                 * 这种方法最简单但**精度不高,**只能识别差别较大的几种形状。
                 *本处参考
                 *
                 * 信号分析法
                 * 使用Moments ()函数计算多边形的重点,求绕多边形一周重心到多边形轮廓线的距离。
                 * 把距离值形成信号曲线图,我们可以看到不同的形状信号曲线图区别很大。信号分析法可以识别多种类型的多边形形状，通过不同形状的信号图来确定不同的形状种类
                 * 实际开发中，在进行形状识别时，很可能会出现一些很小的瑕疵，经过开运算和闭运算也不能消除的杂质，
                 * 我们可以通过计算面积来进行过滤，小于多少的面积直接过滤掉，这也是一种好办法。通过Imgproc.contourArea(Mat mat)来进行多边形面积计算。
                 * **/
                if (approxCurve.rows() == 3) {
                    tri++;
                    Log.d("are","三角形面积为："+Imgproc.contourArea(contours.get(i))+"");
                }
                else if (approxCurve.rows() == 4){
                    //Log.d("cmd","contour2f:"+approxCurve.toList()+"");//获取顶点的坐标
                    List<Point> pointList = approxCurve.toList();//得到顶点坐标放入pointList
                    Point point1 = pointList.get(0);//将四个坐标分别保存
                    Point point2 = pointList.get(1);
                    Point point3 = pointList.get(2);
                    Point point4 = pointList.get(3);
                    double distance_x1 = Math.abs(point3.x - point1.x);//将对角线x坐标值相减
                    double distance_x2 = Math.abs(point4.x - point2.x);//将对角线y坐标值相减
                    double distance_y1 = Math.abs(point3.y - point1.y);
                    double distance_y2 = Math.abs(point4.y - point2.y);
                    double  diagonal_1 =Math.sqrt(Math.pow(distance_x1,2)+Math.pow(distance_y1,2));//得到对角线的长度
                    double  diagonal_2 =Math.sqrt(Math.pow(distance_x2,2)+Math.pow(distance_y2,2));
                    if (diagonal_1/diagonal_2>0.92&&diagonal_1/diagonal_2<1.08)//判断对角线是否相等
                        rect++;
                    else rhomb++;
                    Log.d("are","矩形面积为："+Imgproc.contourArea(contours.get(i))+"");

                }
                else if (approxCurve.rows() > 4) {
                    int mianji1 = rect1.height*rect1.width;
                    double mianji2 = Imgproc.contourArea(contours.get(i));
                    contourlength = new MatOfPoint2f(contours.get(i).toArray());
                    double zhouchang = Imgproc.arcLength(contourlength,true);
                    if((mianji2/mianji1)>0.75&&(zhouchang/(2*Math.sqrt((mianji2/3.14)))<3.8)){
                        circle++;
                        Log.d("are","圆形面积为："+Imgproc.contourArea(contours.get(i))+"");
                    }
                    else{
                        star++;
                        Log.d("shape","五角星算数结果："+zhouchang/(2*Math.sqrt((mianji2/3.14)))+"");
                    }
                    Log.d("are","五角星面积为："+Imgproc.contourArea(contours.get(i))+"");
                }
            }
        }
        dstmat_2.release();
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

}











