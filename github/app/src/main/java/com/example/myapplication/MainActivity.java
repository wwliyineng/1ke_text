package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/*https://www.cnblogs.com/zhujiabin/p/6252903.html*/
public class MainActivity extends AppCompatActivity {

    private EditText sockte_et;
    private TextView obtain_tv, connect_tv;
    private Button obtain_bu, connect_bu, send_bu;
    Socket socket;
    Handler handler = null;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;
    String response;
    private boolean bool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sockte_et = findViewById(R.id.sockte_et);
        obtain_tv = findViewById(R.id.obtain_tv);
        connect_tv = findViewById(R.id.connect_tv);
        obtain_bu = findViewById(R.id.obtain_bu);
        connect_bu = findViewById(R.id.connect_bu);
        send_bu = findViewById(R.id.send_bu);
        //创建新的handler不能这样创建，会内存泄漏，我是测试使用的，这是警告
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        obtain_tv.setText(response);
                        break;
                    case 1:
                        connect_tv.setText(bool + "");//显示true就是连接成功
                        break;
                }
            }
        };
        //判断是否和服务器连接成功
        connect_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            socket = new Socket("192.168.1.54", 1234);
                            bool = socket.isConnected();
                            Message message = new Message();
                            message.what = 1;
                            message.obj = bool;
                            handler.sendMessage(message);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });


        //接受服务器的数据
        obtain_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            is = socket.getInputStream();
                            isr = new InputStreamReader(is);
                            br = new BufferedReader(isr);
                            response = br.readLine();
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        //发数据给服务器
        send_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String msg = sockte_et.getText().toString();
                    if (msg.equals("")) {
                        Toast.makeText(MainActivity.this, "能能，程序员请输入", Toast.LENGTH_LONG).show();
                    }
                    if (bool == false) {
                        Toast.makeText(MainActivity.this, "能能，程序员请连接", Toast.LENGTH_LONG).show();

                    }
                    if (bool) {
                        if (msg.equals("")) {
                            return;
                        } else {
                            //发送过服务器的数据
                            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                            writer.writeUTF(msg);
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
