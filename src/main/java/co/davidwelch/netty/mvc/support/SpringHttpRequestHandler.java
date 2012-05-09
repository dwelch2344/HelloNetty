package co.davidwelch.netty.mvc.support;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;

import co.davidwelch.netty.mvc.ModelAndView;
import co.davidwelch.netty.mvc.View;
import co.davidwelch.netty.mvc.impl.ViewResolver;

public class SpringHttpRequestHandler extends SimpleChannelUpstreamHandler {

	private final Logger logger = Logger.getLogger( getClass().getSimpleName() );
	
	private static String NL = "\n";
	
	private boolean readingChunks;
	
	private final MethodMappingResolver mmResolver;
	private ViewResolver viewResolver;
	
	public SpringHttpRequestHandler(MethodMappingResolver mmResolver) {
		this(mmResolver, null);
	}
	
	@Autowired
	public SpringHttpRequestHandler(MethodMappingResolver mmResolver,
			ViewResolver viewResolver) {
		super();
		this.mmResolver = mmResolver;
		this.viewResolver = viewResolver;
	}
	
	public void setViewResolver(ViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (readingChunks) {
			readChunk(null, ctx, e);
		} else {
			handleRequest(e);
		}
	}
	
	private void handleRequest(MessageEvent e){
		HttpRequest request = (HttpRequest) e.getMessage();

		if (is100ContinueExpected(request)) {
			send100Continue(e);
		}

		if (request.isChunked()) {
			readingChunks = true;
		} else {
			ChannelBuffer content = request.getContent();
			if (content.readable()) {
//				buf.append("CONTENT: "
//						+ content.toString(CharsetUtil.UTF_8) + "\r\n");
			}
			writeResponse(request, e);
		}
	}
	
	private void readChunk(HttpRequest request, ChannelHandlerContext ctx, MessageEvent e){
		HttpChunk chunk = (HttpChunk) e.getMessage();
		if (chunk.isLast()) {
			readingChunks = false;

			HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
			if (!trailer.getHeaderNames().isEmpty()) {
				// TODO something with the trailer? I need to learn lots about HTTP :P
			}
			writeResponse(request, e);
		}
	}
	
	

	private void writeResponse(HttpRequest request, MessageEvent e) {
		// Decide whether to close the connection or not.
		boolean keepAlive = isKeepAlive(request);

		
		
		StringBuilder message = null;
		
		
		HttpResponseStatus responseStatus = HttpResponseStatus.OK;
		final ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		try{
			ModelAndView mav = mmResolver.invoke( request.getUri(), Arrays.asList((Object) request) );
			
			View view;
			if( mav.getView() == null ){
				if( this.viewResolver == null ) {
					throw new IllegalStateException("Unable to resolve view; no ViewResolver was supplied");
				}
				view = viewResolver.resolve( mav.getViewName() );
			}else{
				view = mav.getView();
			}
			
			OutputStream os = new OutputStream() {
				
				@Override
				public void write(int b) throws IOException {
					buff.writeByte(b);
				}
			};
			
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
			Map<String, List<String>> params = queryStringDecoder.getParameters();
			view.render(os);
			os.flush();
			os.close();

		}catch(MethodMappingNotFoundException ex){
			message = new StringBuilder();
			responseStatus = HttpResponseStatus.NOT_FOUND;
			message.append("Could not resolve: ").append(request.getUri()).append(NL);
		}catch(Exception ex){
			ex.printStackTrace();
			responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
			message = new StringBuilder();
			message.append("Something went wrong: ").append( ex.getMessage() ).append(NL);
			message.append("You requested: ").append(request.getUri()).append(NL);
		}
		
		
		
		
		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		
		if(message != null){
			response.setContent(ChannelBuffers.copiedBuffer(message, CharsetUtil.UTF_8));
		}else{
			response.setContent(buff);
		}

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, response.getContent().readableBytes());
		}

		// Encode the cookie.
		String cookieString = request.getHeader(COOKIE);
		if (cookieString != null) {
			CookieDecoder cookieDecoder = new CookieDecoder();
			Set<Cookie> cookies = cookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				// Reset the cookies if necessary.
				CookieEncoder cookieEncoder = new CookieEncoder(true);
				for (Cookie cookie : cookies) {
					cookieEncoder.addCookie(cookie);
				}
				response.addHeader(SET_COOKIE, cookieEncoder.encode());
			}
		}

		// Write the response.
		ChannelFuture future = e.getChannel().write(response);

		// Close the non-keep-alive connection after the write operation is
		// done.
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
		e.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.warning("Uncaught exception: " + e.getCause().toString());
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
