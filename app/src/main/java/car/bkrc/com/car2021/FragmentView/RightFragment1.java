package car.bkrc.com.car2021.FragmentView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import car.bkrc.com.car2021.DataProcessingModule.QR_Recognition;
import car.bkrc.com.car2021.MessageBean.StateChangeBean;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.OtherUtil.WiFiStateUtil;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.ActivityView.LoginActivity;

import static car.bkrc.com.car2021.ActivityView.FirstActivity.Connect_Transport;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.but_handler;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.toastUtil;

public class RightFragment1 extends Fragment {

    String Camera_show_ip = null;

    private TextView Data_show = null;
    private EditText speededit = null;
    private EditText coded_discedit = null;
    private EditText angle_dataedit = null;
    private TextView stateTV, psStatusTV, codedDiskTV, lightTV, ultraSonicTV;

    public static final String TAG = "RightFragment1";
    private View view = null;
    public static RightAutoFragment RightAutoFragment = new RightAutoFragment();

    private boolean dateGetState = true; // ???????????????????????????

    public static RightFragment1 getInstance() {
        return RightFragment1Holder.sInstance;
    }

    private static class RightFragment1Holder {
        private static final RightFragment1 sInstance = new RightFragment1();
    }



    private int Rgb_flag=0;
    private int QR1_flag=0;
    private int shape_flag=0;
    private int camre_flag=0;
    // ?????????????????????????????????
    @SuppressLint("HandlerLeak")
    private Handler rehHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                byte[] mByte = (byte[]) msg.obj;
                    if (mByte[0] == 0x55) {
                    // ????????????
                    long psStatus = mByte[3] & 0xff;
                    // ???????????????
                    long ultraSonic = mByte[5] & 0xff;
                    ultraSonic = ultraSonic << 8;
                    ultraSonic += mByte[4] & 0xff;
                    // ????????????
                    long light = mByte[7] & 0xff;
                    light = light << 8;
                    light += mByte[6] & 0xff;
                    // ??????
                    long codedDisk = mByte[9] & 0xff;
                    codedDisk = codedDisk << 8;
                    codedDisk += mByte[8] & 0xff;
                    Camera_show_ip = FirstActivity.IPCamera;
                    if (mByte[1] == (byte) 0xaa) {  //??????
                        if (dateGetState) {
                            // ????????????
                            stateTV.setText(mByte[2] + "");              // ????????????
                            psStatusTV.setText(psStatus + "");        // ????????????
                            codedDiskTV.setText(codedDisk + "");      // ??????
                            lightTV.setText(light + " lx");           // ?????????
                            ultraSonicTV.setText(ultraSonic + " mm"); // ?????????
                            Data_show.setText("???  ???");

                        }
                        switch (mByte[2]) {
                            case (byte) 0xb1://???????????????
                                mByte[2] = 0x00;//???0
                                if (mByte[7] == (byte) 0xbb) {
                                    if (Rgb_flag == 0) {
//                                        Message message = new Message();
//                                        message.what = 6;
//                                        Connect_Transport.cameraSet.sendMessage(message);
                                        Connect_Transport.mark = 1;//?????????
                                        Connect_Transport.autoDrive();
                                        Rgb_flag = 1;
                                    }

                                }
                                break;
                            case (byte) 0xb2://???????????????
                                mByte[2] = 0x00;//???0
                                if (mByte[7] == (byte) 0xbb) {
                                    if (QR1_flag == 0) {
                                        Connect_Transport.mark = 2;//?????????
                                        Connect_Transport.autoDrive();
                                        QR1_flag = 1;
                                    }
                                }
                                break;
                            case (byte) 0xb3://????????????
                                mByte[2] = 0x00;//???0
                                if (mByte[7] == (byte) 0xbb) {
                                    if (shape_flag == 0) {
                                        Connect_Transport.mark = 3;//??????????????????
                                        Connect_Transport.autoDrive();
                                        shape_flag = 1;
                                    }
                                }
                                break;
                            case (byte) 0xb4://???????????????
                                mByte[2] = 0x00;//???0
                                if (mByte[7] == (byte) 0xbb) {
                                    Message message;
                                    switch (mByte[3]) {
                                        case (byte) 0x01://???????????????
                                            message = new Message();
                                            message.what = 1;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x02://???????????????
                                            message = new Message();
                                            message.what = 2;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x03://???????????????
                                            message = new Message();
                                            message.what = 3;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x04://???????????????
                                            message = new Message();
                                            message.what = 4;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x05://????????????????????????1
                                            message = new Message();
                                            message.what = 5;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x06://????????????????????????1
                                            message = new Message();
                                            message.what = 6;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x07://???????????????
                                            message = new Message();
                                            message.what = 7;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x08://????????????????????????2
                                            message = new Message();
                                            message.what = 8;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x09://????????????????????????2
                                            message = new Message();
                                            message.what = 9;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x10://????????????????????????3
                                            message = new Message();
                                            message.what = 10;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                        case (byte) 0x11://????????????????????????3
                                            message = new Message();
                                            message.what = 11;
                                            Connect_Transport.cameraSet.sendMessage(message);
                                            break;
                                    }
                                    break;
                                }


                        }
                    }
                    if (mByte[1] == (byte) 0x02) //??????
                    {
                        if (!dateGetState) {
                            if (mByte[2] == -110) {
                                byte[] newData = new byte[50];
                                Log.e("data", "" + mByte[4]);
                                newData = Arrays.copyOfRange(mByte, 5, mByte[4] + 5);
                                Log.e("data", "" + "??????" + newData.length);
                                try {
                                    String str = new String(newData, "ascii");//?????????????????????????????????
                                    Toast.makeText(getActivity(), "" + str, Toast.LENGTH_LONG).show();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                // ????????????
                                stateTV.setText(mByte[2] + "");              // ????????????
                                psStatusTV.setText(psStatus + "");        // ????????????
                                codedDiskTV.setText(codedDisk + "");      // ??????
                                lightTV.setText(light + " lx");           // ?????????
                                ultraSonicTV.setText(ultraSonic + " mm"); // ?????????
                                Data_show.setText("???  ???");
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            if (LoginActivity.isPad(getActivity()))
                view = inflater.inflate(R.layout.right_fragment1, container, false);
            else
                view = inflater.inflate(R.layout.right_fragment1_mobilephone, container, false);
        }
        FirstActivity.recvhandler = rehHandler;
        EventBus.getDefault().register(this); // EventBus????????????
        control_init();
        connect_Open();
        return view;
    }



    /**
     * ???????????????
     */
    private void control_init() {
        Data_show = view.findViewById(R.id.rvdata);
        speededit = view.findViewById(R.id.speed_data);
        coded_discedit = view.findViewById(R.id.coded_disc_data);
        angle_dataedit = view.findViewById(R.id.angle_data);
        stateTV = view.findViewById(R.id.stateTV);
        psStatusTV = view.findViewById(R.id.psStatusTV);
        codedDiskTV = view.findViewById(R.id.codedDiskTV);
        lightTV = view.findViewById(R.id.lightTV);
        ultraSonicTV = view.findViewById(R.id.ultraSonicTV);

        ImageButton up_bt = view.findViewById(R.id.up_button);
        ImageButton blew_bt = view.findViewById(R.id.below_button);
        ImageButton stop_bt = view.findViewById(R.id.stop_button);
        ImageButton left_bt = view.findViewById(R.id.left_button);
        ImageButton right_bt = view.findViewById(R.id.right_button);

        up_bt.setOnClickListener(new onClickListener2());
        blew_bt.setOnClickListener(new onClickListener2());
        stop_bt.setOnClickListener(new onClickListener2());
        left_bt.setOnClickListener(new onClickListener2());
        right_bt.setOnClickListener(new onClickListener2());
        up_bt.setOnLongClickListener(new onLongClickListener2());
    }

    /**
     * ???????????????????????????Eventbus??????
     *
     * @param refresh
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
        if (refresh.getRefreshState() == 1 && new WiFiStateUtil(getActivity()).wifiInit()) {
            connect_Open();
        } else if (refresh.getRefreshState() == 3) {
            toastUtil.ShowToast("???????????????");
        } else if (refresh.getRefreshState() == 4) {
            toastUtil.ShowToast("?????????????????????");
        } else toastUtil.ShowToast("?????????WiFi???????????????");

    }

    /**
     * ?????????????????????????????????Eventbus??????
     *
     * @param stateChangeBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventStateThread(StateChangeBean stateChangeBean) {
        if (stateChangeBean.getStateChange() == 0) {
            dateGetState = true;
        } else if (stateChangeBean.getStateChange() == 0) {
            dateGetState = false;
        }

    }

    /**
     * ????????????????????????
     */
    private void connect_Open() {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            connect_thread();                            //????????????????????????
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {
            serial_thread();   //???????????????uart4
        }
    }

    private void connect_thread() {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                FirstActivity.Connect_Transport.connect(rehHandler, FirstActivity.IPCar);
            }
        });
    }

