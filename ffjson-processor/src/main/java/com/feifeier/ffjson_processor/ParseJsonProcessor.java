package com.feifeier.ffjson_processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.feifeier.ffjson_annotation.SerializedName;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：2020/6/27
 **************************************************************************************************/

@AutoService(Processor.class)
public class ParseJsonProcessor extends AbstractProcessor {
  private Messager mMessager;
  private Elements mElements;
  private Filer mFiler;
  private int mRound = 0;
  private Map<ClassContext, LinkedList<Element>> mElementMaps = new HashMap<>();
  private ClassName m_JSONObject;
  private ClassName m_JSONArray;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    mMessager = processingEnvironment.getMessager();
    mElements = processingEnvironment.getElementUtils();
    mFiler = processingEnvironment.getFiler();
    m_JSONObject = ClassName.get("org.json", "JSONObject");
    m_JSONArray = ClassName.get("org.json", "JSONArray");
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(SerializedName.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    mMessager.printMessage(Diagnostic.Kind.NOTE,
        "process " + mRound + " process over: " + roundEnvironment.processingOver());
    mRound++;
    mElementMaps.clear();
    for (Element element : roundEnvironment.getElementsAnnotatedWith(SerializedName.class)) {
      if (element.getKind() != ElementKind.FIELD) {
        onError("SerializedName annotation can only be applied to field", element);
        return false;
      }
      if (!(element instanceof VariableElement)) {
        onError("element is not VariableElement", element);
        return false;
      }
      if (!element.getModifiers().contains(Modifier.PUBLIC)) {
        onError("field must be public:", element);
        return true;
      }

      VariableElement variableElement = (VariableElement) element;
      TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
      // 类名
      String className = typeElement.getSimpleName().toString();

      ClassContext classContext = new ClassContext();
      classContext.packageName = Utils.getPackageName(mElements, typeElement);
      classContext.className = className;

      addElement(classContext, element);
    }
    generateCode();
    return true;
  }

  private void generateCode() {
    if (mElementMaps.size() == 0) {
      return;
    }
    for (Map.Entry<ClassContext, LinkedList<Element>> entry : mElementMaps
        .entrySet()) {
      ClassContext classContext = entry.getKey();
      LinkedList<Element> elements = entry.getValue();

      String packageName = classContext.packageName;
      String className = classContext.className;

      VariableElement firstVariableElement = (VariableElement) elements.get(0);
      TypeElement typeElement = (TypeElement) firstVariableElement.getEnclosingElement();

      ClassName jsonParseClassName = ClassName.get(classContext.packageName,
          String.format("%s_JsonUtils", className));

      String retObjectName = Utils.toLowerCaseFirstChar(className);

      MethodSpec.Builder fromJsonMethod =
          createFromJsonMethod(typeElement, retObjectName, elements, packageName);
      MethodSpec.Builder fromJsonObjectMethod =
          createFromJsonObjectMethod(typeElement, retObjectName, elements, packageName);
      MethodSpec.Builder toJsonMethod =
          createToJsonMethod(typeElement, retObjectName, elements, packageName);

      MethodSpec fromJsonMethodSpec = fromJsonMethod.build();
      MethodSpec fromJsonObjectMethodSpec = fromJsonObjectMethod.build();
      MethodSpec toJsonMethodSpec = toJsonMethod.build();

      TypeSpec typeSpec = TypeSpec
          .classBuilder(jsonParseClassName)
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addMethod(fromJsonMethodSpec)
          .addMethod(fromJsonObjectMethodSpec)
          .addMethod(toJsonMethodSpec)
          .build();
      JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
          .addFileComment(" This codes are generated automatically. Do not modify!")
          .build();
      try {
        javaFile.writeTo(mFiler);
      } catch (IOException e) {
        onError("Failed to write java file: " + e.getMessage(), null);
      }
    }
  }

  private MethodSpec.Builder createFromJsonMethod(TypeElement typeElement, String retObjectName,
                                                  LinkedList<Element> elements, String packageName) {
    MethodSpec.Builder fromJsonMethod = MethodSpec
        .methodBuilder("fromJson")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(TypeName.get(typeElement.asType()))
        .addParameter(String.class, "json")
        .addStatement("$T rootJsonObject = null", m_JSONObject)
        .beginControlFlow("try")
        .addStatement("rootJsonObject = new $T(json)", m_JSONObject)
        .nextControlFlow("catch ($T e)", Exception.class)
        .endControlFlow()
        .beginControlFlow("if (rootJsonObject == null)")
        .addStatement("return null")
        .endControlFlow();
    fromJsonMethod.addStatement("$1T $2N = new $1T()", typeElement, retObjectName);

    for (Element element : elements) {
      VariableElement variableElement = (VariableElement) element;
      SerializedName annotation = variableElement.getAnnotation(SerializedName.class);
      String jsonKey = annotation.value();
      fromJsonMethod =
          addOptStatement(fromJsonMethod, variableElement, retObjectName, jsonKey, packageName);
    }

    fromJsonMethod.addStatement("return $N", retObjectName);
    return fromJsonMethod;
  }

