package com.feifeier.jsonparser.bean;


import java.util.List;

import com.feifeier.ffjson_annotation.SerializedName;


/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：2020/7/4
 **************************************************************************************************/

public class TestBean {

  @SerializedName("name")
  public String mName;

  @SerializedName("year")
  public int year;

  @SerializedName("time")
  public long mTime;

  @SerializedName("percent")
  public double mPercent;

  @SerializedName("isTrue")
  public boolean mIsTrue;

  @SerializedName("mile")
  public float mile;

  @SerializedName("names")
  public List<String> mNameList;

  @SerializedName("years")
  public List<Integer> mYears;

  @SerializedName("times")
  public List<Long> mTimes;

  @SerializedName("bools")
  public List<Boolean> mBoolList;

  @SerializedName("person")
  public PersonBean mPerson;

  @SerializedName("classes")
  public List<ClassBean> mClassList;

  @SerializedName("persons")
  public List<PersonBean> mPersons;


  public static class PersonBean {

    @SerializedName("age")
    public int age;

    @SerializedName("name")
    public String name;

  }
}
