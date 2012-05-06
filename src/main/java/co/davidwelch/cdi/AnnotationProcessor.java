package co.davidwelch.cdi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;


public class AnnotationProcessor {

	private static final String CLASS_RESOURCE_PATTERN = "**/*.class";
	
	
	private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
    
	public AnnotationProcessor(ResourcePatternResolver resourcePatternResolver) {
		super();
		this.resourcePatternResolver = resourcePatternResolver;
	}

	public Set<Class<?>> scan(Class<? extends Annotation> annotationToScanFor, String packageName){
		return scan(annotationToScanFor, Arrays.asList(packageName));
	}
	
	public Set<Class<?>> scan(Class<? extends Annotation> annotationToScanFor, String[] packages){
		return scan(annotationToScanFor, Arrays.asList(packages));
	}

	public Set<Class<?>> scan(Class<? extends Annotation> annotationToScanFor, List<String> packages){
		TypeFilter annotationFilter = new AnnotationTypeFilter(annotationToScanFor);
		
        Set<Class<?>> persistentClasses = new HashSet<Class<?>>();

        try {
            for (String p : packages) {
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(p)) +
                        "/" +
                        CLASS_RESOURCE_PATTERN;

                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);

                        if (annotationFilter.match(metadataReader, metadataReaderFactory)) {
                            persistentClasses.add(Class.forName(metadataReader.getAnnotationMetadata().getClassName()));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("I/O failure during classpath scanning", ex);
        } catch (ClassNotFoundException ex) {
			throw new RuntimeException("Class not found during scanning: ", ex);
		}
        
        return persistentClasses;
	}
}
