## 公司内部，CRM项目组，针对api部分的开发规则，仅供参考
### 一. api开发总则
1. 遵循单一职责原则，一个类只做一件（类）事。
1. 数据库表与业务类是 1:N 的关系。
1. 简单业务表建议只有一个业务类，复杂业务表，建议有多个业务类。

### 二. 类、接口命名规范
根据业务规则，首字母大写，如，针对表:crmSysApp，业务命名：SysAppService，接口、实现类、抽象类，都是在这个命名的基础上进一步命名的，规则如下。
1. 接口，在前面加I，如：IsysAppService。
1. 抽象类（非必须），在前面A，如：AsysAppService。
1. 实现类，在后面加Impl，如：SysAppServiceImpl，需要加@Service注解。
1. 处理类，在后面加Handler，如：SysAppServiceHandler。
1. 处理类仅仅是业务实现类供消费方（client）进行消费的桥梁，不处理具体的业务。声明业务处理对象，必须也只能用接口来声明，如：IsysAppService sysAppService，且加上@Autowired注解。
    ```java
    public class ShopServiceHandler extends AapiHandler
    {
        @Autowired
        IshopService shopService;
    
        public List<ShopInfoView> getShopInfoView(RequestParametersView requestParametersView)
        {
            return shopService.getShopInfoView(requestParametersView);
        }
    }
    ```

### 三. 开发步骤
1. 接口。在com.maile360.crm.service.facade.api下，创建interface，参考：IsysAppService；抽象类为非必须，如果有需要，在同样的目录下创建即可。参数，统一为：RequestParametersView requestParametersView。
1. 实现类。在com.maile360.crm.service.impl.api下，创建实现类，参考：SysAppServiceImpl
1. 处理类。在com.maile360.crm.service.facade.handler下，创建处理类，参考：SysAppServiceHandler。处理类须 extends AapiHandler。
1. 配置处理类以及方法名参数与实际实现类对应的方法。在restful模块的资源文件夹下，handlers子目录下，添加对应的配置文件，文件名保持与处理类同名（便于维护和查找），如：SysAppServiceHandler.xml，将具体的apiMethod（调用参数）与对应接口名配置好。好处：维护人员方便查找对应处理方法。
1. 配置处理类列表。修改restful模块的资源文件夹下的配置文件：serviceConfig.xml，把上面的handler配置到列表中，目的是让系统根据请求的apiMethod名自动匹配到handler类，然后handler类根据上面配置的方法名自动去调用业务实现类的方法。
1. *配置处理类列表key命名规则：*"crm_"+下面针对apiMethod命名规则中的Y+下划线"_"+版本号，如：crm_sys_sms_template_1.0

### 四. apiMethod，对应handler.methods.key，命名规则
1. 如：X.Y.Z，英语字母全部用小写。
1. X 为前缀，固定:crm
1. Y 为服务名，英语字母加下划线，可以参考业务对应的表名，如：sys_sms_template
1. Z 为具体的业务方法名标志，最好能通过命名看出意义来，英文小写字母，中间以"."隔开，不限制长度，如：get.member.by.primary.key
1. 上面的例子，apiMethod=crm.sys_sms_template.get.member.by.primary.key
1. 业务方法实现相关的配置，见serviceHandler*.xml
1. 一个Service对应一个Handler，一个Handler对应一个xml配置文件，存放到模块restful下，resources/handlers目录，命名规则：serviceHandlerX.xml，其中X为对应Service的名字。

### 五. 返回数据类型
1. 不建议返回类型为Map。在返回类型是确定的情况下，比如是一个int型的数字，或者某个确定的pojo以及某个pojo的列表，则直接返回这个确定的类型。
1. 如果返回的数据直接是数据库表的记录，则返回对应的实体即可。
1. 如果返回的数据需要加工整合，则在 com.maile360.crm.view.out 下，创建对应的view类，类名须以View结束。如：ShopInfoView，字段属性为private，需要setter和getter。
1. 如果返回的数据除了基本类型，还包含实体，即定义在com.maile360.crm.dal.entity下的pojo（非Example类），而crm.view项目下又无法引用这部分pojo，那么，这种情况下，需要在com.maile360.crm.dal.view包下增加返回view的pojo。参考：OrderSmsOptionByTradeView、TopTmcMessageQueueView
1. 如果需要返回多个结果集，定义一个pojo来组装，参考上面。

