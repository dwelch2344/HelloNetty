package co.davidwelch.netty.mvc.impl.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import co.davidwelch.netty.mvc.View;
import co.davidwelch.netty.mvc.ViewResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerDirectoryBasedViewResolver implements ViewResolver{

	private final Configuration freemarker = new Configuration();
	private final String prefix, suffix;
	
	public FreemarkerDirectoryBasedViewResolver(String directory) throws IOException {
		this(directory, null, null);
	}
	
	public FreemarkerDirectoryBasedViewResolver(String directory, String prefix, String suffix) throws IOException {
		freemarker.setDirectoryForTemplateLoading(new File(directory));
		this.prefix = prefix == null ? "" : prefix;
		this.suffix = suffix == null ? "" : suffix;
	}
	
	
	@Override
	public View resolve(String viewName) {
		Template template;
		try {
			template = freemarker.getTemplate(prefix + viewName + suffix);
			return new FreemarkerFileView(template);
		} catch (IOException e) {
			throw new RuntimeException("Failed!");
		}
		
	}
	
	public class FreemarkerFileView implements View{

		private Template template;

		public FreemarkerFileView(Template template) {
			this.template = template;
		}

		@Override
		public void render(OutputStream os, Map<String, Object> model) throws IOException {
			try {
				template.process(model, new PrintWriter(os));
			} catch (TemplateException e) {
				throw new IOException("TemplateException ", e);
			}
		}

		@Override
		public String getContentType() {
			return "text/html";
		}
		
	}
	
	  
}
