package co.davidwelch.test.netty;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerPipelineFactory implements ChannelPipelineFactory {
	
	public static final Integer MAX_UPLOAD_SIZE = Integer.MAX_VALUE;
	
	private ChannelHandler handler;
	
	public HttpServerPipelineFactory(ChannelHandler handler) {
		super();
		this.handler = handler;
	}



	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = new DefaultChannelPipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine = null;
		// //SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));

		pipeline.addLast("decoder", new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle
		// HttpChunks.

		pipeline.addLast("aggregator", new HttpChunkAggregator(MAX_UPLOAD_SIZE));
		pipeline.addLast("encoder", new HttpResponseEncoder());

		// Remove the following line if you don't want automatic content
		// compression.
		pipeline.addLast("deflater", new HttpContentCompressor());
		//pipeline.addLast("handler", new HttpRequestHandler());
		pipeline.addLast("handler", handler);

		return pipeline;
	}
}