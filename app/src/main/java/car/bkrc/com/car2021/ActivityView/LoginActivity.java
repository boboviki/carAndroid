package car.bkrc.com.car2021.ActivityView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import car.bkrc.com.car2021.Utils.CameraUtile.CameraSearchService;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;
import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.OtherUtil.CameraConnectUtil;
import car.bkrc.com.car2021.Utils.OtherUtil.ToastUtil;
import car.bkrc.com.car2021.Utils.OtherUtil.WiFiStateUtil;
import car.bkrc.com.car2021.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText device_edit = null;
    private EditText login_edit = null;
    private EditText passwd_edit = null;
    private ToastUtil toastUtil;

    private Button bt_connect = null;
    private CheckBox rememberbox = null, wifi_box = null, uart_box = null;

    private ProgressDialog dialog = null;
    private CameraConnectUtil cameraConnectUtil;

    @RequiresApi(api = Build.VERSION_CODES.M)
    void Request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                if (getConnectWifiSsid() == "<unknown ssid>") {
                    toastUtil.ShowToast("当前未接入到Wi-Fi网络中，请连接平台Wi-Fi");
                } else toastUtil.ShowToast("当前连接WiFi：" + getConnectWifiSsid());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                    if (getConnectWifiSsid() == "<unknown ssid>") {
                        toastUtil.ShowToast("当前未接入到Wi-Fi网络中，请连接平台Wi-Fi");
                    } else toastUtil.ShowToast("当前连接WiFi：" + getConnectWifiSsid());
                }
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 判断是否是平板
        if (isPad(this)) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.activity_login_mobilephone);
        }
        EventBus.getDefault().register(this); // EventBus消息注册
        cameraConnectUtil = new CameraConnectUtil(this);
        findViews();  //控件初始化
        cameraConnectUtil.cameraInit();//摄像头初始化
        Request();
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findViews() {
        toastUtil = new ToastUtil(this);
        device_edit = findViewById(R.id.deviceid);
        login_edit = findViewById(R.id.loginname);
        passwd_edit = findViewById(R.id.loginpasswd);
        Button bt_reset = findViewById(R.id.reset);
        bt_connect = findViewById(R.id.connect);
        rememberbox = findViewById(R.id.remember);
        wifi_box = findViewById(R.id.wifi_each);
        uart_box = findViewById(R.id.uart_each);

        bt_reset.setOnClickListener(this);
        bt_connect.setOnClickListener(this);
        rememberbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwd_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwd_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        uart_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    uart_box.setChecked(true);
                    wifi_box.setChecked(false);
                    XcApplication.isserial = XcApplication.Mode.USB_SERIAL;
                    toastUtil.ShowToast("要把A72开发板的串口线接到竞赛平台哦！");
                } else {
                    uart_box.setChecked(false);
                }
            }
        });
        wifi_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifi_box.setChecked(true);
                    uart_box.setChecked(false);
                    XcApplication.isserial = XcApplication.Mode.SOCKET;
                    toastUtil.ShowToast("不要忘记把WiFi连接到竞赛平台哦！");
                } else {
                    wifi_box.setChecked(false);
                }
            }
        });
    }

    private String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    public String getConnectWifiSsidTwo() {
        WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
        assert wifiManager != null;

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID = wifiInfo.getSSID();

        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkId) {
                SSID = wifiConfiguration.SSID;
            }
        }

        return SSID.replace("\"", "");
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reset) {
            device_edit.setText("");
            login_edit.setText("");
            passwd_edit.setText("");
            rememberbox.setChecked(false);
        } else if (view.equals(bt_connect)) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("撸起袖子加载中...");
            dialog.show();
            if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
                useNetwork();
            } else if (XcApplication.isserial != XcApplication.Mode.SOCKET) {

                useUart();
            }
        }
    }

    /**
     * 接收Eventbus消息
     *
     * @param refresh
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
        if (refresh.getRefreshState() == 2) {
            startFirstActivity();
        }
    }

    // 搜索摄像cameraIP
    private void search() {
        Intent intent = new Intent(LoginActivity.this, CameraSearchService.class);
        startService(intent);
    }


    private void useUart() {
        // 搜索摄像头然后启动摄像头
        search();
    }

    private void useNetwork() {
        //2.
        if (new WiFiStateUtil(this).wifiInit()) {
            //WiFi初始化成功
            search();
        } else {
            dialog.cancel();
            toastUtil.ShowToast("请确认设备已通过WiFi接入竞赛平台！");
        }
    }

    private void startFirstActivity() {
        dialog.cancel();
        startActivity(new Intent(LoginActivity.this, FirstActivity.class));
        if (FirstActivity.IPCamera.equals("null:81")) {
            toastUtil.ShowToast("摄像头没有找到，快去找找它吧");
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); // EventBus消息注销
        if (dialog != null) {
            dialog.cancel();
        }
        Log.e("LoginActivity", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("LoginActivity", "onRestart");
    }

}

