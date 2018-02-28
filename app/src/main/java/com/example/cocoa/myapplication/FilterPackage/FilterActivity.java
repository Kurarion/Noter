package com.example.cocoa.myapplication.FilterPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocoa.myapplication.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cocoa on 2017/12/17.
 */
//此类用作过滤设置的主活动
public class FilterActivity extends AppCompatActivity {
    //过滤选项列表
    private List<Filter> filterList = new ArrayList<Filter>();
    //文件读取和记录 用于对程序默认的初始过滤使用
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    //重写菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    //重写菜单功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.add_item:
                //Toast.makeText(this,"add!!!!!!!!",Toast.LENGTH_SHORT).show();
                final EditText et = new EditText(this);
                //Hint
                et.setHint("使用空格分隔 来源 关键字一 关键字二 注释\n如（中国联通 您 欠费 提示欠费）");
                //内容居中显示
                et.setGravity(Gravity.CENTER);
                new AlertDialog.Builder(this).setTitle("创建过滤条目")
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getApplicationContext(), "创建内容不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    //对输入进行处理
                                    String []in=input.split(" ");
                                    //用作最终输出
                                    if(in.length<=3) {
                                        String[] In = new String[3];
                                        for (int i = 0; i < in.length; ++i)
                                            In[i] = in[i];
                                        for (int i = in.length + 1; i <= 3; ++i)
                                            In[i - 1] = "";
                                        //进行过滤的创建
                                        Filter f4 = new Filter();
                                        f4.setWho(In[0]);
                                        f4.setKey_1(In[1]);
                                        f4.setKey_2(In[2]);
                                        f4.save();
                                        //重新加载数据库 读取数据
                                        initData();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"输入的参数过多，添加失败！",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                break;
            case R.id.delet_all_item:
                //删除全部规则
                final EditText et1 = new EditText(this);
                //Hint
                et1.setHint("安全码详见帮助");
                //内容居中显示
                et1.setGravity(Gravity.CENTER);
                new AlertDialog.Builder(this).setTitle("请输入安全码")
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et1.getText().toString();
                                if (input.equals("8888")) {
                                        //删除表中所有数据
                                        DataSupport.deleteAll(Filter.class);
                                        //重新加载数据库 读取数据
                                        initData();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"安全码错误！",Toast.LENGTH_SHORT).show();
                                    }

                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.help_item:
                //这里写使用说明
                final TextView textview=new TextView(this);
                //内容居中显示 字体等设置
                textview.setGravity(Gravity.CENTER);
                textview.setTextColor(Color.rgb(57, 153, 0));
                textview.setTextSize(16.0f);
                String s="\n"+
                        "1-菜单栏中可以通过“添加”选项创建自己需要的短信通知生成条目\n\n" +
                        "2-创建的格式为“来源 关键字一 关键字二 注释”【一个空格为一个分隔，两个空格则中间参数设为空】 注：来源即短信中使用【】框起来的发送方，如【中国联通】\n\n" +
                        "3-创建中来源不能为空，但可以使用\"*\"来匹配任意短信来源\n\n"+
                        "4-创建的条目中两个关键字【第一个关键字使用()标识，第二个关键字使用<>标识】【前者优先级大】可以使用正则表达式格式为\"~正则表达式\"，如\"~[1-9]*\"\n\n"+
                        "5-单击列表中的一个条目即可编辑该条目，也可以直接删除此条目\n\n"+
                        "6-菜单栏中“删除全部”选项可以删除当前全部条目【安全码：8888】\n";
                textview.setText(s.toCharArray(),0,s.length());
                new AlertDialog.Builder(this).setTitle("帮助")
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(textview)
                        .setPositiveButton("我知道了", null)
                        .show();

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInit=pref.getBoolean("isInit",false);
        if(!isInit) {
            //初始化Filter
            initFilter();
       }
       //加载数据库 读取数据
       initData();



    }
    @Override
    protected void onResume() {
        initData();
        //Toast.makeText(getApplicationContext(),"OnResume",Toast.LENGTH_SHORT).show();
        super.onResume();
    }
    //多次调用封装为方法
    public void initData(){
        //从数据库中导入
        filterList=DataSupport.findAll(Filter.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_filter);
        //使用线性的布局
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //StaggeredGridLayoutManager layoutManager = new
          //  StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        FilterAdapter adapter = new FilterAdapter(filterList,this);
        recyclerView.setAdapter(adapter);
    }
    private void initFilter()
    {
        //此处打算使用数据库读取过滤信息
        //这些为软件自带的过滤项
        //测试数据
        Filter f1=new Filter(1,"中国联通", "尊敬的", "余额","中国联通提醒您交费");
        f1.save();
        Filter f2=new Filter(2,"*", "验证码", "","验证码短信匹配");
        f2.save();
        Filter f3=new Filter(3,"*", "~[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+", "","Email匹配");
        f3.save();

        Filter f4=new Filter();
        f4.setId(4);
        f4.setWho("*");
        f4.setKey_1("~[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}");
        f4.setKey_2("");
        f4.setAnnotation("手机号码匹配");
        f4.save();

        /*
        for(int i=0;i<30;++i) {
            Filter f1 = new Filter("中国联通", "交费", "验证码");
            filterList.add(f1);
            Filter f2 = new Filter("中国联通", "交费", "验证码");
            filterList.add(f2);
            Filter f3 = new Filter("中国联通", "交费", "验证码");
            filterList.add(f3);
        }
        */
        //标志已经初始化了
        editor=pref.edit();
        editor.putBoolean("isInit",true);
        editor.apply();
    }

}
