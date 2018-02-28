package com.example.cocoa.myapplication.FilterPackage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cocoa.myapplication.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//有问题！！！
//Filter过滤的服务，由广播接收器接收到msg广播启动来处理短信内容的过滤和通知的显示
public class FilterService extends Service {

    //过滤选项列表
    private List<Filter> filterList = new ArrayList<Filter>();


    public FilterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //return  null;
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(getApplicationContext(),"onCreate",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(),"onDestroy",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int StartId) {

        //从数据库中导入
        filterList = DataSupport.findAll(Filter.class);
        //Toast.makeText(getApplicationContext(),"onStartCommand",Toast.LENGTH_SHORT).show();
        //测试短信内容的正确传递

        String txt = intent.getStringExtra("txt");
        String date = intent.getStringExtra("date");
        String who = intent.getStringExtra("who");
        // Toast.makeText(getApplicationContext(), date+": "+who+": "+txt, Toast.LENGTH_LONG).show();

        //进行内容处理
        int indexBegin = txt.indexOf("【");

        int indexEnd = txt.indexOf("】");
        String filter_who;

        String msg_who = "未知";
        if (indexBegin != -1 && indexEnd != -1)
            msg_who = txt.substring(indexBegin + 1, indexEnd);
        //测试txt是否全
        //Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
        //Log.d("txt:",txt);
        for (Filter filter : filterList) {
            filter_who = filter.getWho();

            //如果不是任意匹配设定
            if (!filter_who.equals("*")) {
                if (filter_who.equals(msg_who)) {
                    //进行关键字匹配
                    mateKey(filter, filter_who, date, txt, indexEnd);
                } else {
                    // Toast.makeText(getApplicationContext(), "没有匹配", Toast.LENGTH_LONG).show();
                }
                // Toast.makeText(getApplicationContext(), filter_who+"   "+filter_key_1+"  "+filter_key_2, Toast.LENGTH_LONG).show();
            } else {
                mateKey(filter, msg_who, date, txt, indexEnd);
            }
        }

        //进行短信内容的正则表达匹配

        //生成通知

        stopSelf();
        return super.onStartCommand(intent, flags, StartId);


    }

