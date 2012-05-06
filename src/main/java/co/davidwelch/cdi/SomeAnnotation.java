package co.davidwelch.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SomeAnnotation {

    /**
    * This param defines single or multiple subdomain
    * Where the Method/Type is valid to be called
    */
    String[] value() default {};
    /**
    * This param defines site domain and tld
    * It's important to put the leading dot
    * Not an array, so cannot be used for mapping multiple domains/tld
    */
    String tld() default ".custom.tld";
}