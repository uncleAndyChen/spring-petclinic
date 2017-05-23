# spring 官方例子->宠物医院，数据库改成 MySQL，数据库连接池改成 druid
修改部分
1. 把默认数据源改成了mysql
1. 数据库连接池改成了druid
1. 修改web目录为webapp

###  运行
1. 从git china上clone代码
```bash
git clone https://git.oschina.net/elsafly/spring-petclinic.git
```

1. 创建mysql数据库和初始化数据。执行resources/db/mysql/schema.sql，没有创建数据库petclinic的话，会自动创建，不过，建议先手动创建，通过以下脚本创建，可以指定编码格式
```sql
create schema petclinic default character set utf8;
```

1. 执行resources/db/mysql/data.sql
1. 修改application.yml文件中数据库连接参数，改为你的数据库服务器IP和用户名、密码：
```bash
  datasource:
    url: jdbc:mysql://192.168.0.130:3306/petclinic?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false
    username: root
    password: root
```

1. 执行如下命令：
```bash
cd spring-petclinic #进入代码主目录
mvn spring-boot:run #运行
```

1. 如果通过上面的运行脚本，运行过程中有错误，则执行 mvn clean compile，会下载所需依赖，看到编译成功之后，再执行 mvn spring-boot:run
1. 通过上面的步骤成功运行之后，也可以直接运行PetClinicApplication.main，IDEA会自动识别，可直接运行。
1. 在执行mvn spring-boot:run 之前就直接通过IDEA运行spring-boot入口方法，即PetClinicApplication.main，css样式和js不会被加载，因为这个demo的css和js是动态生成的。
1. 也可以通过 IDEA 的 Terminal 命令窗口执行 mvn spring-boot:run
1. 还可以在运行配置里增加一项 maven 启动，在Commond line栏处输入：spring-boot:run，这种方式还可以利用 IDEA 的调试功能。
1. 通过上面步骤成功运行之后，访问：http://localhost:8083

### 说明
该demo来自spring官方，简单改了一下，要看官方原版，[猛戳进入](https://github.com/spring-projects/spring-petclinic)。
