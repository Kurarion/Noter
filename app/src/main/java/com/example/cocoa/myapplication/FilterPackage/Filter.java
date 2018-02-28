package com.example.cocoa.myapplication.FilterPackage;

import org.litepal.crud.DataSupport;

/**
 * Created by Cocoa on 2017/12/17.
 */
    //类Filter 过滤的关键信息
    //继承DataSupport对数据库数据进行操作
public class Filter extends DataSupport{
    //Litepal数据库自动生成自增的Id
    private long id;
    private String who;
    private String key_1;
    private String key_2;
    private String annotation;

    public Filter()
    {

    }

    public Filter(long id,String who ,String key_1,String key_2,String annotation)
    {
        this.id=id;
        this.who=who;
        this.key_1=key_1;
        this.key_2=key_2;
        this.annotation=annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWho() {
        return who;
    }

    public String getKey_1() {
        return key_1;
    }

    public String getKey_2() {
        return key_2;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setKey_1(String key_1) {
        this.key_1 = key_1;
    }

    public void setKey_2(String key_2) {
        this.key_2 = key_2;
    }

}
