package com.example.cocoa.myapplication.NotePackage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.cocoa.myapplication.R;

//通知栏的通知类，希望进一步增加文本输入框布局，目前以下为测试内容
public class NoteActivity extends AppCompatActivity implements View.OnClickListener{
    //文件读取和记录
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    //便签的标题和内容
    String Title="~~便签~~";
    String Txt="点击我创建便签";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //取消按钮
        Button cancel =(Button)findViewById(R.id.cancel);

        //确认按钮
        Button accept =(Button)findViewById(R.id.accept);

        //pref的初始化[注意：这里使用本Activity的上下文防止内存泄漏]
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        //查看是否更改过便签
        boolean isSet=pref.getBoolean("isSet",false);
        if(isSet){
            Title=pref.getString("title","~~便签~~");
            Txt=pref.getString("txt","点击我创建便签");
            //标题
            EditText title=(EditText)findViewById(R.id.title) ;
            //内容
            EditText txt=(EditText)findViewById(R.id.txt) ;
            //恢复标题
            title.setText(Title.toCharArray(),0,Title.length());
            //恢复内容
            txt.setText(Txt.toCharArray(),0,Txt.length());
        }

        cancel.setOnClickListener(this);
        accept.setOnClickListener(this);

        //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //manager.cancel(1);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                editor=pref.edit();

                //标题
                EditText title=(EditText)findViewById(R.id.title) ;
                //内容
                EditText txt=(EditText)findViewById(R.id.txt) ;
                //判断是否空内容
                if(!title.getText().toString().equals("")||!txt.getText().toString().equals("")) {
                    Title = title.getText().toString();
                    Txt = txt.getText().toString();
                    editor.putBoolean("isSet",true);
                    editor.putString("title",Title);
                    editor.putString("txt",Txt);
                }
                else{
                    Title ="~~便签~~";
                    Txt = "点击我创建便签";
                    editor.putBoolean("isSet",false);
                    editor.putString("title","");
                    editor.putString("txt","");
                }

                editor.apply();
                Intent intent = new Intent(this, NoteActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(Title)
                        .setContentText(Txt)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pi)
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                        //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.big_image)))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //设置为用户无法手动关闭
                        .setOngoing(true)
                        .build();
                manager.notify(1, notification);

                NoteActivity.this.finish();
                break;
            case R.id.cancel:
                //清除通知
                NotificationManager cancel_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                cancel_manager.cancel(1);
                NoteActivity.this.finish();
                break;
            default:

                break;

        }
        }
}