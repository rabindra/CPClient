package com.aptude.aidl.poc.cpclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aptude.aidl.poc.cpserver.ICalService;

public class MainActivity extends AppCompatActivity {

    EditText mEditName, mEditVal1, mEditVal2;
    TextView mResultView;
    protected ICalService mCalService = null;
    private final static String INTENT_BIND_SERVICE = "com.aptude.aidl.poc.cpserver.multiplyService";
    private final static String SERVICE_PACKAGE = "com.aptude.aidl.poc.cpserver";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mEditName = (EditText) findViewById(R.id.name);
        mEditVal1 = (EditText) findViewById(R.id.num1);
        mEditVal2 = (EditText) findViewById(R.id.num2);

        mResultView = (TextView)findViewById(R.id.result);

        if(mCalService == null){

            //Using Intent with ServiceClass
            /*Intent it= new Intent()
                    .setComponent(new ComponentName(
                            "com.aptude.aidl.poc.cpserver",
                            "com.aptude.aidl.poc.cpserver.CalService"

                    ));
            */


            //Using Intent with Intent-filter
            Intent it = new Intent(INTENT_BIND_SERVICE);
            it.setPackage(SERVICE_PACKAGE);


            bindService(it, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void multiply(View v){

        switch(v.getId()){
            case R.id.multiply_btn:


                int num1 = Integer.parseInt(mEditVal1.getText().toString());
                int num2 = Integer.parseInt(mEditVal2.getText().toString());
                String name = mEditName.getText().toString();

                try{
                    int result = mCalService.getResult(num1, num2);
                    String msg = mCalService.getMessage(name);
                    mResultView.setText(msg+ result);
                    //Toast.makeText( this, num1+" - "+num2+" - "+name+" :: "+ result, Toast.LENGTH_SHORT).show();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCalService = ICalService.Stub.asInterface(service);
            Toast.makeText(getApplicationContext(),"Service Connected", Toast.LENGTH_SHORT);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCalService = null;
            Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT);
        }
    };
}
