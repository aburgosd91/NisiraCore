package com.nisira.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface RelacionTabla {
	/**
	 * Nombre del campo en la tabla sql
	 */
	String campo();
	
	/**
	 * Nombre del campo en la tabla sql foranea
	 */
	
	String campoRelacionado();
}
