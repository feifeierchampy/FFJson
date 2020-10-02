package com.feifeier.jsonparser.bean;


import java.util.ArrayList;

import com.feifeier.ffjson_annotation.SerializedName;


/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：2020/7/4
 **************************************************************************************************/

public class ClassBean {

  @SerializedName("name")
  public String name;

  @SerializedName("level")
  public int mLevel;

  @SerializedName("persons")
  public ArrayList<TestBean.PersonBean> mPersons;
}
