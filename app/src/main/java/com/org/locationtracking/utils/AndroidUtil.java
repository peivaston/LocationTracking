package com.org.locationtracking.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Locale;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import lombok.NonNull;


//*********************************************************************
public class AndroidUtil
//*********************************************************************
{
    public static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Currently active context.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context sContext = null;

    /**
     * Toast a message.
     *
     * @param longToast Should the toast be a long one?
     * @param message   Message to toast.
     */
    //******************************************************************
    @UiThread
    public static void toast(boolean longToast,
                             @NonNull String message)
    //******************************************************************
    {

        //show toast
        Toast
                .makeText(sContext, message, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
                .show();
    }

    //******************************************************************
    public static void setContext(Context context)
    //******************************************************************
    {
        sContext = context;
    }

    //******************************************************************
    @CheckResult
    public static @NonNull
    Context getContext()
    //******************************************************************
    {
        return sContext;
    }

    //******************************************************************
    @CheckResult
    public static @NonNull
    Context getApplicationContext()
    //******************************************************************
    {
        return sContext.getApplicationContext();
    }


}
