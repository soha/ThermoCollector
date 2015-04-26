package jp.tokyo.thermocollector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.robocatapps.thermodosdk.Thermodo;
import com.robocatapps.thermodosdk.ThermodoFactory;
import com.robocatapps.thermodosdk.ThermodoListener;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class ThermoService extends Service implements ThermodoListener {
    public ThermoService() {
    }

    static final String TAG="ThermoService";
    private Thermodo mThermodo;
    private Calendar prevCal = Calendar.getInstance();
    private int TIME_INTERVAL = 600000; //ms

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        makeText(this, "ThermoService#onCreate", LENGTH_SHORT).show();

        mThermodo = ThermodoFactory.getThermodoInstance(this);
        mThermodo.setThermodoListener(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        Toast.makeText(this, "ThermoService#onStartCommand", Toast.LENGTH_SHORT).show();

        mThermodo.start();
        //明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "ThermoService#onDestroy", Toast.LENGTH_SHORT).show();

        mThermodo.stop();
    }


    //サービスに接続するためのBinder
    public class MyServiceLocalBinder extends Binder {
        //サービスの取得
        ThermoService getService() {
            return ThermoService.this;
        }
    }
    //Binderの生成
    private final IBinder mBinder = new MyServiceLocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "ThermoService#onBind" + ": " + intent, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onBind" + ": " + intent);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent){
        Toast.makeText(this, "ThermoService#onRebind" + ": " + intent, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onRebind" + ": " + intent);
    }

    @Override
    public boolean onUnbind(Intent intent){
        Toast.makeText(this, "ThermoService#onUnbind" + ": " + intent, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onUnbind" + ": " + intent);

        //onUnbindをreturn trueでoverrideすると次回バインド時にonRebildが呼ばれる
        return true;
    }



    //////////////////
    // for Thermodo //
    //////////////////
    @Override
    public void onStartedMeasuring() {
        Toast.makeText(this, "Started measuring", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Started measuring");
    }

    @Override
    public void onStoppedMeasuring() {
        Toast.makeText(this, "Stopped measuring", Toast.LENGTH_SHORT).show();
        //mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
        Log.i(TAG, "Stopped measuring");
    }

    /**
     * 温度が変わるとこれが呼ばれる
     * @param temperature 摂氏
     */
    @Override
    public void onTemperatureMeasured(float temperature) {
        //mTemperatureTextView.setText(Float.toString(temperature));

        Calendar nowCal = Calendar.getInstance();
        if((nowCal.getTimeInMillis() - prevCal.getTimeInMillis()) >= TIME_INTERVAL) {
            prevCal = nowCal;
            //１分超えてたら書き込む
            Log.i(TAG, "Got temparature: " + temperature);
            Map<String, Object> hash = new HashMap<String, Object>();

            hash.put("temparature", temperature);
            //TresureDataに書き込み
            CustomLog.log(this.getApplicationContext(), hash);
        }
    }

    @Override
    public void onErrorOccurred(int what) {
        Toast.makeText(this, "An error has occurred: " + what, Toast.LENGTH_SHORT).show();
        switch (what) {
            case Thermodo.ERROR_AUDIO_FOCUS_GAIN_FAILED:
                Log.e(TAG, "An error has occurred: Audio Focus Gain Failed");
                //mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
                break;
            case Thermodo.ERROR_AUDIO_RECORD_FAILURE:
                Log.e(TAG, "An error has occurred: Audio Record Failure");
                break;
            case Thermodo.ERROR_SET_MAX_VOLUME_FAILED:
                Log.w(TAG, "An error has occurred: The volume could not be set to maximum");
                break;
            default:
                Log.e(TAG, "An unidentified error has occurred: " + what);
        }
    }

}