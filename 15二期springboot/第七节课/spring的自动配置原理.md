# spring的自动配置原理

springboot配置文件的装配过程

1、springboot在启动的时候会加载主配置类，开启了@EnableAutoConfiguration。

2、@EnableAutoConfiguration的作用：

- 利用AutoConfigurationImportSelector给容器导入一些组件。
- 查看selectImports方法的内容，返回一个AutoConfigurationEntry

```java
AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(autoConfigurationMetadata,
      annotationMetadata);
------
List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
------
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
				getBeanClassLoader());
		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
				+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
```

- 可以看到SpringFactoriesLoader.loadFactoryNames，继续看又调用了loadSpringFactories方法，获取META-INF/spring.factories资源文件

```java
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

总结：将类路径下 META-INF/spring.factories 里面配置的所有EnableAutoConfiguration的值加入到了容器中；每一个xxxAutoConfiguration类都是容器中的一个组件，最后都加入到容器中，用来做自动配置，每一个自动配置类都可以进行自动配置功能

使用HttpEncodingAutoConfiguration来解释自动装配原理

```java
/*
表名这是一个配置类，
*/
@Configuration(proxyBeanMethods = false)
/*
启动指定类的ConfigurationProperties功能,进入HttpProperties查看，将配置文件中对应的值和HttpProperties绑定起来，并把HttpProperties加入到ioc容器中
*/
@EnableConfigurationProperties(HttpProperties.class)
/*
spring底层@Confitional注解，根据不同的条件判断，如果满足指定的条件，整个配置类里面的配置就会生效
此时表示判断当前应用是否是web应用，如果是，那么配置类生效
*/
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/*
判断当前项目由没有这个类CharacterEncodingFilter，springmvc中进行乱码解决的过滤器
*/
@ConditionalOnClass(CharacterEncodingFilter.class)
/*
判断配置文件中是否存在某个配置：spring.http.encoding.enabled
如果不存在，判断也是成立的，
即使我们配置文件中不配置spring.http.encoding.enabled=true，也是默认生效的
*/
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {

    //和springboot的配置文件映射
	private final HttpProperties.Encoding properties;

    //只有一个有参构造器的情况下，参数的值就会从容器中拿
	public HttpEncodingAutoConfiguration(HttpProperties properties) {
		this.properties = properties.getEncoding();
	}

    //给容器中添加一个组件，这个组件的某些值需要从properties中获取
	@Bean
	@ConditionalOnMissingBean//判断容器中是否有此组件
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
		filter.setEncoding(this.properties.getCharset().name());
		filter.setForceRequestEncoding(this.properties.shouldForce(Type.REQUEST));
		filter.setForceResponseEncoding(this.properties.shouldForce(Type.RESPONSE));
		return filter;
	}

	@Bean
	public LocaleCharsetMappingsCustomizer localeCharsetMappingsCustomizer() {
		return new LocaleCharsetMappingsCustomizer(this.properties);
	}

	private static class LocaleCharsetMappingsCustomizer
			implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {

		private final HttpProperties.Encoding properties;

		LocaleCharsetMappingsCustomizer(HttpProperties.Encoding properties) {
			this.properties = properties;
		}

		@Override
		public void customize(ConfigurableServletWebServerFactory factory) {
			if (this.properties.getMapping() != null) {
				factory.setLocaleCharsetMappings(this.properties.getMapping());
			}
		}

		@Override
		public int getOrder() {
			return 0;
		}
	}
}

```

根据当前不同的条件判断，决定这个配置类是否生效！

总结：

​		1、springboot启动会加载大量的自动配置类

​		2、查看需要的功能有没有在springboot默认写好的自动配置类中华

​		3、查看这个自动配置类到底配置了哪些组件

​		4、给容器中自动配置类添加组件的时候，会从properties类中获取属性

@Conditional：自动配置类在一定条件下才能生效

| @Conditional扩展注解            | 作用                                     |
| ------------------------------- | ---------------------------------------- |
| @ConditionalOnJava              | 系统的java版本是否符合要求               |
| @ConditionalOnBean              | 容器中存在指定Bean                       |
| @ConditionalOnMissingBean       | 容器中不存在指定Bean                     |
| @ConditionalOnExpression        | 满足SpEL表达式                           |
| @ConditionalOnClass             | 系统中有指定的类                         |
| @ConditionalOnMissingClass      | 系统中没有指定的类                       |
| @ConditionalOnSingleCandidate   | 容器中只有一个指定的Bean，或者是首选Bean |
| @ConditionalOnProperty          | 系统中指定的属性是否有指定的值           |
| @ConditionalOnResource          | 类路径下是否存在指定资源文件             |
| @ConditionOnWebApplication      | 当前是web环境                            |
| @ConditionalOnNotWebApplication | 当前不是web环境                          |
| @ConditionalOnJndi              | JNDI存在指定项                           |