### 六. 入参
1. 不建议使用Map当作mapper的参数类型。入参多于两个，建议添加pojo来接收。如果在程序内部，也建议通过pojo来组装查询参数。
1. 如果入参只有一个，比如仅为id，则可以这样获取：
    ```java
    int id = (int)requestParametersView.objApiParas.get("id");
    //如果客户端传的是string类型，则上面的转换会报错：java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
    //改为：
    int id = Integer.parseInt(requestParametersView.objApiParas.get("id").toString());
    ```
1. 添加pojo来接收参数时的规则：类名须以View结束。如：ShopSearchConditionView。放在 com.maile360.crm.view.in 包下，字段属性建议直接设置为public，这样，就只需要设置setter而不需要getter。入参接收需在自己的实现类定义和初始化：
    ```java
    ShopSearchConditionView shopSearchConditionView = JSONHelper.json2Object(requestParametersView.apiParas, ShopSearchConditionView.class);
    ```

### 七. mapper扩展
1. 在com.maile360.crm.dal.ext下，创建相应的mapper，扩展自己需要的业务方法。不要继续com.maile360.crm.dal.mapper下的类，会引起装配冲突。
1. com.maile360.crm.dal.mapper、com.maile360.crm.dal.entity，这两个包下的文件，禁止修改。这两个包下的文件是自动生成的，随时可能会被替换。

### 八. 数据库交互方式
以下三种方式均可，对于比较简单的crud，推荐基于注解的方式（下面的前两种方式），对于比较复杂的sql，建议使用更加灵活的xml配置方式。
1. 注解方式一，参考：com.maile360.crm.dal.ext.ShopMapperExt.getShopInfoViewByAnnotated
    ```java
    public class ShopSqlProviderExt
    {
        public String getShopInfoViewByAnnotated(ShopSearchConditionView shopSearchConditionView)
        {
            SQL sql = new SQL();
    
            sql.SELECT("a.appID, a.appName, a.appCode, sp.shopName, sp.sellerNickname, sp.secretKey");
            sql.FROM("crmSysApp a");
            sql.INNER_JOIN("crmShop sp on a.appID=sp.appID");
    
            if (shopSearchConditionView.shopName.length() > 0)
            {
                shopSearchConditionView.shopName = "%" + shopSearchConditionView.shopName + "%";
                sql.WHERE("sp.shopName like #{shopName,jdbcType=VARCHAR}");
            }
    
            if (shopSearchConditionView.sellerNickname.length() > 0)
            {
                sql.WHERE("sp.sellerNickname = #{sellerNickname,jdbcType=VARCHAR}");
            }
    
            if (shopSearchConditionView.shopID > 0)
            {
                sql.WHERE("sp.shopID = #{shopID,jdbcType=INTEGER}");
            }
    
            if (shopSearchConditionView.appID > 0)
            {
                sql.WHERE("sp.appID = #{appID,jdbcType=INTEGER}");
            }
    
            if (shopSearchConditionView.secretKey.length() > 0)
            {
                sql.OR();
                sql.WHERE("sp.secretKey = #{secretKey,jdbcType=VARCHAR}");
            }
    
            sql.ORDER_BY("sp.shopID");
    
            return sql.toString();
        }
    }
    ```
