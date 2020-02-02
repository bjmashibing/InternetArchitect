## springboot web开发

​		springboot在开发web项目的时候具备天然的优势，现在的很多企业级开发都是依托于springboot的。

​		使用springboot的步骤：

​		1、创建一个SpringBoot应用，选择我们需要的模块，SpringBoot就会默认将我们的需要的模块自动配置好
​		2、手动在配置文件中配置部分配置项目就可以运行起来了
​		3、专注编写业务代码，不需要考虑以前那样一大堆的配置了。

### 1、springboot整合servlet

​		很多同学在刚接触web开发的时候第一个接触的都是servlet，下面我们来使用springboot整合servlet

​		（1）编写servlet类

```java
@WebServlet(name = "myServlet",urlPatterns = "/srv")
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("111");
        super.doGet(req, resp);
    }
}
```

​		（2）在启动类上添加如下配置

```java
@SpringBootApplication
@ServletComponentScan
public class ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<MyServlet> getServletRegistrationBean(){
        ServletRegistrationBean<MyServlet> bean = new ServletRegistrationBean<>(new MyServlet());
        bean.setLoadOnStartup(1);
        return bean;
    }
}
```

​		（3）编写filter类

```java
@WebFilter(filterName = "MyFilter", urlPatterns = "/filter")
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("filter");
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        System.out.println("destory");
    }
}
```

​		（4）编写监听器 

​		listener是servlet规范定义的一种特殊类，用于监听servletContext,HttpSession和ServletRequest等域对象的创建和销毁事件。监听域对象的属性发生修改的事件，用于在事件发生前、发生后做一些必要的处理。可用于以下方面：1、统计在线人数和在线用户2、系统启动时加载初始化信息3、统计网站访问量4、记录用户访问路径。

编写监听器类

```java
package com.mashibing.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class MyHttpSessionListener implements HttpSessionListener {

    public static int online=0;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("创建session");
        online++;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("销毁session");
    }
}

```

添加到配置类

```java
package com.mashibing.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Bean
    public ServletListenerRegistrationBean listenerRegist(){
        ServletListenerRegistrationBean srb = new ServletListenerRegistrationBean();
        srb.setListener(new MyHttpSessionListener());
        System.out.println("listener");
        return srb;
    }
}
```

添加控制层代码

```java
package com.mashibing.controller;

import com.mashibing.config.MyHttpSessionListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class ThymeleafController {
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        HttpSession session = request.getSession(true);
        return "login";
    }

    @RequestMapping("online")
    @ResponseBody
    public String online(){
        return "当前在线人数："+MyHttpSessionListener.online +"人";
    }
}
```

先发送login请求，然后再发送online的请求

### 2、静态资源的配置

​		默认情况下，Spring Boot 将在 classpath 或者 ServletContext 根目录下从名为 /static （/public、/resources 或 /META-INF/resources）目录中服务静态内容。它使用了 Spring MVC 的 ResourceHttpRequestHandler，因此您可以通过添加自己的 WebMvcConfigurerAdapter 并重写 addResourceHandlers 方法来修改此行为。

```java
@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			if (!this.resourceProperties.isAddMappings()) {
				logger.debug("Default resource handling disabled");
				return;
			}
			Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
			CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
			if (!registry.hasMappingForPattern("/webjars/**")) {
				customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
						.addResourceLocations("classpath:/META-INF/resources/webjars/")
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
			String staticPathPattern = this.mvcProperties.getStaticPathPattern();
			if (!registry.hasMappingForPattern(staticPathPattern)) {
				customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
						.addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
		}
```

（1）加载webjars文件

​		在pom文件中添加jquery的相关依赖，直接可以通过浏览器访问到http://localhost:8080/webjars/jquery/3.4.1/jquery.js

```
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.4.1</version>
</dependency>
```

（2）加载静态资源

​		当查找静态资源的时候能够发现静态资源的路径是/**，会去ResourceProperties这个类，可以看到对应的资源目录。

```java
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	/**
	 * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
	 * /resources/, /static/, /public/].
	 */
	private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
	private String[] appendSlashIfNecessary(String[] staticLocations) {
		String[] normalized = new String[staticLocations.length];
		for (int i = 0; i < staticLocations.length; i++) {
			String location = staticLocations[i];
			normalized[i] = location.endsWith("/") ? location : location + "/";
		}
		return normalized;
	}
```

​		可以看到静态资源的目录一共有如下几个：
​		"classpath:/resources/"	
​		"classpath:/static/"	
​		"classpath:/public/" 

（3）首页的配置信息

```java
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext,
				FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
			WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
					new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
					this.mvcProperties.getStaticPathPattern());
			welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
			return welcomePageHandlerMapping;
		}

		private Optional<Resource> getWelcomePage() {
			String[] locations = getResourceLocations(this.resourceProperties.getStaticLocations());
			return Arrays.stream(locations).map(this::getIndexHtml).filter(this::isReadable).findFirst();
		}
private Resource getIndexHtml(String location) {
			return this.resourceLoader.getResource(location + "index.html");
		}
