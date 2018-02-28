package com.example.cocoa.myapplication.FilterPackage;

/**
 * Created by Cocoa on 2017/12/17.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocoa.myapplication.R;

import org.litepal.crud.DataSupport;

import java.util.List;
//此类用作类Filter的适配器
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder>{

    private List<Filter> mFilterList;
    //activity上下文
    private FilterActivity context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View filterView;
        TextView filterId;
        TextView filterWho;
        TextView filterKey_1;
        TextView filterKey_2;
        TextView filterNote;
        public ViewHolder(View view) {
            super(view);
            filterView = view;
            filterId=(TextView)view.findViewById(R.id.filter_id);
            filterWho = (TextView) view.findViewById(R.id.filter_who);
            filterKey_1 = (TextView) view.findViewById(R.id.filter_key_1);
            filterKey_2 = (TextView) view.findViewById(R.id.filter_key_2);
            filterNote=(TextView) view.findViewById(R.id.filter_annotation);
        }
    }

    public FilterAdapter(List<Filter> filterList,FilterActivity context) {
        mFilterList = filterList;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到所选过滤项目对象
                int position = holder.getAdapterPosition();
                final Filter filter = mFilterList.get(position);
                String old=filter.getWho()+" "+filter.getKey_1()+" "+filter.getKey_2()+" "+filter.getAnnotation();
                //通过Dialog编辑
                final EditText et = new EditText(context);
                //传入之前的数据
                et.setText(old.toCharArray(),0,old.length());
                //内容居中显示
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                //呼出对话框
                new AlertDialog.Builder(context).setTitle("编辑此条目")
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();

                                if (input.equals("")) {
                                    Toast.makeText(context, "编辑内容不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    //对输入进行处理
                                    String []in=input.split(" ");
                                    //用作最终输出
                                    if(in.length<=4) {
                                        String[] In = new String[4];
                                        for (int i = 0; i < in.length; ++i)
                                            In[i] = in[i];
                                        for (int i = in.length + 1; i <= 4; ++i)
                                            In[i - 1] = "";
                                        //进行过滤的编辑更新
                                        filter.setWho(In[0]);
                                        filter.setKey_1(In[1]);
                                        filter.setKey_2(In[2]);
                                        filter.setAnnotation(In[3]);
                                        filter.save();
                                        //进行刷新activity的数据
                                        context.initData();

                                    }
                                    else{
                                        Toast.makeText(context,"输入的参数过多，编辑失败！",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {{
                                DataSupport.delete(Filter.class,filter.getId());
                                //进行刷新activity的数据
                                context.initData();
                            }
                        }})
                        .show();


                //
                //int position = holder.getAdapterPosition();
                //Filter filter = mFilterList.get(position);
                //Toast.makeText(v.getContext(), "you clicked view " + filter.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        //测试点击事件

        holder.filterNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Filter filter = mFilterList.get(position);
                Toast.makeText(v.getContext(), filter.getAnnotation(), Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Filter filter = mFilterList.get(position);
        holder.filterId.setText(filter.getId()+"");
        holder.filterWho.setText(filter.getWho());
        holder.filterKey_1.setText(filter.getKey_1());
        holder.filterKey_2.setText(filter.getKey_2());
        holder.filterNote.setText("[查看]");
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

}
