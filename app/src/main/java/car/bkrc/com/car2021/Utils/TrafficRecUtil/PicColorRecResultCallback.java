package car.bkrc.com.car2021.Utils.TrafficRecUtil;

import android.graphics.Bitmap;

public interface PicColorRecResultCallback {

    /** 获取信息成功 */
    void onSuccess(String info);
    /** 获取信息失败 */
    void returnBitmap(Bitmap bitmap);
    void failure();
}
