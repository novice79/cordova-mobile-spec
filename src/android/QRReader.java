/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package freego.david;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.util.Base64;

/**
* This class exposes methods in Cordova that can be called from JavaScript.
*/
public class QRReader extends CordovaPlugin {
    protected static final String TAG = "qr_plugin";
    private CordovaWebView mWebView;
    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    private CallbackContext mCallbackContext;
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mWebView = webView;
        this.mCallbackContext = null;
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                Log.i(TAG, "scanResult = "+scanResult);
                if (mCallbackContext != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, scanResult);
                    result.setKeepCallback(true);
                    mCallbackContext.sendPluginResult(result);
                }
  
            }
        };
		//广播过滤
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
        //在用户自行获取数据时，将广播的优先级调到最高 1000，***此处必须***
		mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mWebView.getContext().registerReceiver(mReceiver, mFilter);

    }
    public void onPause(boolean multitasking) {
        //注销获取扫描结果的广播
        mWebView.getContext().unregisterReceiver(mReceiver);
        super.onPause(multitasking);
    }
 
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        //注册广播来获取扫描结果
        mWebView.getContext().registerReceiver(mReceiver, mFilter);
    }
    public void onDestroy() {
        mReceiver = null;
        mFilter = null;
        super.onDestroy();
    }

     /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback context from which we were invoked.
     */
    @SuppressLint("NewApi")
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            this.mCallbackContext = callbackContext;
            String a1 = args.getString(0);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "from java jw" ));
        } else if(action.equals("echoAsync")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, args.optString(0)));
                }
            });
        } else {
            return false;
        }
        return true;
    }
}
