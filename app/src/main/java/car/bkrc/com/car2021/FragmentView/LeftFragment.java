package car.bkrc.com.car2021.FragmentView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import car.bkrc.com.car2021.MessageBean.StateChangeBean;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;

import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.OtherUtil.CameraConnectUtil;
import car.bkrc.com.car2021.Utils.OtherUtil.RadiusUtil;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.ActivityView.LoginActivity;

import static car.bkrc.com.car2021.ActivityView.FirstActivity.IPCamera;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.toastUtil;
import static car.bkrc.com.car2021.FragmentView.RightFragment1.TAG;

public class LeftFragment extends Fragment{

    private float x1 = 0;
    private float y1 = 0;
    private ImageView image_show = null;
    private ImageButton refershImageButton;
    private Button reference_Btn;
    // 摄像头工具
    public static CameraCommandUtil cameraCommandUtil;
    private static TextView showip = null;
    private CameraConnectUtil cameraConnectUtil;
    public static byte[] img;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        // 判断设备
        if (LoginActivity.isPad(getActivity()))
            view = inflater.inflate(R.layout.left_fragment, container, false);
        else
            view = inflater.inflate(R.layout.left_fragment_mobilephone, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        cameraCommandUtil = new CameraCommandUtil();
        cameraConnectUtil = new CameraConnectUtil(getContext());
        getCameraPic();
        if (XcApplication.isserial == XcApplication.Mode.SOCKET && !IPCamera.equals("null:81")) {
            setCameraConnectState(true);
            showip.setText("WiFi-IP：" + FirstActivity.IPCar + "\n" + "Camera-IP:" + FirstActivity.purecameraip);
        } else if (XcApplication.isserial == XcApplication.Mode.SOCKET && IPCamera.equals("null:81")) {
            showip.setText("WiFi-IP：" + FirstActivity.IPCar + "\n" + "请重启您的平台设备！");
        }
        return view;
    }


