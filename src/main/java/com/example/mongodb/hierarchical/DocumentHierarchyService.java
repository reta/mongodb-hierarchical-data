package com.example.mongodb.hierarchical;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DocumentHierarchyService {
	private MongoOperations template;

	public DocumentHierarchyService( final MongoOperations template ) {
		this.template = template;
	}
	
	public void bootstrap() {
		template.dropCollection( SimpleDocument.class );

		final SimpleDocument parent = new SimpleDocument( "1", "Parent 1" );
		final SimpleDocument child1 = new SimpleDocument( "2", "Child 1.1", parent );
		final SimpleDocument child11 = new SimpleDocument( "3", "Child 1.1.1", child1 );
		final SimpleDocument child12 = new SimpleDocument( "4", "Child 1.1.2", child1 );
		final SimpleDocument child121 = new SimpleDocument( "5", "Child 1.1.2.1", child12 );
		final SimpleDocument child13 = new SimpleDocument( "6", "Child 1.1.3", child1 );
		final SimpleDocument child2 = new SimpleDocument( "7", "Child 1.2", parent );

		template.insertAll( Arrays.asList( parent, child1, child11, child12, child121, child13, child2 ) );
	}
	
	public SimpleDocument find( final String id ) {
		final SimpleDocument document = template.findOne(
				Query.query( new Criteria( "id" ).is( id ) ),
				SimpleDocument.class
			);

		if( document == null ) {
			return document;
		}

		return build(
				document,
				template.find(
					Query.query( new Criteria( "path" ).regex( "^" + id + "[.]" ) ),
					SimpleDocument.class
				)
			);
	}

    private SimpleDocument build( final SimpleDocument root, final Collection< SimpleDocument > documents ) {
        final Map< String, SimpleDocument > map = new HashMap< String, SimpleDocument >();

        for( final SimpleDocument document: documents ) {
            map.put( document.getPath(), document );
        }

        for( final SimpleDocument document: documents ) {
            map.put( document.getPath(), document );

            final String path = document.getPath().substring( 0, document.getPath().lastIndexOf( SimpleDocument.PATH_SEPARATOR ) );
            if( path.equals( root.getPath() ) ) {
                root.getDocuments().add( document );
            } else {
                final SimpleDocument parent = map.get( path );
                if( parent != null ) {
                    parent.getDocuments().add( document );
                }
            }
        }

        return root;
    }
}