  private MethodSpec.Builder createFromJsonObjectMethod(TypeElement typeElement,
                                                        String retObjectName, LinkedList<Element> elements, String packageName) {
    MethodSpec.Builder fromJsonObjectMethod = MethodSpec
        .methodBuilder("fromJson")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(TypeName.get(typeElement.asType()))
        .addParameter(m_JSONObject, "rootJsonObject")
        .beginControlFlow("if ($N == null)", "rootJsonObject")
        .addStatement("return null")
        .endControlFlow()
        .addStatement("$1T $2N = new $1T()", typeElement, retObjectName);

    for (Element element : elements) {
      VariableElement variableElement = (VariableElement) element;
      SerializedName annotation = variableElement.getAnnotation(SerializedName.class);
      String jsonKey = annotation.value();
      fromJsonObjectMethod =
          addOptStatement(fromJsonObjectMethod, variableElement, retObjectName, jsonKey,
              packageName);
    }

    fromJsonObjectMethod.addStatement("return $N", retObjectName);
    return fromJsonObjectMethod;
  }


  private MethodSpec.Builder createToJsonMethod(TypeElement typeElement, String retObjectName,
                                                LinkedList<Element> elements, String packageName) {
    MethodSpec.Builder toJsonMethod = MethodSpec
        .methodBuilder("toJson")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(String.class)
        .addParameter(TypeName.get(typeElement.asType()), "bean")
        .addStatement("$1T $2N = new $1T()", m_JSONObject,
            "rootJsonObject");

    for (Element element : elements) {
      VariableElement variableElement = (VariableElement) element;
      SerializedName annotation = variableElement.getAnnotation(SerializedName.class);

      String jsonKey = annotation.value();
      String fieldName = element.getSimpleName().toString();
      String fieldFullClassName = element.asType().toString();
      String simpleClassName = Utils.getSimpleClassName(element);

      boolean isGenericBasicClass = Utils.isGenericBasicClass(element);

      toJsonMethod.beginControlFlow("try");

      if (Utils.isBasicClass(fieldFullClassName)) {
        toJsonMethod.addStatement("rootJsonObject.put($S, bean.$N)", jsonKey, fieldName);
      } else if (fieldFullClassName.startsWith("java.util.List")
          || fieldFullClassName.startsWith("java.util.ArrayList")) {
        String genericFullClassName = Utils.getGenericFullClassName(element);
        String genericSimpleClassName = Utils.getGenericSimpleClassName(element);
        if (isGenericBasicClass) {
          toJsonMethod.addStatement("rootJsonObject.put($1S, new $2T($3N.$4N))", jsonKey,
              m_JSONArray, "bean", fieldName);
        } else {
          toJsonMethod
              .beginControlFlow("if ($1N.$2N != null && !$1N.$2N.isEmpty())", "bean", fieldName)
              .addStatement("$1T jsonArray = new $1T()", m_JSONArray)
              .beginControlFlow("for ($1N tempBean : $2N.$3N)", genericFullClassName, "bean",
                  fieldName)
              .addStatement("jsonArray.put($T.toJson(tempBean))",
                  ClassName.get(packageName, String.format("%s_JsonUtils", genericSimpleClassName)))
              .endControlFlow()
              .addStatement("rootJsonObject.put($S, jsonArray)", jsonKey)
              .endControlFlow();
        }
      } else {
        toJsonMethod.addStatement("rootJsonObject.put($1S, $2T.toJson($3N.$4N))", jsonKey,
            ClassName.get(packageName, String.format("%s_JsonUtils", simpleClassName)),
            "bean",
            fieldName);
      }

      toJsonMethod.nextControlFlow("catch ($T e)", Exception.class)
          .endControlFlow()
          .addCode("\n");
    }

    toJsonMethod.addStatement("return $N.toString()", "rootJsonObject");
    return toJsonMethod;
  }