1. 注解方式二，参考：com.maile360.crm.dal.ext.ShopOrderSmsConfigSqlProviderExt
    ```java
    public class ShopOrderSmsConfigSqlProviderExt
    {
        public String getShopOrderSmsConfigInfo(Map<String, Integer> map)//入参已不推荐使用map，上面已写
        {
            String sql = "select sost.orderSTID, sost.iconNameClose, sost.iconNameOpen, sost.smsTypeName, soss.isSwitchOn \n" +
                    "from crmSysOrderSmsType sost \n" +
                    "left join crmShopOrderSmsConfig soss on sost.orderSTID = soss.orderSTID and soss.shopID = #{shopID,jdbcType=INTEGER}\n" +
                    "where sost.orderSTStatus = #{orderSTStatus,jdbcType=INTEGER}\n" +
                    "order by sost.orderByIndex;";
            return sql;
        }
    
        public String getFilterConditionView(Map<String, Integer> map)//入参已不推荐使用map，上面已写
        {
            String sql = "select fc.filterPID, fc.filterEIDs, fc.isAll, fc.filterValueOne, fc.filterValueTwo, fp.propertyName, fp.dataTypeID\n" +
                    "from crmFilterCondition fc\n" +
                    "inner join crmSysFilterProperty fp on fc.filterPID = fp.filterPID\n" +
                    "where fc.shopID = #{shopID,jdbcType=INTEGER} and fc.FCStatus = 1 " +
                    "and fc.bizType = #{bizType,jdbcType=INTEGER} " +
                    "and fc.bizTypeID = #{bizTypeID,jdbcType=INTEGER};\n";
    
            return sql;
        }
    }
    ```
1. 方式三：基于xml配置方式（与注解方式等效），参考：com.maile360.crm.dal.ext.ShopMapperExt.getShopInfoView
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.maile360.crm.dal.ext.ShopMapperExt">
        <select id="getShopInfoView" parameterType="com.maile360.crm.view.in.ShopSearchConditionView"
                resultType="com.maile360.crm.view.out.ShopInfoView">
            select
            a.appID, a.appName, a.appCode, sp.shopName, sp.sellerNickname, sp.secretKey
            from crmSysApp a
            inner join crmShop sp on a.appID=sp.appID
            where 1=1
            <if test="shopName != null and shopName != ''">
                and sp.shopName like CONCAT(CONCAT('%', #{shopName, jdbcType=VARCHAR}),'%')
            </if>
            <if test="sellerNickname != null and sellerNickname != ''">
                and sp.sellerNickname = #{sellerNickname,jdbcType=VARCHAR}
            </if>
            <if test="shopID > 0">
                and sp.shopID = #{shopID,jdbcType=INTEGER}
            </if>
            <if test="appID > 0">
                and sp.appID = #{appID,jdbcType=INTEGER}
            </if>
            <if test="sellerNickname != null and sellerNickname != ''">
                or sp.secretKey = #{secretKey,jdbcType=VARCHAR}
            </if>
            order by sp.shopID
        </select>
    </mapper>
    ```

### 九. *分页查询示例：*
   ```java
    public ResponsePagingView<SysApp> getAppInfoAll(RequestParametersView requestParametersView)
    {
        PageHelper.startPage(requestParametersView.pagingInfo.pageNum, requestParametersView.pagingInfo.pageSize);
        List<SysApp> sysAppList = sysAppMapper.selectByExample(null);

        ResponsePagingView responsePagingView = new ResponsePagingView();
        responsePagingView.resultList = sysAppList;
        responsePagingView.totalRecord = ((Page<?>) sysAppList).getTotal();

        return responsePagingView;
    }
   ```
### 十. 以下为php端调用参数
   ```php
    public function getAppAll()
    {
        Doo::loadClass('ApiClient');
        $c = new ApiClient();

        $request = array();
        $request['apiParas'] = array(
            "id" => 3,
            "pagingInfo" => array(
                "pageNum" => 3,
                "pageSize" => 10
            )
        );
        $request['apiMethod'] = "crm.app.get.all";
        $request['secretKey'] = $this->secret_key;
        $request['shopID'] = $this->shop_id;

        $resp = $c->execute($request);
        print_r($resp);
    }
   ```
