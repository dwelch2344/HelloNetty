package co.davidwelch.netty.mvc.support;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;

import co.davidwelch.netty.mvc.ModelAndView;

public class SpringHttpRequestHandler extends SimpleChannelUpstreamHandler {

	private static String NL = "\n";
	
	private boolean readingChunks;
	
	private MethodMappingResolver resolver;
	
	@Autowired
	public SpringHttpRequestHandler(MethodMappingResolver resolver) {
		super();
		this.resolver = resolver;
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

		
		
		StringBuilder message = new StringBuilder();
		
		
		
		try{
			ModelAndView mav = resolver.invoke( request.getUri(), Arrays.asList((Object) request) );
			message.append("Worked successfully, we tried to serve up: ")
				.append( mav.getViewName() )
				.append( NL );
			Map<String, Object> model = mav.getModel();
			for(String key : model.keySet()){
				message.append("==> ")
					.append(key)
					.append(": ")
					.append( model.get(key) )
					.append(NL);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			message.append("Something went wrong! You requested: ").append(request.getUri()).append(NL);
			// Handle parameters
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
			Map<String, List<String>> params = queryStringDecoder.getParameters();
			
			for(String key : params.keySet()){
				message.append("==> ").append(key).append(": ");
				List<String> plist = params.get(key);
				if(plist != null && !plist.isEmpty()){
					for(int i = 0; i < plist.size(); i++){
						message.append( plist.get(i) );
						if(i + 1 < plist.size()) message.append(", ");
					}
				}
				message.append(NL);
			}
		}
		
		
		
		
		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(message, CharsetUtil.UTF_8));
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

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
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
