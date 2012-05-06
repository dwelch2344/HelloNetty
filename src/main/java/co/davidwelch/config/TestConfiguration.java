package co.davidwelch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import co.davidwelch.cdi.AnnotationProcessor;
import co.davidwelch.test.netty.SomeBean;

@Configuration
public class TestConfiguration {


	@Bean
	public AnnotationProcessor getAnnotationProcessor(){
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		AnnotationProcessor processor = new AnnotationProcessor(resolver);
		return processor;
	}
	
	@Bean
	public SomeBean getSomeBean(){
		return new SomeBean();
	}
}
