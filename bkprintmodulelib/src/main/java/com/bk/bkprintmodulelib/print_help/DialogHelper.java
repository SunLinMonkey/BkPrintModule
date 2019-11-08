package com.bk.bkprintmodulelib.print_help;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.bk.bkprintmodulelib.R;

public class DialogHelper {
    private static ProgressDialog mProgressDialog;


    public static void showProgressDialog(Context context, String message) {
        if (((Activity) context).isFinishing()) {
            return;
        }
        hideProgressDialog();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        if (!((Activity) context).isFinishing()) {
            mProgressDialog.show();
        }
    }


    public static void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            Log.e("aaa", e.toString());
        }

    }
}
