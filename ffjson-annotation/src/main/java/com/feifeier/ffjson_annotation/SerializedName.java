package com.feifeier.ffjson_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：2019/2/8
 **************************************************************************************************/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface SerializedName {
  String value();
}
