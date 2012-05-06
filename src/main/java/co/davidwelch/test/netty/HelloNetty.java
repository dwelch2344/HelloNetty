package co.davidwelch.test.netty;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import co.davidwelch.cdi.AnnotationProcessor;
import co.davidwelch.cdi.SomeAnnotation;
import co.davidwelch.netty.mvc.support.MethodMappingResolver;

public class HelloNetty {

	public static final Integer MAX_UPLOAD_SIZE = Integer.MAX_VALUE;
	
	public static void main(String[] args) {
		ApplicationContext app = new AnnotationConfigApplicationContext("co.davidwelch");
		MethodMappingResolver resolver = app.getBean(MethodMappingResolver.class);
		
		resolver.invoke("/users/list");
	}
	
	public static void main3(String[] args) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		AnnotationProcessor processor = new AnnotationProcessor(resolver);
		
		Set<Class<?>> foo = processor.scan(SomeAnnotation.class, Arrays.asList("co.davidwelch.test.netty") );
		System.out.println("I got " + foo);
		
	}
	
	public static void main2(String[] args) {

		System.out.println("Starting");
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		System.out.println("Setting pipeline Factory");
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

		System.out.println("Binding");
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(8080));
		
		System.out.println("Binded. Press enter to exit...");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		
		System.out.println("Exiting");
		bootstrap.releaseExternalResources();
		
		
	}

	public static class HttpServerPipelineFactory implements
			ChannelPipelineFactory {
		public ChannelPipeline getPipeline() throws Exception {
			// Create a default pipeline implementation.
			ChannelPipeline pipeline = new DefaultChannelPipeline();

			// Uncomment the following line if you want HTTPS
//			 SSLEngine engine = null; //SecureChatSslContextFactory.getServerContext().createSSLEngine();
//			 engine.setUseClientMode(false);
//			 pipeline.addLast("ssl", new SslHandler(engine));

			pipeline.addLast("decoder", new HttpRequestDecoder());
			// Uncomment the following line if you don't want to handle
			// HttpChunks.
			
			pipeline.addLast("aggregator", new HttpChunkAggregator( MAX_UPLOAD_SIZE ));
			pipeline.addLast("encoder", new HttpResponseEncoder());

			// Remove the following line if you don't want automatic content
			// compression.
			pipeline.addLast("deflater", new HttpContentCompressor());
			pipeline.addLast("handler", new HttpRequestHandler());

			return pipeline;
		}
	}
}