    private void serial_thread() {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                FirstActivity.Connect_Transport.serial_connect(rehHandler);
            }
        });
    }

    // ?????????????????????
    private int getSpeed() {
        String src = speededit.getText().toString();
        int speed = 90;
        if (!src.equals("")) {
            speed = Integer.parseInt(src);
        } else {
            toastUtil.ShowToast("??????????????????????????????");
        }
        return speed;
    }

    private int getEncoder() {
        String src = coded_discedit.getText().toString();
        int encoder = 20;
        if (!src.equals("")) {
            encoder = Integer.parseInt(src);
        } else {
            toastUtil.ShowToast("?????????????????????");
        }
        return encoder;
    }

    private int getAngle() {
        String src = angle_dataedit.getText().toString();
        int angle = 50;
        if (!src.equals("")) {
            angle = Integer.parseInt(src);
        } else {
            toastUtil.ShowToast("???????????????????????????");
        }
        return angle;
    }

    // ??????????????????
    private int sp_n;

    private class onClickListener2 implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sp_n = getSpeed();

            switch (v.getId()) {
                case R.id.up_button:
                    int en_n = getEncoder();
                    FirstActivity.Connect_Transport.go(sp_n, en_n);
                    break;
                case R.id.left_button:
                    FirstActivity.Connect_Transport.left(sp_n);
                    break;
                case R.id.right_button:
                    FirstActivity.Connect_Transport.right(sp_n);
                    break;
                case R.id.below_button:
                    en_n = getEncoder();
                    FirstActivity.Connect_Transport.back(sp_n, en_n);
                    break;
                case R.id.stop_button:
                    FirstActivity.Connect_Transport.stop();
                    break;
            }
        }
    }

    private class onLongClickListener2 implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            if (view.getId() == R.id.up_button) {
                sp_n = getAngle();
                FirstActivity.Connect_Transport.line(sp_n);
            }
    /*?????????onLongClick??????false???????????????????????????????????????????????????????????????
    ????????????true????????????????????????*/
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private Result result;

    private void QRRecon(Bitmap bitmap) {
        if (bitmap != null)
            new Thread(() -> {
                QR_Recognition rSource = new QR_Recognition(
                        bitmap);
                try {
                    BinaryBitmap binaryBitmap = new BinaryBitmap(
                            new HybridBinarizer(rSource));
                    Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                    hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                    QRCodeReader reader = new QRCodeReader();
                    result = reader.decode(binaryBitmap, hint);
                    qrHandler.sendEmptyMessage(10);
                    System.out.println("????????????");
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    qrHandler.sendEmptyMessage(30);
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            }).start();
    }

    // ????????????????????????
    @SuppressLint("HandlerLeak")
    Handler qrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (result.toString() != null)
                        toastUtil.ShowToast(result.toString());
                    break;
                case 20:
                    break;
                case 30:
                    toastUtil.ShowToast("????????????????????????");
                    break;
                default:
                    break;
            }
        }
    };
}


