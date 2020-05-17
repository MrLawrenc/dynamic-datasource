## 原理
- 复写spring的AbstractDataSource#getConnection等方法，可以获取指定的数据库连接（connection）
- 这样就可以切换到不同的数据源的不同的connect连接
- [完整源码](https://github.com/MrLawrenc/dynamic-datasource)
## 实战
### 引入mybatis-plus动态数据源相关包
- 动态数据源
```xml
        <!--动态数据源-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
            <version>3.1.0</version>
        </dependency>
```
- 必要的包，如mysql、mybatis-plus、boot相关的。
```xml
 	<dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>
        
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.2.0</version>
        </dependency>
```
- 其余的druid连接池、swagger、sql分析插件、lombok等等可以自行选择引入

### yml配置
```xml
server:
  port: 8080
spring:

  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://ip:port/study?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=UTF-8
          username: root
          password: "pwd"
          driver-class-name: com.mysql.cj.jdbc.Driver
        lmy:
          url: jdbc:mysql://localhost:3306/study?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=UTF-8
          username: root
          password: admin
          driver-class-name: com.mysql.cj.jdbc.Driver
logging:
  level:
    com.baomidou: debug


```

### 使用
- 注解方式切换数据源（@DS("数据源名")）
- 手动方式切换数据源(DynamicDataSourceContextHolder.push（"数据源名"）设置)
- 两种实现方式的service如下
```java
@Service
public class ServiceImpl {
    @Autowired
    private UserMapper userMapper;

    /**
     * 注解和手动模式均可以开启
     */
    // @DS("lmy")
    public List<User> lmyList() {
        DynamicDataSourceContextHolder.push("lmy");
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        DynamicDataSourceContextHolder.clear();
        return users;
    }

    //    @DS("master")
    public List<User>  masterList() {
        DynamicDataSourceContextHolder.push("master");
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        DynamicDataSourceContextHolder.clear();
        return users;
    }

    // @DS("lmy")
    public void lmyAdd() {
        DynamicDataSourceContextHolder.push("lmy");
        userMapper.insert(new User(10, "我是插入的lmy1", "", "", "", false, LocalDateTime.now(),LocalDateTime.now()));
        DynamicDataSourceContextHolder.clear();
    }

    //    @DS("master")
    public void masterAdd() {
        DynamicDataSourceContextHolder.push("master");
        userMapper.insert(new User(10, "我是插入的master", "", "", "", false,LocalDateTime.now(),LocalDateTime.now()));
        DynamicDataSourceContextHolder.clear();
    }

}
```
User实体
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String realName;
    private String img;
    private boolean isDel;

    private LocalDateTime createTime;
    private LocalDateTime delTime;
}
```
### 若使用druid连接池
- 先引入druid相关pom
```xml
        <!-- https://mvnrepository.com/artifact/com.alibaba/druid-spring-boot-starter -->
	<!-- 需要连接池则配置，不需要则注释 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.21</version>
        </dependency>
```
- 更改配置文件
更改application.yml文件
```yml
server:
  port: 8080
spring:
  profiles:
    active: druid
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://47.96.158.220:13306/study?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=UTF-8
          username: root
          password: "@123lmyLMY."
          driver-class-name: com.mysql.cj.jdbc.Driver
        lmy:
          url: jdbc:mysql://localhost:3306/study?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=UTF-8
          username: root
          password: admin
          driver-class-name: com.mysql.cj.jdbc.Driver
logging:
  level:
    com.baomidou: debug
```
增加application-druid.yml
```yml
spring:
  datasource:
    druid:
      stat-view-servlet:
        enabled: true
    dynamic:
      druid: #以下是全局默认值，可以全局更改
        filters: stat
      datasource:
        master:
          druid: #这里可以重写默认值
            initial-size: 5
        lmy:
          druid: #这里可以重写默认值
            initial-size: 3
```
- 随后启动类注解排除druid原生配置类
```java
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
```
- 之后即是启用了连接池

### 附其他操作
- 查看当前所有数据源、增加数据源、删除数据源(引入其他非mysql数据源需要引入驱动包)
```java
@RestController
@AllArgsConstructor
@RequestMapping("/datasources")
@Api(tags = "添加删除数据源")
public class LoadController {

  private final DataSource dataSource;
  private final DataSourceCreator dataSourceCreator;
  private final BasicDataSourceCreator basicDataSourceCreator;
  private final JndiDataSourceCreator jndiDataSourceCreator;
  private final DruidDataSourceCreator druidDataSourceCreator;
  private final HikariDataSourceCreator hikariDataSourceCreator;

  @GetMapping
  @ApiOperation("获取当前所有数据源")
  public Set<String> now() {
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    return ds.getCurrentDataSources().keySet();
  }

  @PostMapping("/add")
  @ApiOperation("通用添加数据源（推荐）")
  public Set<String> add(@Validated @RequestBody DataSourceDTO dto) {
    DataSourceProperty dataSourceProperty = new DataSourceProperty();
    BeanUtils.copyProperties(dto, dataSourceProperty);
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
    ds.addDataSource(dto.getPollName(), dataSource);
    return ds.getCurrentDataSources().keySet();
  }


  @PostMapping("/addBasic")
  @ApiOperation(value = "添加基础数据源", notes = "调用Springboot内置方法创建数据源，兼容1,2")
  public Set<String> addBasic(@Validated @RequestBody DataSourceDTO dto) {
    DataSourceProperty dataSourceProperty = new DataSourceProperty();
    BeanUtils.copyProperties(dto, dataSourceProperty);
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    DataSource dataSource = basicDataSourceCreator.createDataSource(dataSourceProperty);
    ds.addDataSource(dto.getPollName(), dataSource);
    return ds.getCurrentDataSources().keySet();
  }

  @PostMapping("/addJndi")
  @ApiOperation("添加JNDI数据源")
  public Set<String> addJndi(String pollName, String jndiName) {
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    DataSource dataSource = jndiDataSourceCreator.createDataSource(jndiName);
    ds.addDataSource(pollName, dataSource);
    return ds.getCurrentDataSources().keySet();
  }

  @PostMapping("/addDruid")
  @ApiOperation("基础Druid数据源")
  public Set<String> addDruid(@Validated @RequestBody DataSourceDTO dto) {
    DataSourceProperty dataSourceProperty = new DataSourceProperty();
    BeanUtils.copyProperties(dto, dataSourceProperty);
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    DataSource dataSource = druidDataSourceCreator.createDataSource(dataSourceProperty);
    ds.addDataSource(dto.getPollName(), dataSource);
    return ds.getCurrentDataSources().keySet();
  }

  @PostMapping("/addHikariCP")
  @ApiOperation("基础HikariCP数据源")
  public Set<String> addHikariCP(@Validated @RequestBody DataSourceDTO dto) {
    DataSourceProperty dataSourceProperty = new DataSourceProperty();
    BeanUtils.copyProperties(dto, dataSourceProperty);
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    DataSource dataSource = hikariDataSourceCreator.createDataSource(dataSourceProperty);
    ds.addDataSource(dto.getPollName(), dataSource);
    return ds.getCurrentDataSources().keySet();
  }

  @DeleteMapping
  @ApiOperation("删除数据源")
  public String remove(String name) {
    DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
    ds.removeDataSource(name);
    return "删除成功";
  }
}

```
- 
