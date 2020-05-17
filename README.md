## 引入苞米豆的动态数据源依赖
其余的mysql h2 等包依据自身需求引进
```xml
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.200</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.alibaba/druid-spring-boot-starter -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.21</version>
        </dependency>

        <!--动态数据源-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
            <version>3.1.0</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>

```
## 使用

- 注解
DS标记使用lmy这个数据源，DS注解可以像如下加在方法上，也可以作用于类上（推荐）
```java
     @DS("lmy")
    public void t1() {
        //DynamicDataSourceContextHolder.push("lmy");
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        //DynamicDataSourceContextHolder.clear();
    }
```
- 手动切换
在具体的数据库操作方法之前，可以设置当前数据源，push是添加到一个栈顶，数据库操作是取栈顶数据源，
使用栈是为了支持多数据库嵌套操作，手动设置尽量调用clear清除
```java
public void t1() {
    DynamicDataSourceContextHolder.push("lmy");
    List<User> users = userMapper.selectList(null);
    System.out.println(users);
    DynamicDataSourceContextHolder.clear();
    }
```
---

分界线

---

## 若不使用druid连接池
注释掉pom中对于连接池的引用，且使用application.yml配置文件

## 若使用连接池，以druid为例
需要引用pom
```xml
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.21</version>
        </dependency>
```
且需要排除原生druid的配置类
```java
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
```
