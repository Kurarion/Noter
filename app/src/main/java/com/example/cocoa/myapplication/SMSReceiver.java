package com.example.cocoa.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.cocoa.myapplication.FilterPackage.FilterService;

import java.text.SimpleDateFormat;
import java.util.Date;
//短信接收器，对短信广播的监听，创建含信息的Intent并唤起服务进行处理
public class SMSReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //测试广播
        //String dd=new String("一条短信get");

        //Toast.makeText(context,dd,Toast.LENGTH_SHORT).show();
        //解析短信内容
        String msgTxt="";
        Date date;
        String receiveTime="";
        String senderNumber="";
        SmsMessage msg = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg = SmsMessage.createFromPdu((byte[]) p);
                //短信正文
                msgTxt += msg.getMessageBody();
                //Log.d("txt:",msgTxt);
                date = new Date(msg.getTimestampMillis());
                //yyyy-MM-dd HH:mm:ss
                SimpleDateFormat format = new SimpleDateFormat("MM月dd日-HH时mm分");
                //短信接收时间
                receiveTime = format.format(date);
                //短信发送方
                senderNumber = msg.getOriginatingAddress();
                //Intent intent_msg = new Intent(context, FilterService.class);
                //测试
                //Toast.makeText(context, receiveTime+": "+senderNumber+": "+msgTxt, Toast.LENGTH_LONG).show();

            }
            Intent startIntent = new Intent(context,FilterService.class);
            startIntent.putExtra("who",senderNumber);
            startIntent.putExtra("date",receiveTime);
            startIntent.putExtra("txt",msgTxt);
            context.startService(startIntent);
            //Log.d("txt:",msgTxt);
        }

        /*
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg= SmsMessage.createFromPdu((byte[]) p);

                String msgTxt =msg.getMessageBody();//得到消息的内容

                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);

                String senderNumber = msg.getOriginatingAddress();

                if (msgTxt.equals("Testing!")) {
                    Toast.makeText(context, "success!", Toast.LENGTH_LONG)
                            .show();
                    System.out.println("success!");
                    return;
                } else {
                    Toast.makeText(context, msgTxt, Toast.LENGTH_LONG).show();
                    System.out.println("发送人："+senderNumber+"  短信内容："+msgTxt+"接受时间："+receiveTime);
                    return;
                }
            }
            return;*/
       // }
    }
}
