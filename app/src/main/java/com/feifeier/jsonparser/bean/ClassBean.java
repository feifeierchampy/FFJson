package com.feifeier.jsonparser.bean;


import com.feifeier.json_annotation.SerializedName;

import java.util.ArrayList;

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
