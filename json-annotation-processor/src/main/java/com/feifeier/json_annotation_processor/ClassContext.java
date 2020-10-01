package com.feifeier.json_annotation_processor;

import java.util.Objects;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：2020/6/28
 **************************************************************************************************/

public class ClassContext {
  public String packageName;
  public String className;

  @Override
  public boolean equals(Object o) {
    if (o instanceof ClassContext) {
      return ((ClassContext) o).packageName.equals(this.packageName)
          && ((ClassContext) o).className.equals(this.className);
    }
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageName, className);
  }
}
