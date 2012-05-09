package co.davidwelch.config;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import co.davidwelch.cdi.AnnotationProcessor;
import co.davidwelch.netty.mvc.impl.FreemarkerViewResolver;
import co.davidwelch.netty.mvc.impl.ViewResolver;
import co.davidwelch.netty.mvc.support.MethodMappingResolver;
import co.davidwelch.netty.mvc.support.SpringHttpRequestHandler;
import co.davidwelch.test.netty.HttpServerPipelineFactory;
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
	
	@Bean
	public ChannelHandler getHandler(MethodMappingResolver mmResolver, ViewResolver viewResolver){
		//return new HttpRequestHandler();
		return new SpringHttpRequestHandler(mmResolver, viewResolver);
	}
	
	@Bean
	public ViewResolver getViewResolver() throws IOException{
		return new FreemarkerViewResolver();
	}
	
	@Bean
	public ServerBootstrap getBootstrap(ChannelHandler handler){
		System.out.println("Starting");
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		System.out.println("Setting pipeline Factory");
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(handler));

//		System.out.println("Binding");
//		// Bind and start to accept incoming connections.
//		bootstrap.bind(new InetSocketAddress(8080));
		
		return bootstrap;
	}
}
