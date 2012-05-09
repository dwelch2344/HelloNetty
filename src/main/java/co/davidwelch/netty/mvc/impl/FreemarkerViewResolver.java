package co.davidwelch.netty.mvc.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import co.davidwelch.netty.mvc.View;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerViewResolver implements ViewResolver{

	private Configuration freemarker = new Configuration();
	
	public FreemarkerViewResolver() throws IOException {
		Configuration config = freemarker;
        
        // Process TemplatePath init-param out of order:
        //config.setTemplateLoader(createTemplateLoader("class://"));
		config.setDirectoryForTemplateLoading(new File("/Users/dwelch/Desktop"));
	}
	
	@Override
	public View resolve(String viewName) {
		Template template;
		try {
			template = freemarker.getTemplate(viewName);
			return new DummyView(template);
		} catch (IOException e) {
			throw new RuntimeException("Failed!");
		}
		
	}
	
	protected TemplateLoader createTemplateLoader(String templatePath) throws IOException
    {
        if (templatePath.startsWith("class://")) {
            // substring(7) is intentional as we "reuse" the last slash
            return new ClassTemplateLoader(getClass(), templatePath.substring(7));
        } else {
            if (templatePath.startsWith("file://")) {
                templatePath = templatePath.substring(7);
                return new FileTemplateLoader(new File(templatePath));
            } else {
            	throw new RuntimeException();
                //return new WebappTemplateLoader(this.getServletContext(), templatePath);
            }
        }
    }
	
	public class DummyView implements View{

		private Template template;

		public DummyView(Template template) {
			this.template = template;
		}

		@Override
		public void render(OutputStream os) throws IOException {
			PrintWriter writer = new PrintWriter(os);
			writer.write("Hello world from view!");
			writer.flush();
			writer.close();
			
			try {
				template.process(null, new PrintWriter(os));
			} catch (TemplateException e) {
				throw new IOException("TemplateException ", e);
			}
		}

		@Override
		public String getContentType() {
			return "text/plain";
		}
		
	}
	
	  
}
