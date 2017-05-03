package org.springframework.samples.petclinic.config;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Druid的Servlet
 * @author Andy Chen
 * @since 2016.11.7
//        @WebInitParam(name="deny",value="192.168.0.*"),// IP黑名单 (存在共同时，deny优先于allow)
 */
//@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*", 
initParams={
        @WebInitParam(name="allow",value="127.0.0.1,192.168.0.169"),// IP白名单 (没有配置或者为空，则允许所有访问)
        @WebInitParam(name="loginUsername",value="admin"),// 用户名
        @WebInitParam(name="loginPassword",value="admin"),// 密码
        @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能
})
public class DruidStatViewServlet extends StatViewServlet {
    private static final long serialVersionUID = -2688872071445249539L;
}
