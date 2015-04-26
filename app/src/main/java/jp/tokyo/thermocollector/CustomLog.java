package jp.tokyo.thermocollector;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.treasuredata.android.TreasureData;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CustomLog {
    static final String API_KEY = ""; //自身のTreasureDataアカウントで発行する
    static final String DB_NAME = "collector"; //大文字使えない（TreasureDataの制限）
    static final String TABLE_NAME = "thermodo"; //大文字使えない（TreasureDataの制限）
    static final String TAG="ThermoService";

    static public void log(Context context, Map<String, Object> referrer) {
        TreasureData.enableLogging();
        final TreasureData td = new TreasureData(context, API_KEY);

        Log.i(TAG, "write TD.");
        td.addEvent(DB_NAME, TABLE_NAME, referrer);
        td.uploadEvents();
    }
}
