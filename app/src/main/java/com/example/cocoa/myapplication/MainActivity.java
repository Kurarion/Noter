package com.example.cocoa.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocoa.myapplication.FilterPackage.FilterActivity;
import com.example.cocoa.myapplication.NotePackage.NoteActivity;
import com.kyleduo.switchbutton.SwitchButton;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    //文件读取和记录 用于对广播滑动按钮状态判定
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    //测试滑动按钮开关
    private SwitchButton mSwitchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏默认标题栏
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置说明
        TextView help=(TextView)findViewById(R.id.main_help);

        String txt_helo="       开启便签将生成一条不能被滑动取消的通知，单击它将弹出一个对话框，对话框中可以输入想要创建通知的标题以及内容，你也可以随时修改这条通知\n"+
        "       短信监控滑动开启则会开始监听短信，如果某条短信符合您的要求则会在通知栏上创建这条短信的内容（关键字部分会被标记），同便签一样不能滑动关闭\n"+
        "       过滤设置则会进入一个新的页面，在那里可以定义您自己需要的过滤选项，您也可以在新的页面中的菜单按钮里的帮助得到使用说明\n";
        help.setText(txt_helo);
        //开启便签功能  <v1.0> 布局仍有问题
        Button sendNotice = (Button) findViewById(R.id.start_note);
        //文件读取初始化
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //为滑块按钮初始化
        mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
        //默认初次使用短信监听为关闭状态
        boolean firstStart = pref.getBoolean("firstStart", true);
        if (firstStart){
        unRegister();
            //标记非首次启动
            editor=pref.edit();
            editor.putBoolean("firstStart",false);
            editor.apply();
        }

        //判断上次启动是否开启了静态注册短信监听功能
        boolean isRegister=pref.getBoolean("isRegister",false);
        if(isRegister){
            mSwitchButton.setChecked(true);
        }
        else{
            mSwitchButton.setChecked(false);
        }

        //测试过滤
        Button filter =(Button) findViewById(R.id.filter);
        //测试便签
        //Button note =(Button) findViewById(R.id.note);
        //测试对话框
        //Button dialog =(Button) findViewById(R.id.dialog);
        //记住滑块的选择

        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //监听滑动开关变化
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String s;
                editor=pref.edit();
                if (b) {
                    s = "短信监听开启";
                    register();
                    //存储到文件
                    editor.putBoolean("isRegister",true);
                }
                else {
                    s = "短信监听关闭";
                    unRegister();
                    //存储到文件
                    editor.putBoolean("isRegister",false);
                }

                editor.apply();

                Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT).show();
            }
        });
        //dialog.setOnClickListener(this);
        filter.setOnClickListener(this);
        //note.setOnClickListener(this);
        sendNotice.setOnClickListener(this);
        //检查是否具有短信权限（只需申请一个就能获得短信的全部操作权限）
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }else{

        }
    }
    //取消短信广播静态注册
    private void unRegister(){
        getPackageManager().setComponentEnabledSetting( new ComponentName("com.example.cocoa.myapplication", SMSReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    //重新静态注册短信广播
    private void register(){
        getPackageManager().setComponentEnabledSetting( new ComponentName("com.example.cocoa.myapplication", SMSReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);
        //第一次创建数据库
        Connector.getDatabase();
    }
    //开启便签功能
    private  void startNote(){
        Intent intent = new Intent(this, NoteActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("~~便签~~")
                .setContentText("点击我创建便签")
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
    }
    //关闭便签功能=>通过Notification_Note关闭

    //动态获得短信权限
    private void getPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_note:
                /*
                Intent intent = new Intent(this, NoteActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("~~便签~~")
                        .setContentText("点击我创建便签")
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

                manager.notify(1, notification);*/
           /*     Intent intent = new Intent();
//对应BroadcastReceiver中intentFilter的action
                intent.setAction(TEST_ACTION);
//发送广播
                sendBroadcast(intent);*/
                startNote();
                break;
            case R.id.filter:
                Intent intent_filter=new Intent(this,FilterActivity.class);
                startActivity(intent_filter);
                break;
         //   case R.id.note:
            //    Intent intent_note=new Intent(this,Note.class);
           //     startActivity(intent_note);
          //      break;
            /*
            case R.id.dialog:
                final EditText et = new EditText(this);

                new AlertDialog.Builder(this).setTitle("创建note")
                       // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getApplicationContext(), "创建内容不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    Notification notification = new NotificationCompat.Builder(getApplicationContext())
                                            .setContentTitle("Note!!!")
                                            .setContentText(input)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                          //  .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                            .setContentIntent(pi)
                                            //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                                            //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                                            //        .setLights(Color.GREEN, 1000, 1000)
                                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                                            //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.big_image)))
                                            .setPriority(NotificationCompat.PRIORITY_MAX)
                                            .build();
                                    manager.notify(2, notification);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
                */
            default:
                break;
        }
    }

}