package com.example.mongodb.hierarchical;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document( collection = "documents" )
public class SimpleDocument {
	public static final String PATH_SEPARATOR = ".";

	@Id private String id;
	@Field private String name;
	@Field private String path;

	// We won't store this collection as part of document but will build it on demand
	@Transient private Collection< SimpleDocument > documents = new LinkedHashSet< SimpleDocument >();

	public SimpleDocument() {
	}

	public SimpleDocument( final String id, final String name ) {
		this.id = id;
		this.name = name;
		this.path = id;
	}

	public SimpleDocument( final String id, final String name, final SimpleDocument parent ) {
		this( id, name );
		this.path = parent.getPath() + PATH_SEPARATOR + id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Collection< SimpleDocument > getDocuments() {
		return documents;
	}

	public void write( final PrintStream writer ) {
		write( writer, Collections.singletonList( this ), 0 );
	}

	private void write( final PrintStream writer, final Collection< SimpleDocument > documents, final int level ) {
		final StringBuilder sb = new StringBuilder();

		if( level > 0 ) {
			sb.append( "|--" );

			for( int i = 0; i < level; ++i ) {
				sb.insert( i, "  " );
			}
		}

		for( final SimpleDocument document: documents ) {
			writer.println( String.format( "%s %s", sb.toString(), document.getName() ) );
			write( writer, document.getDocuments(), level + 1 );
		}
	}

}