  private void onError(String message, Element element) {
    mMessager.printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  private void addElement(ClassContext classContext, Element element) {
    if (mElementMaps.containsKey(classContext)) {
      List<Element> list = mElementMaps.get(classContext);
      list.add(element);
    } else {
      LinkedList<Element> list = new LinkedList<>();
      list.add(element);
      mElementMaps.put(classContext, list);
    }
  }

  private MethodSpec.Builder addOptStatement(MethodSpec.Builder builder, VariableElement element,
                                             String retObjectName, String jsonKey, String packageName) {
    String fieldClassName = element.asType().toString();
    String fieldName = element.getSimpleName().toString();
    String optName = "";
    if (element.asType().getKind().isPrimitive()) {
      switch (element.asType().getKind()) {
        case INT:
          optName = "optInt";
          break;
        case LONG:
          optName = "optLong";
          break;
        case BOOLEAN:
          optName = "optBoolean";
          break;
        case DOUBLE:
          optName = "optDouble";
          break;
        case FLOAT:
          optName = "optFloat";
          break;
      }
    } else if (fieldClassName.equals("java.lang.String")) {
      optName = "optString";
    } else if (fieldClassName.startsWith("java.util.List")
        || fieldClassName.startsWith("java.util.ArrayList")) {
      addOptListStatement(builder, element, retObjectName, jsonKey, packageName);
      return builder;
    } else {
      addOptCustomStatement(builder, element, retObjectName, jsonKey, packageName);
      return builder;
    }
    if (optName.equals("optFloat")) {
      builder.addStatement("$1N.$2N = (float) rootJsonObject.$3N($4S, $5N)", retObjectName, fieldName,
          "optDouble",
          jsonKey, retObjectName + "." + fieldName);
    } else {
      builder.addStatement("$1N.$2N = rootJsonObject.$3N($4S, $5N)", retObjectName, fieldName,
          optName,
          jsonKey, retObjectName + "." + fieldName);
    }
    return builder;
  }

  private MethodSpec.Builder addOptListStatement(MethodSpec.Builder builder,
                                                 VariableElement element,
                                                 String retObjectName, String jsonKey, String packageName) {

    String fieldName = element.getSimpleName().toString();
    String genericSimpleClassName = Utils.getGenericSimpleClassName(element);
    String varName_JSONArray = fieldName + "JsonArray";

    String genericFullClassName = Utils.getGenericFullClassName(element);
    boolean isBasicClass = Utils.isGenericBasicClass(element);

    builder.addStatement("$1T $2N = rootJsonObject.optJSONArray($3S)",
        m_JSONArray,
        varName_JSONArray,
        jsonKey)
        .beginControlFlow("if ($N != null)", varName_JSONArray);
    if (isBasicClass) {
      builder.addStatement("$1T<$2N> list = new $1T<>()", ArrayList.class, genericSimpleClassName);
    } else {
      builder.addStatement("$1T<$2N> list = new $1T<>()", ArrayList.class, genericFullClassName);
    }
    builder.beginControlFlow("for (int i = 0; i < $N.length(); i++)", varName_JSONArray);

    if (isBasicClass) {
      if (Utils.isGenericLongClass(element)) {
        builder.addStatement("list.add((($1T) $2N.opt(i)).longValue())", Number.class, varName_JSONArray);
      } else {
        builder.addStatement("list.add(($1N) $2N.opt(i))", genericSimpleClassName, varName_JSONArray);
      }
    } else {
      builder.addStatement("$1T jsonObject = $2N.optJSONObject(i)", m_JSONObject, varName_JSONArray)
          .beginControlFlow("if (jsonObject != null)")
          .addStatement("list.add($T.fromJson(jsonObject))",
              ClassName.get(packageName, String.format("%s_JsonUtils", genericSimpleClassName)))
          .endControlFlow();
    }
    builder.endControlFlow()
        .addStatement("$1N.$2N = list", retObjectName, fieldName)
        .endControlFlow();
    return builder;
  }

  private MethodSpec.Builder addOptCustomStatement(MethodSpec.Builder builder,
                                                   VariableElement element,
                                                   String retObjectName, String jsonKey, String packageName) {

    String fieldName = element.getSimpleName().toString();
    String simpleClassName = Utils.getSimpleClassName(element);
    String varName = Utils.toLowerCaseFirstChar(simpleClassName);
    String varName_JSONObject = String.format("%sJsonObject", fieldName);

    builder.addCode("\n")
        .addStatement("$1T $2N = rootJsonObject.optJSONObject($3S)", m_JSONObject,
            varName_JSONObject, jsonKey)
        .addStatement("$1T $2N = $3T.fromJson($4N)",
            TypeName.get(element.asType()),
            varName,
            ClassName.get(packageName, String.format("%s_JsonUtils", simpleClassName)),
            varName_JSONObject)
        .addStatement("$1N.$2N = $3N", retObjectName, fieldName, varName)
        .addCode("\n");
    return builder;
  }
}
