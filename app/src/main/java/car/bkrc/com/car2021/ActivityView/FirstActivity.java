package car.bkrc.com.car2021.ActivityView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.MessageBean.StateChangeBean;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.support.design.widget.BottomNavigationView;
import android.widget.Button;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import car.bkrc.com.car2021.DataProcessingModule.ConnectTransport;
import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.OtherUtil.CameraConnectUtil;
import car.bkrc.com.car2021.Utils.OtherUtil.ToastUtil;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.ViewAdapter.ViewPagerAdapter;
import car.bkrc.com.car2021.Utils.OtherUtil.Transparent;
import car.bkrc.com.car2021.yolov5ncnn.Yolov5Fragment;
import car.bkrc.com.car2021.FragmentView.LeftFragment;
import car.bkrc.com.car2021.FragmentView.RightFragment1;
import car.bkrc.com.car2021.FragmentView.RightInfraredFragment;
import car.bkrc.com.car2021.FragmentView.RightOtherFragment;
import car.bkrc.com.car2021.FragmentView.RightZigbeeFragment;
import car.bkrc.com.car2021.Utils.OtherUtil.TitleToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FirstActivity extends AppCompatActivity {
    private ViewPager viewPager;

    private static Context Context = null;
    public static ToastUtil toastUtil;
    public static ConnectTransport Connect_Transport;
    public static RightAutoFragment RightAutoFragment;
    private Button auto_btn;
    // 设备ip
    public static String IPCar;
    // 摄像头IP
    public static String IPCamera = null;
    public static String purecameraip = null;
    public static boolean chief_control_flag = true; //主从控制
    public static Handler recvhandler = null;
    public static Handler but_handler;  //是按钮和menu同步
    private ViewPager mLateralViewPager;
    private CameraConnectUtil cameraConnectUtil;

    public static boolean full_go_falg = false;//定义全自动驾驶状态

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {//增加功能页面
            switch (item.getItemId()) {
                case R.id.yolov5_page_item:
                    mLateralViewPager.setCurrentItem(0);
                    return true;
                case R.id.auto_page_item:
                    mLateralViewPager.setCurrentItem(1);
                    return true;
                case R.id.home_page_item:
                    mLateralViewPager.setCurrentItem(2);
                    return true;
                case R.id.scene_setting_item:
                    mLateralViewPager.setCurrentItem(3);
                    return true;
                case R.id.device_manage_item:
                    mLateralViewPager.setCurrentItem(4);
                    return true;
//                case R.id.personal_center_item:
//                    mLateralViewPager.setCurrentItem(5);
//                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first1);
        initAll();

    }

    public static Context getContext() {
        return Context;
    }
    private void initAll(){
        but_handler = button_handler;  //用于leftfragment中隐藏的按钮和标题栏中的menu同步
        toastUtil = new ToastUtil(this);
        if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL) {  //竞赛平台和a72通过usb转串口通信
            mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS); //启动usb的识别和获取
            Transparent.showLoadingMessage(this, "正在拼命追赶串口……", false);//启动旋转效果的对话框，实现usb的识别和获取
        }
        EventBus.getDefault().register(this); // EventBus消息注册
        TitleToolbar mToolbar = (TitleToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //设置自动驾驶按钮的监听事件
        auto_btn = findViewById(R.id.auto_drive_btn);
        auto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               autoDriveAction();//运行自动驾驶函数
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);//使用viewPager实现页面滑动效果
        viewPager.setOffscreenPageLimit(5);//改之前为3

        // 底部导航栏
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        nativeView(bottomNavigationView);
        Connect_Transport = new ConnectTransport();    //实例化连接类
        cameraConnectUtil = new CameraConnectUtil(this);
        //RightAutoFragment =new RightAutoFragment();
        //RightAutoFragment.task();
    }

    private void nativeView(BottomNavigationView navigation) {
        navigation = findViewById(R.id.bottomNavigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mLateralViewPager = findViewById(R.id.viewpager);//获取到ViewPager
        setupViewPager(viewPager);                      //加载fragment
        //ViewPager的监听
        final BottomNavigationView finalNavigation = navigation;
        mLateralViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                finalNavigation.getMenu().getItem(position).setChecked(true);
                //写滑动页面后做的事，使每一个fragmen与一个page相对应
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Yolov5Fragment.getInstance());
        adapter.addFragment(RightAutoFragment.getInstance());
        adapter.addFragment(RightFragment1.getInstance());
        adapter.addFragment(RightZigbeeFragment.getInstance());
        adapter.addFragment(RightInfraredFragment.getInstance());
       // adapter.addFragment(RightOtherFragment.getInstance());
        viewPager.setAdapter(adapter);
    }


    private Menu toolmenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   //activity创建时创建菜单Menu
        getMenuInflater().inflate(R.menu.tool_rightitem, menu);
        toolmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //菜单项监听
        int id = item.getItemId();
        // Toast.makeText(FirstActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
        switch (id) {
            case R.id.car_status:
                if (item.getTitle().equals("接收主车状态")) {
                    item.setTitle(getResources().getText(R.string.follow_status));
                    Connect_Transport.stateChange(2);
                    EventBus.getDefault().post(new StateChangeBean(0));
                } else if (item.getTitle().equals("接收从车状态")) {
                    item.setTitle(getResources().getText(R.string.main_status));
                    Connect_Transport.stateChange(1);
                    EventBus.getDefault().post(new StateChangeBean(1));
                }
                break;
            case R.id.car_control:
                if (item.getTitle().equals("控制主车")) {
                    chief_control_flag = true;
                    item.setTitle(getResources().getText(R.string.follow_control));
                    Connect_Transport.TYPE = 0xAA;
                } else if (item.getTitle().equals("控制从车")) {
                    chief_control_flag = false;
                    item.setTitle(getResources().getText(R.string.main_control));
                    Connect_Transport.TYPE = 0x02;
                }
                break;
            case R.id.clear_coded_disc:
                Connect_Transport.clear();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int autoDrive_int=0;
    private void autoDriveAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置Title的内容
        builder.setIcon(R.mipmap.rc_logo);
        builder.setTitle("温馨提示");
        // 设置Content来显示一个信息
        builder.setMessage("请确认是否开始自动驾驶！");
        // 设置一个PositiveButton
        builder.setPositiveButton("开始", (dialog, which) -> {
            dialog.dismiss();
            Connect_Transport.mark =1;//进入形状识别
            Connect_Transport.autoDrive();

//            Log.e("cmd", "RgbOpen值为"+RightAutoFragment.RgbOpencvFlag);
//            Connect_Transport.mark=2;
//            Connect_Transport.autoDrive();
//             toastUtil.ShowToast( "开始自动驾驶，请检查车辆周围环境！");

        });
        // 设置一个NegativeButton
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @SuppressLint("HandlerLeak")
    private Handler button_handler = new Handler()  //实现menu和leftfragment中的三个按钮同步
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    toolmenu.getItem(1).setTitle(getResources().getText(R.string.follow_status));
                    break;
                case 22:
                    toolmenu.getItem(1).setTitle(getResources().getText(R.string.main_status));
                    break;
                case 33:
                    toolmenu.getItem(2).setTitle(getResources().getText(R.string.follow_control));
                    break;
                case 44:
                    toolmenu.getItem(2).setTitle(getResources().getText(R.string.main_control));
                    break;
                default:
                    break;

            }
        }
    };


    /**
     * 接收Eventbus消息
     *
     * @param refresh
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
         if (refresh.getRefreshState() == 4) {
        }
    }


    //------------------------------------------------------------------------------------------
    //获取和实现usb转串口的通信，实现A72和竞赛平台的串口通信
    public static UsbSerialPort sPort = null;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.e(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {   //新的数据
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = recvhandler.obtainMessage(1, data);
                            msg.sendToTarget();
                            FirstActivity.this.updateReceivedData(data);
//自己写的新增监听接口给各个fragment传数据/*****实验，新增数据接口，暂未实现*******/
                            Log.e("tag",newDataListeners.toArray().toString());
                            for(NewDataListener listener : newDataListeners){
                                listener.receiveData(data);
                            }
                        }
                    });
                }
            };

    private List<NewDataListener> newDataListeners = new ArrayList<>();
    public void addNewDataListener(NewDataListener listener){
        if(!newDataListeners.contains(listener)){
            newDataListeners.add(listener);
        }
    }
    public void removeNewDataListener(NewDataListener listener){
        if(newDataListeners.contains(listener)){
            newDataListeners.remove(listener);
        }
    }

    public interface NewDataListener{

        void receiveData(byte[] data);
    }

    /*****实验，新增数据接口，暂未实现*******/

    protected void controlusb() {
        Log.e(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            toastUtil.ShowToast("没有串口驱动！");
        } else {
            openUsbDevice();
            if (connection == null) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                toastUtil.ShowToast("串口驱动失败！");
                return;
            }
            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                toastUtil.ShowToast("串口驱动错误！");
                try {
                    sPort.close();
                } catch (IOException e2) {
                }
                sPort = null;
                return;
            }
        }
        onDeviceStateChange();
        Transparent.dismiss();//关闭加载对话框
    }

    // 在打开usb设备前，弹出选择对话框，尝试获取usb权限
    private void openUsbDevice() {
        tryGetUsbPermission();
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbDeviceConnection connection;

    private void tryGetUsbPermission() {

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            if (mUsbManager.hasPermission(usbDevice)) {
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            } else {
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    private void afterGetUsbPermission(UsbDevice usbDevice) {

        toastUtil.ShowToast("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId());
        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        connection = mUsbManager.openDevice(usbDevice);
    }

    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        toastUtil.ShowToast("Permission denied for device" + usbDevice);
                    }
                }
            }
        }
    };

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.e(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.e(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener); //添加监听
            mExecutor.submit(mSerialIoManager); //在新的线程中监听串口的数据变化
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        //  Log.e("read data is ：：","   "+message);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraConnectUtil.destroy();
        if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL) {
            try {
                unregisterReceiver(mUsbPermissionActionReceiver);
                sPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException ignored) {

            }
            sPort = null;
        } else if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            Connect_Transport.destory();
        }
    }

    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 5000;
    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private final String TAG = FirstActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    refreshDeviceList();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler usbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                try {
                    useUsbtoserial();
                } catch (IndexOutOfBoundsException e) {
                    Transparent.dismiss();//关闭加载对话框
                    toastUtil.ShowToast("串口通信失败，请检查设备连接状态！");
                }
            }
        }
    };

    private void useUsbtoserial() {
        final UsbSerialPort port = mEntries.get(0);  //A72上只有一个 usb转串口，用position =0即可
        final UsbSerialDriver driver = port.getDriver();
        final UsbDevice device = driver.getDevice();
        final String usbid = String.format("Vendor %s  ，Product %s",
                HexDump.toHexString((short) device.getVendorId()),
                HexDump.toHexString((short) device.getProductId()));
        Message msg = LeftFragment.showidHandler.obtainMessage(22, usbid);
        msg.sendToTarget();
        FirstActivity.sPort = port;
        if (sPort != null) {
            controlusb();  //使用usb功能
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void refreshDeviceList() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.e(TAG, "Refreshing device list ...");
                Log.e("mUsbManager is :", "  " + mUsbManager);
                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.e(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                usbHandler.sendEmptyMessage(2);
                Log.e(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
            }
        }.execute((Void) null);
    }

}
