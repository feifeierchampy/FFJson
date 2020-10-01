package com.feifeier.jsonparser;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.feifeier.jsonparser.bean.ClassBean;
import com.feifeier.jsonparser.bean.TestBean;
import com.feifeier.jsonparser.bean.TestBean_JsonUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void clickParseJson(View view) {
    TestBean bean = new TestBean();
    bean.mName = "test";
    bean.year = 2020;
    bean.mTime = 1234567890;
    bean.mPercent = 0.12345;
    bean.mIsTrue = true;
    bean.mile = 0.123f;

    bean.mNameList = new ArrayList<>();
    bean.mNameList.add("aaa");
    bean.mNameList.add("bbb");
    bean.mNameList.add("ccc");

    bean.mYears = new ArrayList<>();
    bean.mYears.add(1999);
    bean.mYears.add(2000);
    bean.mYears.add(2001);
    bean.mYears.add(2002);

    bean.mTimes = new ArrayList<>();
    bean.mTimes.add((long) 12345670);
    bean.mTimes.add((long) 12345671);
    bean.mTimes.add((long) 12345672);

    bean.mBoolList = new ArrayList<>();
    bean.mBoolList.add(true);
    bean.mBoolList.add(true);
    bean.mBoolList.add(false);

    bean.mPerson = new TestBean.PersonBean();
    bean.mPerson.age = 13;
    bean.mPerson.name = "haha";

    bean.mClassList = new ArrayList<>();
    ClassBean classBean1 = new ClassBean();
    classBean1.name = "class1";
    classBean1.mLevel = 1;

    classBean1.mPersons = new ArrayList<>();
    TestBean.PersonBean personBean1 = new TestBean.PersonBean();
    personBean1.name = "class1person1";
    personBean1.age = 111;

    TestBean.PersonBean personBean2 = new TestBean.PersonBean();
    personBean2.name = "class1person2";
    personBean2.age = 222;

    classBean1.mPersons.add(personBean1);
    classBean1.mPersons.add(personBean2);


    ClassBean classBean2 = new ClassBean();
    classBean2.name = "class1";
    classBean2.mLevel = 1;

    classBean2.mPersons = new ArrayList<>();
    TestBean.PersonBean personBean2_1 = new TestBean.PersonBean();
    personBean2_1.name = "class2person1";
    personBean2_1.age = 2111;

    TestBean.PersonBean personBean2_2 = new TestBean.PersonBean();
    personBean2_2.name = "class2person2";
    personBean2_2.age = 2222;

    classBean2.mPersons.add(personBean2_1);
    classBean2.mPersons.add(personBean2_2);


    bean.mClassList.add(classBean1);
    bean.mClassList.add(classBean2);

    bean.mPersons = new ArrayList<>();
    TestBean.PersonBean p1 = new TestBean.PersonBean();
    p1.age = 1;
    p1.name = "haha1";


    String jsonStr = TestBean_JsonUtils.toJson(bean);
    Log.d(TAG, "json: " + jsonStr);

    TestBean testBean = TestBean_JsonUtils.fromJson(jsonStr);
    Log.d(TAG, "clickJsonTest: " + testBean);
  }
}