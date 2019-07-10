package comtesttest.example.admin.myapplication.util;

/**
 * Created by wrs on 2019/7/1,18:57
 * projectName: AutoInstaller-master
 * packageName: top.wuhaojie.installer
 * <p>
 * ProgressDialogUtils.getInstance().show(
 * MainActivity.this,
 * "测试");
 * <p>
 * ProgressDialogUtils.getInstance().setNum(progress);
 * ProgressDialogUtils.getInstance().dismiss();
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class ProgressDialogUtils {
    private static ProgressDialog mProgressDialog = null;
    private static Activity mContext;
    private static ProgressDialogUtils mInstance = null;

    public static ProgressDialogUtils getInstance() {
        if (mInstance == null) {
            mInstance = new ProgressDialogUtils();
        }
        return mInstance;
    }


    public void show(Activity activity, String mMessage) {
        try {
            if (activity == null) {
                return;
            }
            // 开始请求是，显示请求对话框
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(activity, 0);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //  STYLE_HORIZONTAL STYLE_SPINNER
                mProgressDialog.setTitle(mMessage);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                });
            }
            if (!activity.isFinishing()) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        System.out.println("dismiss");
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void setNum(int num) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(num);
        }
    }
}
