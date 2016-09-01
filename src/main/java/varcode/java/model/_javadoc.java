package varcode.java.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.doc.lib.text.EscapeString;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _javadoc
	implements SelfAuthored
{
	public static _javadoc from( _javadoc jdoc )
	{
		if( jdoc != null && !jdoc.isEmpty() )
		{
			return of( jdoc.comment );
		}
		return new _javadoc();
	}
	
	public static _javadoc of( String... commentLines )
	{
		return new _javadoc( commentLines );
	}
	
	public boolean isEmpty()
	{
		return comment == null || comment.trim().length() == 0;
	}
	
	public static final Dom JAVADOC_DOM = BindML.compile(
		"{{+?comment:" +	
		"/**" + N +
		"{+$formatCommentLines(comment)+}" + N +
		" */" + N + 
		"+}}");
	
	public static final String formatCommentLines( VarContext ctx, String varName )
	{
		String val = (String)ctx.get( varName );
		if( val == null )
		{
			return null;
		}
		return formatCommentLines( val );
	}
	
	public static String formatCommentLines( String input )
	{
		if (input == null )
		{
			return null;
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
		String line;
		try 
		{
			line = br.readLine();
			boolean firstLine = true;
			while( line != null )
			{
				if(! firstLine )
				{
					fb.append( System.lineSeparator() );					
				}
				fb.append( " * " );
				fb.append( EscapeString.escapeJavaString( line ) );
				firstLine = false;
				line = br.readLine();
			}
			return fb.toString();
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error formatting Comment Lines" );
		}	
	}
	
	private String comment;
	
	public _javadoc( )
	{
		this.comment = null;
	}
	
	public _javadoc( String... commentLines )
	{
		if( commentLines != null && commentLines.length > 0 )
		{
			StringBuilder sb = new StringBuilder();
			
			for( int i = 0; i < commentLines.length; i++ )
			{
				if( i > 0 )
				{
					sb.append( System.lineSeparator() );
				}
				sb.append( commentLines[ i ] );
			}
			this.comment = sb.toString();
		}
		//this.comment = comment;
	}
	
	public String getComment()
	{
		if( comment != null )
		{
			return comment;
		}
		return "";
	}
	
	public _javadoc replace( String target, String replacement )
	{
		if( this.comment != null )
		{
			this.comment = this.comment.replace(target, replacement);
		}
		return this;
	}
	
	@Override
	public String toCode( Directive... directives ) 
	{
		return Author.code( 
			this.getClass(), 
			JAVADOC_DOM, 
			VarContext.of( "comment", comment ), 
			directives );
	}
	
	public String toString()
	{
		return toCode();
	}
	

}
