package jp.tokyo.thermocollector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static jp.tokyo.thermocollector.R.*;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        Button btn = (Button) findViewById(id.StartButton);
        btn.setOnClickListener(btnListener);//リスナの登録

        btn  = (Button) findViewById(id.StopButton);
        btn.setOnClickListener(btnListener);//リスナの登録

        btn = (Button) findViewById(id.BindButton);
        btn.setOnClickListener(btnListener);//リスナの登録

        btn = (Button) findViewById(id.UnbindButton);
        btn.setOnClickListener(btnListener);//
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch(v.getId()){

                case id.StartButton://startServiceでサービスを起動
                    startService(new Intent(MainActivity.this, ThermoService.class));
                    break;
                case id.StopButton://stopServiceでサービスの終了
                    stopService(new Intent(MainActivity.this, ThermoService.class));
                    break;

                case id.BindButton://doBindService
                    doBindService();
                    break;

                case id.UnbindButton://doUnbindService
                    doUnbindService();
                    break;

                default:
                    break;
            }
        }
    };


    //取得したServiceの保存
    private ThermoService mBoundService;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            // サービスとの接続確立時に呼び出される
            Toast.makeText(MainActivity.this, "Activity:onServiceConnected",
                    Toast.LENGTH_SHORT).show();

            // サービスにはIBinder経由で#getService()してダイレクトにアクセス可能
            mBoundService = ((ThermoService.MyServiceLocalBinder)service).getService();

            //必要であればmBoundServiceを使ってバインドしたサービスへの制御を行う
        }

        public void onServiceDisconnected(ComponentName className) {
            // サービスとの切断(異常系処理)
            // プロセスのクラッシュなど意図しないサービスの切断が発生した場合に呼ばれる。
            mBoundService = null;
            Toast.makeText(MainActivity.this, "Activity:onServiceDisconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {

        Toast.makeText(MainActivity.this, "Activity:doBindService",
                Toast.LENGTH_SHORT).show();

        //サービスとの接続を確立する。明示的にServiceを指定
        //(特定のサービスを指定する必要がある。他のアプリケーションから知ることができない = ローカルサービス)
        bindService(new Intent(MainActivity.this,
                ThermoService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {

            // サービスとの接続確立時に呼び出される
            Toast.makeText(MainActivity.this, "Activity:doUnbindService",
                    Toast.LENGTH_SHORT).show();

            // コネクションの解除
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
