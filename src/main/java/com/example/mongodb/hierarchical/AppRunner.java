package com.example.mongodb.hierarchical;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppRunner {
	public static void main( String[] args ) {
		final ApplicationContext context = new AnnotationConfigApplicationContext( AppConfig.class );
		final DocumentHierarchyService service = context.getBean( DocumentHierarchyService.class );

		service.bootstrap();
		final SimpleDocument document = service.find( "1" );

		if( document != null ) {
			document.write( System.out );
		}
	}
	
	
}