```

### 3、springmvc的扩展

​		springmvc框架是一个mvc的web框架，springmvc允许创建@controller和@RestController bean来处理传入的HTTP请求，控制器种的方法通过@RequestMapping注解映射到HTTP。

​		Springboot提供了适用于大多数Springmvc应用的自动配置。

```tex
引入 ContentNegotiatingViewResolver 和 BeanNameViewResolver bean视图解析器。
支持服务静态资源，包括对 WebJar 的支持。
自动注册 Converter（网页传入的数据封装成对象，完成数据类型的转化）、GenericConverter 和 Formatter bean（将日期转换成规定的格式）。
支持 HttpMessageConverter，用来转换http请求和响应。
自动注册 MessageCodesResolver，定义错误代码生成规则。
支持静态 index.html。
支持自定义 Favicon。
自动使用 ConfigurableWebBindingInitializer bean，将请求树绑定到javaBean中。
```

自动配置了ViewResolver，就是我们之前学习的SpringMVC的视图解析器：即根据方法的返回值取得视图对象（View），然后由视图对象决定如何渲染（转发，重定向）。

```java
//WebMvcAutoConfiguration
@Bean //我们在这里确实看到已经给容器中注册了一个bean
        @ConditionalOnBean({ViewResolver.class})
        @ConditionalOnMissingBean(
            name = {"viewResolver"},
            value = {ContentNegotiatingViewResolver.class}
        )
        public ContentNegotiatingViewResolver viewResolver(BeanFactory beanFactory) {
            ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
            resolver.setContentNegotiationManager((ContentNegotiationManager)beanFactory.getBean(ContentNegotiationManager.class));
            resolver.setOrder(-2147483648);
            return resolver;
        }


//ContentNegotiatingViewResolver
@Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        List<MediaType> requestedMediaTypes = this.getMediaTypes(((ServletRequestAttributes)attrs).getRequest());
        if (requestedMediaTypes != null) {
            //获取候选的视图对象
            List<View> candidateViews = this.getCandidateViews(viewName, locale, requestedMediaTypes);
            //选择一个最适合的视图对象，然后把这个对象返回
            View bestView = this.getBestView(candidateViews, requestedMediaTypes, attrs);
            if (bestView != null) {
                return bestView;
            }
        }

        String mediaTypeInfo = this.logger.isDebugEnabled() && requestedMediaTypes != null ? " given " + requestedMediaTypes.toString() : "";
        if (this.useNotAcceptableStatusCode) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
            }

            return NOT_ACCEPTABLE_VIEW;
        } else {
            this.logger.debug("View remains unresolved" + mediaTypeInfo);
            return null;
        }
    }


//getCandidateViews
private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes)
			throws Exception {

		List<View> candidateViews = new ArrayList<>();
		if (this.viewResolvers != null) {
			Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
			for (ViewResolver viewResolver : this.viewResolvers) {
				View view = viewResolver.resolveViewName(viewName, locale);
				if (view != null) {
					candidateViews.add(view);
				}
				for (MediaType requestedMediaType : requestedMediaTypes) {
					List<String> extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
					for (String extension : extensions) {
						String viewNameWithExtension = viewName + '.' + extension;
						view = viewResolver.resolveViewName(viewNameWithExtension, locale);
						if (view != null) {
							candidateViews.add(view);
						}
					}
				}
			}
		}
		if (!CollectionUtils.isEmpty(this.defaultViews)) {
			candidateViews.addAll(this.defaultViews);
		}
		return candidateViews;
	}


//initServletContext
@Override
	protected void initServletContext(ServletContext servletContext) {
		Collection<ViewResolver> matchingBeans =
				BeanFactoryUtils.beansOfTypeIncludingAncestors(obtainApplicationContext(), ViewResolver.class).values();
		if (this.viewResolvers == null) {
			this.viewResolvers = new ArrayList<>(matchingBeans.size());
			for (ViewResolver viewResolver : matchingBeans) {
				if (this != viewResolver) {
					this.viewResolvers.add(viewResolver);
				}
			}
		}
		else {
			for (int i = 0; i < this.viewResolvers.size(); i++) {
				ViewResolver vr = this.viewResolvers.get(i);
				if (matchingBeans.contains(vr)) {
					continue;
				}
				String name = vr.getClass().getName() + i;
				obtainApplicationContext().getAutowireCapableBeanFactory().initializeBean(vr, name);
			}

		}
		AnnotationAwareOrderComparator.sort(this.viewResolvers);
		this.cnmFactoryBean.setServletContext(servletContext);
	}
```

通过上面的代码分析，我们知道了springboot是在容器中去找视图解析器，因此，我们可以给容器自定义添加视图解析器,这个类会帮我们将他组合起来。

```java
//自定义视图解析器
    @Bean //放到bean中
    public ViewResolver myViewResolver(){
        return new MyViewResolver();
    }

    //我们写一个静态内部类，视图解析器就需要实现ViewResolver接口
    private static class MyViewResolver implements ViewResolver{
        @Override
        public View resolveViewName(String s, Locale locale) throws Exception {
            return null;
        }
    }
```

扩展使用springmvc

```java
/*如果您想保留 Spring Boot MVC 的功能，并且需要添加其他 MVC 配置（interceptor、formatter 和视图控制器等），可以添加自己的 WebMvcConfigurerAdapter 类型的 @Configuration 类，但不能带 @EnableWebMvc 注解。如果您想自定义 RequestMappingHandlerMapping、RequestMappingHandlerAdapter 或者 ExceptionHandlerExceptionResolver 实例，可以声明一个 WebMvcRegistrationsAdapter 实例来提供这些组件。
如果您想完全掌控 Spring MVC，可以添加自定义注解了 @EnableWebMvc 的 @Configuration 配置类
*/
package com.mashibing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/msb").setViewName("success");
    }
}

```

注意：需要添加模板引擎

```xml
<dependency>
			<groupId>org.thymeleaf</groupId>
			<artifactId>thymeleaf-spring5</artifactId>
		</dependency>
		<dependency>
			<groupId>org.thymeleaf.extras</groupId>
			<artifactId>thymeleaf-extras-java8time</artifactId>
		</dependency>
```



