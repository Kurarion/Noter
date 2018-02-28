package com.example.cocoa.myapplication.FilterPackage;
//暂时不使用
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocoa.myapplication.R;

/**
 * Created by Cocoa on 2017/12/20.
 */

public class Notification_Message_Activity  extends AppCompatActivity implements View.OnClickListener {

    private Intent pass_intent;
    private int filter_id;
    private String filter_note;
    private String from;
    private String context;

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        //setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_context);
        //取消按钮
        //Button cancel =(Button)findViewById(R.id.msg_cancel);

        //确认按钮
        Button accept =(Button)findViewById(R.id.msg_accept);
        //from
        TextView text_from=(TextView)findViewById(R.id.msg_from);

        //context
        TextView text_context=(TextView)findViewById(R.id.msg_context);


        //准备好Filter的ID
        pass_intent=this.getIntent();
        filter_id= pass_intent.getIntExtra("id",2);
        filter_note=pass_intent.getStringExtra("note");
        from=pass_intent.getStringExtra("from");
        context=pass_intent.getStringExtra("context");
        //Toast.makeText(getApplicationContext(), context, Toast.LENGTH_LONG).show();
        //update "from" and "context"
        text_from.setText(from);
        text_context.setText(context);
        //测试Intent接收ID
        Toast.makeText(getApplicationContext(), "ID为"+filter_id+"的过滤条目\n注释："+filter_note, Toast.LENGTH_LONG).show();
        //cancel.setOnClickListener(this);
        accept.setOnClickListener(this);


        //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //manager.cancel(1);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.msg_accept:
                //删除通知
                NotificationManager cancel_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                //cancel_manager.cancel(filter_id);
                Notification_Message_Activity.this.finish();
                break;
            //case R.id.msg_cancel:
                //什么也不干
                //Toast.makeText(getApplicationContext(), this.getIntent().getIntExtra("id",0)+"", Toast.LENGTH_LONG).show();

               // Notification_Message_Activity.this.finish();
               // break;
            default:

                break;

        }
    }
}
