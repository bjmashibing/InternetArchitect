package com.mashibing.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class DruidConfig {
//    @ConfigurationProperties(prefix = "spring.datasource")
//    @Bean
//    public DataSource druidDataSource(){
//        return new DruidDataSource();
//    }
//
//    @Bean
//    public ServletRegistrationBean druidServletRegistrationBean(){
//        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(),"/druid/*");
//        Map<String,String> initParams = new HashMap<>();
//        initParams.put("loginUsername","admin");
//        initParams.put("loginPassword","123456");
//        //后台允许谁可以访问
//        //initParams.put("allow", "localhost")：表示只有本机可以访问
//        //initParams.put("allow", "")：为空或者为null时，表示允许所有访问
//        initParams.put("allow","");
//        //deny：Druid 后台拒绝谁访问
////        initParams.put("msb", "192.168.1.20");//表示禁止此ip访问
//
//        servletRegistrationBean.setInitParameters(initParams);
//        return servletRegistrationBean;
//    }
//
//    //配置 Druid 监控 之  web 监控的 filter
//    //WebStatFilter：用于配置Web和Druid数据源之间的管理关联监控统计
//    @Bean
//    public FilterRegistrationBean webStatFilter() {
//        FilterRegistrationBean bean = new FilterRegistrationBean();
//        bean.setFilter(new WebStatFilter());
//
//        //exclusions：设置哪些请求进行过滤排除掉，从而不进行统计
//        Map<String, String> initParams = new HashMap<>();
//        initParams.put("exclusions", "*.js,*.css,/druid/*");
//        bean.setInitParameters(initParams);
//
//        //"/*" 表示过滤所有请求
//        bean.setUrlPatterns(Arrays.asList("/*"));
//        return bean;
//    }
}