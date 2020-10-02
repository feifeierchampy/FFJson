package com.feifeier.ffjson_processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class Utils {

  public static String toLowerCaseFirstChar(String text) {
    if (text == null || text.length() == 0) {
      return "";
    }
    if (Character.isLowerCase(text.charAt(0))) {
      return text;
    }
    return String.valueOf(Character.toLowerCase(text.charAt(0))) + text.substring(1);
  }

  public static String toUpperCaseFirstChar(String text) {
    if (Character.isUpperCase(text.charAt(0))) {
      return text;
    } else {
      return String.valueOf(Character.toUpperCase(text.charAt(0))) + text.substring(1);
    }
  }

  public static String getPackageName(Elements elementUtils, TypeElement typeElement) {
    return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
  }

  public static String getSimpleClassName(Element element) {
    String[] strings = element.asType().toString().split("\\.");
    return strings[strings.length - 1];
  }

  public static String getFullClassName(Element element) {
    return element.asType().toString();
  }

  public static String getGenericSimpleClassName(Element element) {
    String[] strings = element.asType().toString().split("\\.");
    String lastStr = strings[strings.length - 1];
    return lastStr.substring(0, lastStr.length() - 1);
  }

  public static String getGenericFullClassName(Element element) {
    String className = element.asType().toString();
    String genericClassName = "";

    String regex = "\\<(.*?)>";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(className);
    while (matcher.find()) {
      genericClassName = matcher.group(1);
      break;
    }
    return genericClassName;
  }

  public static boolean isGenericBasicClass(Element element) {
    String className = element.asType().toString();
    String genericClassName = "";

    String regex = "\\<(.*?)>";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(className);
    while (matcher.find()) {
      genericClassName = matcher.group(1);
      break;
    }
    return isBasicClass(genericClassName);
  }

  public static boolean isGenericLongClass(Element element) {
    String className = element.asType().toString();
    String genericClassName = "";

    String regex = "\\<(.*?)>";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(className);
    while (matcher.find()) {
      genericClassName = matcher.group(1);
      break;
    }
    return "java.lang.Long".equals(genericClassName);
  }

  public static boolean isBasicClass(String fullClassName) {
    return "java.lang.String".equals(fullClassName)
        || "java.lang.Integer".equals(fullClassName)
        || "java.lang.Long".equals(fullClassName)
        || "java.lang.Double".equals(fullClassName)
        || "java.lang.Float".equals(fullClassName)
        || "java.lang.Boolean".equals(fullClassName)
        || "int".equals(fullClassName)
        || "long".equals(fullClassName)
        || "double".equals(fullClassName)
        || "float".equals(fullClassName)
        || "boolean".equals(fullClassName);
  }
}
