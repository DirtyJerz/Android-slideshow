package com.udcoled.color.myapplication2.app;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ImageView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;




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
        connectWebSocket();
        //myViewFlipper.setDisplayedChild(argument.optInt(0));


    }
    private ViewFlipper myViewFlipper;
    private float initialXPoint;
    int[] image = { R.drawable.fhd_r, R.drawable.fhd_g,
            R.drawable.fhd_b, R.drawable.fhd_black, R.drawable.fhd_w};

    private WebSocketClient mWebSocketClient;
    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://192.168.10.1:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                //TODO: hide 'reconnect' button
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("WebSocket", "Received: " + message);
                runOnUiThread(new Runnable() {
                    public void run() {
                        myViewFlipper.setDisplayedChild(Integer.parseInt(message));
                    }});
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                //TODO: display 'reconnect' button
                runOnUiThread(new Runnable() {
                    public void run() {
                        myViewFlipper.setDisplayedChild(0);
                    }});
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                runOnUiThread(new Runnable() {
                    public void run() {
                        myViewFlipper.setDisplayedChild(0);
                    }});
                mWebSocketClient.close();
            }
        };
        mWebSocketClient.connect();
    }
    public void onButtonclick(View v){
        Button button=(Button) v;
        connectWebSocket();
    };


}


