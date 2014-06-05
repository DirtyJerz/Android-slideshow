package com.udcoled.color.myapplication2.app;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ViewFlipper;
import android.widget.ImageView;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.future.SimpleFuture;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.ReconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.SocketIORequest;
import com.koushikdutta.async.http.socketio.StringCallback;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
public class FullscreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(layoutParams);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        myViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        for (int i = 0; i < image.length; i++) {
            ImageView imageView = new ImageView(FullscreenActivity.this);
            imageView.setImageResource(image[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            myViewFlipper.addView(imageView);
        }
        //myViewFlipper.setAutoStart(true);
        //myViewFlipper.setFlipInterval(5000);
        //myViewFlipper.startFlipping();
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://192.168.10.1:70", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        System.out.println(string);
                    }
                });
                client.on("changeSlide", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + argument.toString());
                        myViewFlipper.setDisplayedChild(argument.optInt(0));
                    }
                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        System.out.println("json: " + json.toString());
                    }
                });
            }
        });
    }
    private ViewFlipper myViewFlipper;
    private float initialXPoint;
    int[] image = { R.drawable.fhd_r, R.drawable.fhd_g,
            R.drawable.fhd_b, R.drawable.fhd_black, R.drawable.fhd_w};


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialXPoint = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalx = event.getX();
                if (initialXPoint > finalx) {
                    if (myViewFlipper.getDisplayedChild() == image.length)
                        break;
                    myViewFlipper.showNext();
                } else {
                    if (myViewFlipper.getDisplayedChild() == 0)
                        break;
                    myViewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }

}