    /**
     * 页面控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        image_show = view.findViewById(R.id.img);
        refershImageButton = view.findViewById(R.id.refresh_img_btn);
        reference_Btn = view.findViewById(R.id.refresh_btn);

        reference_Btn.setOnClickListener(v -> ObjectrotationAnim(refershImageButton));
        refershImageButton.setOnClickListener(v -> ObjectrotationAnim(refershImageButton));
        showip = (TextView) view.findViewById(R.id.showip);
        image_show.setOnTouchListener(new ontouchlistener1());
        Button clear_coded_disc = (Button) view.findViewById(R.id.clear_coded_disc);

    }

    private void getCameraPic() {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                if (IPCamera.equals("null:81")) return;
                while (true) {
                    getBitmap();
                }
            }
        });
    }

    private Bitmap newbitmap = null;
//    private Bitmap Autobitmap = null;
    /**
     * 接收Eventbus消息
     *
     * @param refresh
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
        if (refresh.getRefreshState() == 1) {
            Log.e(TAG, "onEventMainThread: "  + IPCamera);
            if (IPCamera.equals("null:81")){
                Log.e(TAG, "onEventMainThread: "  + IPCamera);
                cameraConnectUtil.cameraStopService();
                cameraConnectUtil.cameraInit();
                cameraConnectUtil.search();
            }else {
                cameraConnectUtil.cameraStopService();
                cameraConnectUtil.cameraInit();
                cameraConnectUtil.search();
            }
        } else if (refresh.getRefreshState() == 2) {
            getCameraPic();
            if (XcApplication.isserial == XcApplication.Mode.SOCKET && !IPCamera.equals("null:81")) {
                setCameraConnectState(true);
                toastUtil.ShowToast("摄像头已连接");
                showip.setText("WiFi-IP：" + FirstActivity.IPCar + "\n" + "Camera-IP:" + FirstActivity.purecameraip);
            } else if (XcApplication.isserial == XcApplication.Mode.SOCKET && IPCamera.equals("null:81")) {
                showip.setText("WiFi-IP：" + FirstActivity.IPCar + "\n" + "请重启您的平台设备！");
            }
        } else if (refresh.getRefreshState() == 4) {
            connectState = false;
            if (bitmap != null){
                bitmap.recycle();
            }
            setBitmap(newbitmap);

        }
    }


    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            assert manager != null;
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空
            if (networkInfo != null)
                return networkInfo.isConnected();
        }
        return false;
    }

    @SuppressLint("HandlerLeak")
    public static Handler showidHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 22) {
                showip.setText(msg.obj + "\n" + "Camera-IP：" + IPCamera);
            }
        }
    };

    public static void setBitmap(Bitmap bitmap) {
        LeftFragment.bitmap = bitmap;
    }


    // 图片
    public static Bitmap bitmap;
    public static Bitmap bitmap2;
//    public static RightAutoFragment RightAutoFragment;

    // 摄像头连接状态，默认为true
    private boolean cameraConnectState = true;

    public boolean isCameraConnectState() {
        return cameraConnectState;
    }

    public void setCameraConnectState(boolean cameraConnectState) {
        this.cameraConnectState = cameraConnectState;
    }

    private boolean connectState = true;

    // 得到当前摄像头的图片信息
    public void getBitmap() {
        setBitmap(cameraCommandUtil.httpForImage(IPCamera));
        if (bitmap != null) {
            setBitmap(RadiusUtil.roundBitmapByXfermode(bitmap, bitmap.getWidth(), bitmap.getHeight(), 8));

        } else {
            setCameraConnectState(false);
        }
        phHandler.sendEmptyMessage(10);
    }


    // 显示图片
    @SuppressLint("HandlerLeak")
    private Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                image_show.setImageBitmap(bitmap);
                bitmap2 =bitmap;
//                img=Bitmap2Bytes(bitmap);//图片转化为数组
//                Intent intent =new Intent();
//                intent.putExtra("img",Bitmap2Bytes(bitmap));
//                intent.setClass(LeftFragment.this,v.class);
//                LeftFragment.this.startActivity(intent);

//                Intent intent  = new Intent(LeftFragment.this,RightAutoFragment.class);
//
//                mImageView.setDrawingCacheEnabled(Boolean.TRUE);
//                intent.putExtra("BITMAP", mImageView.getDrawingCache()); //这里可以放一个bitmap
//                startActivity(intent);

            } else if (msg.what == 11) {

            }
        }
    };


    //bitmap转化为byte数组的方法：
//    private byte[] Bitmap2Bytes(Bitmap bm){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//        }



    private class ontouchlistener1 implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 点击位置坐标
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                // 弹起坐标
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float xx = x1 > x2 ? x1 - x2 : x2 - x1;
                    float yy = y1 > y2 ? y1 - y2 : y2 - y1;
                    // 判断滑屏趋势
                    int MINLEN = 30;
                    if (xx > yy) {
                        if ((x1 > x2) && (xx > MINLEN)) {        // left
                            toastUtil.ShowToast("向左微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 4, 1);  //左
                                }
                            });

                        } else if ((x1 < x2) && (xx > MINLEN)) { // right
                            toastUtil.ShowToast("向右微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 6, 1);  //右
                                }
                            });
                        }
                    } else {
                        if ((y1 > y2) && (yy > MINLEN)) {        // up
                            toastUtil.ShowToast("向上微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 0, 1);  //上
                                }
                            });
                        } else if ((y1 < y2) && (yy > MINLEN)) { // down
                            toastUtil.ShowToast("向下微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 2, 1);  //下
                                }
                            });
                        }
                    }
                    x1 = 0;
                    x2 = 0;
                    y1 = 0;
                    y2 = 0;

                    break;
            }
            return true;
        }
    }


    /**
     * 刷新按钮实现顺时针360度
     *
     * @param view
     */
    private void ObjectrotationAnim(View view) {
        //构造ObjectAnimator对象的方法
        EventBus.getDefault().post(new DataRefreshBean(1));
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0.0F, 360.0F);// 设置顺时针360度旋转
        animator.setDuration(1500);//设置旋转时间
        animator.start();//开始执行动画（顺时针旋转动画）
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