    private void mateKey(Filter filter, String filter_who, String date, String txt, int indexEnd) {

        //准备参数
        String filter_key_1= filter.getKey_1();
        String filter_key_2= filter.getKey_2();
        //正则表达
        String key_1_regex="";
        String key_2_regex="";
        Matcher match_1=null;
        Matcher match_2=null;
        Pattern key_1_pt=null;
        Pattern key_2_pt=null;
        int msg_key_1=-1;
        int msg_key_2=-1;
       // Log.d("txt:",txt);
        //判断是否正则表达
        if(!filter_key_1.equals("")&&filter_key_1.charAt(0)=='~') {

            //标记为正则表达模式
            msg_key_1 = -2;
            //存储正则表达
            key_1_regex = filter_key_1.substring(1);
            //生成Pattern对象
            key_1_pt = Pattern.compile(key_1_regex);
        }
        else{
            //为了方便下面确定生成通知判断
            if(!filter_key_1.equals(""))
            msg_key_1 = txt.indexOf(filter_key_1, indexEnd);
        }

        if(!filter_key_2.equals("")&&filter_key_2.charAt(0)=='~') {
            //标记为正则表达模式
            msg_key_2 = -2;
            //存储正则表达
            key_2_regex = filter_key_2.substring(1);
            //生成Pattern对象
            key_2_pt = Pattern.compile(key_2_regex);
        }
        else{
            //为了方便下面确定生成通知判断
            if(!filter_key_2.equals(""))
            msg_key_2 = txt.indexOf(filter_key_2, indexEnd);
        }

        //Log.d("key_regex_1:",key_1_regex);&&(txt.substring(indexEnd + 1).matches(key_1_regex)||txt.substring(indexEnd + 1).matches(key_2_regex))
        //其中一个关键字匹配
        //if (msg_key_1 != -1 || msg_key_2 != -1) {
            //Log.d("key_1_content:","ok_1");
            String pre_content = txt.substring(indexEnd + 1);
            //Log.d("txt:",txt);
        //Toast.makeText(getApplicationContext(), pre_content, Toast.LENGTH_LONG).show();
        //判断原理：下标不为-1(字符串为空或者没有找到Key) -2（正则匹配） 与 （正则匹配确定，判断是否能匹配到） 的或运算
            if (((msg_key_1 > -1 ||
                    msg_key_2 > -1)||
                    ((msg_key_1 == -2?key_1_pt.matcher(pre_content).find():false)||
                            (msg_key_2 == -2?key_2_pt.matcher(pre_content).find():false)))) {
                //Log.d("key_1_content:","ok_2");
                //Title
                String title;
                if (msg_key_1 == -2 || msg_key_2 == -2)
                    title = "来自: " + filter_who + "(正则)      日期: " + date;
                else
                    title = "来自: " + filter_who + "            日期: " + date;

                //生成mark后的内容
                String content = pre_content;
                //Log.d("txt:",pre_content);
                String final_content = "";
                //判断是否正则
                if (msg_key_1 == -2) {
                    //赋值Matcher对象
                    match_1 = key_1_pt.matcher(pre_content);
                    //进行查找，逐个匹配项进行标记
                    //Log.d("key_1_enter:","ddddddd");
                    while (match_1.find()) {
                        content = mark(match_1.group()+"", 0, "", -1, content);
                        Log.d("key_1:",match_1.group());
                        //Log.d("key_regex_1:",key_1_regex);

                    }
                }
                //Log.d("key_1_content:",content);
                if (msg_key_2 == -2) {
                    //赋值Matcher对象
                    match_2 = key_2_pt.matcher(pre_content);
                    //进行查找，逐个匹配项进行标记
                    //Log.d("key_2_enter:","ddddddd");
                    while (match_2.find()) {
                        content = mark("", -1, match_2.group()+"", 0, content);
                    }
                }

                //最终输出
                final_content += mark(msg_key_1 == -2?"":filter_key_1, msg_key_1 == -2 ? -1 : msg_key_1, msg_key_2 == -2?"":filter_key_2, msg_key_2 == -2 ? -1 : msg_key_2, content);
                //Toast.makeText(getApplicationContext(), final_content, Toast.LENGTH_LONG).show();
                //Filter的ID
                int filter_id = (int) filter.getId();
                //创建一条通知
                Intent intent_note = new Intent(this, Notification_Message_Activity.class);
                //Intent intent_note = new Intent();
                //传递适配Filter的ID和注释 标题 内容
                intent_note.putExtra("id", filter_id);
                intent_note.putExtra("note",filter.getAnnotation());
                intent_note.putExtra("from",filter_who);
                intent_note.putExtra("context",final_content);
                //Log.d("txt:",final_content);
                //intent_note.putExtra("context",(byte)final_content);
                //关键点：使用PendingIntent传递
                // intent_note.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pi = PendingIntent.getActivity(this, filter_id, intent_note, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText("内容: "+final_content)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pi)
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //设置为用户无法手动关闭
                        //.setOngoing(true)
                        .build();
                manager.notify(filter_id, notification);

            }

    }
    private String mark(String key_1,int msg_key_1,String key_2,int msg_key_2,String pre)
    {
        String pre_content="";
        String content=pre;

        String []x1;
        String []x2;
        String k1="("+key_1+")";
        String k2="<"+key_2+">";
        //测试
       // Log.d("k1:",k1);
        //两个关键字都匹配
        if(!key_2.equals("")||!key_1.equals("")) {

            if (key_1.equals("")?false:msg_key_1 != -1 && key_2.equals("")?false:msg_key_2 != -1) {
                //进行重构输出结果
                content="";
                //测试
                //Log.d("k1:","1+2");
                //对第一个关键字分割
                x1 = pre.split(key_1);
                //重新拼合内容
                //判断开头是否有关键字 经测试不用为开头判断
                //if(pre.indexOf(key_1)==0)
                   // pre_content+=k1;
                for (int i = 0; i < x1.length - 1; ++i) {
                    pre_content += x1[i] + k1;
                }
                //Toast.makeText(getApplicationContext(), x1.length+"   "+k1, Toast.LENGTH_LONG).show();
                //拼合最后一个
                if(x1.length>=1)
                pre_content += x1[x1.length - 1];
                //判断结尾是否有关键字
                if(pre.lastIndexOf(key_1)==(pre.length()-key_1.length()))
                    pre_content+=k1;

                //对第二个关键字分割
                x2 = pre_content.split(key_2);
                //重新拼合内容
                //判断开头是否有关键字 经测试不用为开头判断
               // if(pre_content.indexOf(key_2)==0)
                 //   content+=k2;
                for (int i = 0; i < x2.length - 1; ++i) {
                    content += x2[i] + k2;
                }
                //拼合最后一个
                if(x2.length>=1)
                content += x2[x2.length - 1];
                //判断结尾是否有关键字
                if(pre_content.lastIndexOf(key_2)==(pre_content.length()-key_2.length()))
                    content+=k2;

            } else {
                if (key_1.equals("")?false:msg_key_1 != -1) {
                    //进行重构输出结果
                    content="";
                    //测试
                    //Log.d("k1:","1");
                    //对第一个关键字分割
                    x1 = pre.split(key_1);
                    //重新拼合内容
                    for (int i = 0; i < x1.length - 1; ++i) {
                        content += x1[i] + k1;
                    }
                    //拼合最后一个
                    if(x1.length>=1)
                    content += x1[x1.length - 1];
                    //判断结尾是否有关键字
                    if(pre.lastIndexOf(key_1)==(pre.length()-key_1.length()))
                        content+=k1;
                } else {
                    if (key_2.equals("")?false:msg_key_2 != -1) {
                        //进行重构输出结果
                        content="";
                        //测试
                        //Log.d("k1:","2");
                        //对第二个关键字分割
                        x1 = pre.split(key_2);
                        //重新拼合内容
                        for (int i = 0; i < x1.length - 1; ++i) {
                            content += x1[i] + k2;
                        }
                        //拼合最后一个
                        if(x1.length>=1)
                        content += x1[x1.length - 1];
                        //判断结尾是否有关键字
                        if(pre.lastIndexOf(key_2)==(pre.length()-key_2.length()))
                            content+=k2;
                    }
                }

            }
        }
        return content;

    }
}
