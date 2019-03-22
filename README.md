# badger-spring-boot-starter

# 概要
Badger针对springboot提供的starter

# 例子
>  * JDK 1.8 or above
>  * springboot 2.x
>  * 编译工具 [Maven][maven] or [Gradle][gradle]

## 添加依赖
   ```xml
    <dependency>
        <groupId>org.jfaster</groupId>
        <artifactId>badger-spring-boot-starter</artifactId>
        <version>1.6</version>
    </dependency>
   ```
   
## 配置yml或者properties文件

    src/main/resources/application.yml

    badger:
      #sql方言，默认即可
      #dialect: mysql
      #分页查询时每页查询的最大条数，不能大于此限制，如果大于会赋值成200
      #page-size-limit: 200
      #由于badger支持分库分表，出于性能考虑会缓存sql的解析结果（不是返回结果），默认是10000
      #cache-sql-limit: 10000
      #badger的事务管理器，只支持badger和spring，默认是badger，如果使用spring得配置事务管理器
      #transactionManager: badger
      #badger引用数据源名配置，ds1和ds2..是数据源的key，可以自定义，如果没有slave，可以不配置。
      #badger中的连接池使用hikaricp，所以hikaricp中的配置在此都可以配置。
      datasources:
        - name: ds1
          master:
            driver-class-name: com.mysql.jdbc.Driver
            jdbc-url: jdbc:mysql://127.0.0.1:3306/test
            user-name: root
            password: 272777475
            maximum-pool-size: 10
            connection-timeout: 3000
    
          #slaves:
          #  - driver-class-name: com.mysql.jdbc.Driver
          #    jdbc-url: jdbc:mysql://127.0.0.1:3306/test
          #    user-name: root
          #    password: 272777475
          #    maximum-pool-size: 10
          #    connection-timeout: 3000
    
        #如果不想使用内置的连接池，在此指定spring中其他数据源bean的id
          #  - ref: 其他数据源在spring中的id
    
        #- name: ds2
        #  master:
        #    driver-class-name: com.mysql.jdbc.Driver
        #    jdbc-url: jdbc:mysql://127.0.0.1:3306/test
        #    user-name: root
        #    password: 272777475
        #    maximum-pool-size: 10
        #    connection-timeout: 3000
      #设置拦截器,实现org.jfaster.badger.sql.interceptor.SqlInterceptor接口，可以在sql执行前后做一些事情
      #interceptor-class: org.jfaster.badger.sql.interceptor.iml.SqlInterceptorImpl
      #设置拦截器引用,填写spring容器中sql拦截器的bean的id。和interceptor-class只能存在其一。
      #interceptor-ref: badgerInterceptor
    

      src/main/resources/application.properties
      #badger.dialect=mysql
      #badger.page-size-limit=200
      #badger.cache-sql-limit=10000
      #badger.interceptor-class=org.jfaster.badger.sql.interceptor.iml.SqlInterceptorImpl
      #badger.interceptor-ref=badgerInterceptor
      
      #badger引用数据源名配置，ds1和ds2..是数据源的key，可以自定义，如果没有slave，可以不配置。
      #badger中的连接池使用hikaricp，所以hikaricp中的配置在此都可以配置。      
      badger.datasources[0].name=ds1
      badger.datasources[0].master.driver-class-name=com.mysql.jdbc.Driver
      badger.datasources[0].master.jdbc-url=jdbc:mysql://127.0.0.1:3306/test
      badger.datasources[0].master.user-name=root
      badger.datasources[0].master.password=272777475
      badger.datasources[0].master.maximum-pool-size=10
      badger.datasources[0].master.connection-timeout=3000
      
      #badger.datasources[0].slaves[0].driver-class-name=com.mysql.jdbc.Driver
      #badger.datasources[0].slaves[0].jdbc-url-class-name=jdbc:mysql://127.0.0.1:3306/test
      #badger.datasources[0].slaves[0].user-name=root
      #badger.datasources[0].slaves[0].password=272777475
      #badger.datasources[0].slaves[0].maximum-pool-size=10
      #badger.datasources[0].slaves[0].connection-timeout=3000
      
      #如果不想使用内置的连接池，在此指定其他数据源的bean。
      #badger.datasource[0].slaves[1].ref=其他数据源在spring中的id
      
      #badger.datasources[1].name=ds2
      #badger.datasources[1].master.driver-class-name=com.mysql.jdbc.Driver
      #badger.datasources[1].master.jdbc-url-class-name=jdbc:mysql://127.0.0.1:3306/test
      #badger.datasources[1].master.user-name=root
      #badger.datasources[1].master.password=272777475
      #badger.datasources[1].master.maximum-pool-size=10
      #badger.datasources[1].master.connection-timeout=3000

> 注释部分不是必须配置项，可以根据自己需要自行配置。

## 创建dao，并启动应用
 
1.
    
    `src/main/java/org/jfaster/badger/pojo/User.java`

    ```java
    package org.jfster.badger.pojo;
    
    /**
     * @author fangyanpeng.
     */   
    @Data
    @Table(tableName = "user", dataSourceName="ds1")
    public class User {
    
        private int id;
    
        private String name;
    
        private int age;
    }

    ```

    `src/main/java/org/jfaster/badger/dao/UserDao.java`

    ```java
    package org.jfster.badger.dao;
    import org.jfster.badger.pojo.User;
    
    import java.util.List;
    
    /**
     * 
     * @author fangyanpeng.
     */
    @Repository
    public class UserDao {
    
        @Resource
        private Badger badger;
        
        public User getById(Integer id) {    
            return badger.get(User.class, id);   
        }
    }

    ```

2. 

   `src/main/java/org/jfaster/badger/TestApplication.java`

   ```java

   package org.jfster.badger;
   
   import org.jfster.badger.dao.UserDao;
   import org.jfster.badger.pojo.User;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.context.ApplicationContext;
   
   import java.util.List;
   
   @SpringBootApplication
   public class TestApplication {
   
   	public static void main(String[] args) {
   		ApplicationContext context = SpringApplication.run(TestApplication.class, args);
   		UserDao userDao = context.getBean(UserDao.class);
   		System.out.println(userDao.getById(10));
   	}
   }
   ```
