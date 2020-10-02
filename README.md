# FFJson

- [中文文档](./README_CN.md)

FFJson is a simple json parser tools, it can be used to convert Java Objects into their JSON representation.  
It can also be used to convert a JSON string to an equivalent Java object.
It uses annotation processor to auto generate parser code with `org.json.JSONObject` and `org.json.JSONArray`class.

### Advantage
Do not rely on other third parties, the parse code generated in compile time, do not rely on reflection.

### Getting Started

1. impletation the dependencies
```groovy
compileOnly 'com.feifeier.ffjson-annotation:1.0.0'
annotationProcessor 'com.feifeier.ffjson-processor:1.0.0'
```

2. add `@SerializedName` annotation
```java
public class PersonBean {
  @SerializedName("age")
  public int age;
  
  @SerializedName("name")
  public String name;
}
```

3. generate parse code  
Build -> Make Project  
will generate `XXX_JsonUtils`class
use`toJson` `fromJson` method to implement serialization and deserialization

4. use fromJson or toJson
```java

PersonBean personBean = PersonBean_JsonUtils.fromJson(jsonStr);

String jsonStr = PersonBean_JsonUtils.toJson(bean);

```


