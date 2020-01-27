# springboot运行原理

### 1、启动器

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

​		springboot-boot-starter:就是springboot的场景启动器。springboot将所有的功能场景都抽取出来，做成一个个的starter，只需要在项目中引入这些starter即可，所有相关的依赖都会导入进来，根据公司业务需求决定导入什么启动器即可。

### 2、主程序

```java
package com.mashibing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//SpringBootApplication注解用来标注一个主程序类，说明是一个springboot应用
@SpringBootApplication
public class StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }
}
```

查看**@SpringBootApplication**

```java
/*
@ComponentScan:自动扫描并加载符合条件的组件或者bean，将这个bean定义加载到IOC容器中

@SpringBootConfiguration:标注在某个类上，表示这是一个springboot的配置类。

@EnableAutoConfiguration:开启自动配置功能,之前在使用springboot的时候，springboot可以自动帮我们完成配置功能，@EnableAutoConfiguration告诉springboot开启自动配置功能，这样自动配置才能生效
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {}
```

```java
/*
可以看到SpringBootConfiguration使用了Configuration注解来标注
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {}
```

```java
/*
可以看到Configuration也是容器中的一个组件
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {}
```

```java
/*
@AutoConfigurationPackage:自动配置包

@Import(AutoConfigurationImportSelector.class)：导入哪些组件的选择器，它将所有需要导入的组件以全类名的方式返回，这些组件就会被添加到容器中，它会给容器中导入非常多的自动配置类，就是给容器中导入这个场景需要的所有组件，并配置好这些组件
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {}
```

```java
/*
给容器导入一个组件，导入的组件由AutoConfigurationPackages.Registrar.class将主配置类（@SpringBootApplication标注的类）的所在包及包下面所有子包里面的所有组件扫描到spring容器
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {}
```

```java
/*
在AutoConfigurationImportSelector类中有如下方法，可以看到

*/
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
				getBeanClassLoader());
		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
				+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
/*

此时返回的就是启动自动导入配置文件的注解类
*/
protected Class<?> getSpringFactoriesLoaderFactoryClass() {
		return EnableAutoConfiguration.class;
	}

//进入SpringFactoriesLoader类中
/*
看到会读取对应的配置文件，位置在META-INF/spring.factories中

*/
public final class SpringFactoriesLoader {

	/**
	 * The location to look for factories.
	 * <p>Can be present in multiple JAR files.
	 */
public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";}

//进入loadFactoryNames方法中
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
		String factoryTypeName = factoryType.getName();
		return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
	}

	private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
		MultiValueMap<String, String> result = cache.get(classLoader);
		if (result != null) {
			return result;
		}

		try {
			Enumeration<URL> urls = (classLoader != null ?
					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			result = new LinkedMultiValueMap<>();
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				UrlResource resource = new UrlResource(url);
				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
				for (Map.Entry<?, ?> entry : properties.entrySet()) {
					String factoryTypeName = ((String) entry.getKey()).trim();
					for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
						result.add(factoryTypeName, factoryImplementationName.trim());
					}
				}
			}
			cache.put(classLoader, result);
			return result;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load factories from location [" +
					FACTORIES_RESOURCE_LOCATION + "]", ex);
		}
	}
```

springboot在启动的时候从类路径下的META-INF/spring.factories中获取EnableAutoConfiguration指定的值，将这些值作为自动配置类导入容器，自动配置类就生效，帮我们进行自动配置的工作：spring.factories文件位于springboot-autoconfigure.jar包中。

所以真正实现是从classpath中搜寻所有的**META-INF/spring.factories**配置文件，并将其中对应org.springframework.boot.autoconfigure.包下的配置项通过反射实例化为对应标注了@Configuration的JavaConfig形式的IOC容器配置类，然后将这些都汇总称为一个实例并加载到IOC容器中。