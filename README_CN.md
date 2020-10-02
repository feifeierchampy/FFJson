# FFJson

- [英文文档](./README.md)

FFJson是一个简单的Json解析工具  
通过注解处理器，使用Android原生`org.json.JSONObject` `org.json.JSONArray`来序列化/反序列化


### 优势
不引入第三方库 解析代码在编译期生成 不依赖反射

### 使用示例

1. 引入相关依赖
```groovy
compileOnly 'com.feifeier.ffjson-annotation:1.0.0'
annotationProcessor 'com.feifeier.ffjson-processor:1.0.0'
```

2. 添加`@SerializedName`注解  
`@SerializedName`里的值对应json字符串里的字段名
```java
public class PersonBean {
  @SerializedName("age")
  public int age;
  
  @SerializedName("name")
  public String name;
}
```

3. 自动生成解析代码
Build -> Make Project
会生成`XXX_JsonUtils`类
通过`toJson` `fromJson`方法实现序列化和反序列化

4. 使用
```java
// 反序列化
PersonBean personBean = PersonBean_JsonUtils.fromJson(jsonStr);

// 序列化
String jsonStr = PersonBean_JsonUtils.toJson(bean);

```


