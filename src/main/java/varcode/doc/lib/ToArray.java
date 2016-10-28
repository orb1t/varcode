package varcode.doc.lib;

import java.util.Collection;

import varcode.context.VarContext;
import varcode.context.eval.Eval_JavaScript;

/**
 * Normalize the value of a var to be a Java Object[]
 * and return it.
 * handles Javascript arrays, single elements, and Collection types.
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ToArray 
{
	public static Object from( VarContext context, String varName )
	{
		//the user passes in the NAME of the one I want index for
		//	Object var = context.get( varName );
		Object var = context.resolveVar( varName );
		return toArray( var );
	}
	
	public static Object toArray( Object var )
	{
		//System.out.println( var.getClass() );
		if( var != null )
		{
			if( var.getClass().isArray() )
			{				
				return var;				
			}
			if( var instanceof Collection )
			{
				return ((Collection<?>)var).toArray( new Object[ 0 ] );
			}
			Object[] arr = Eval_JavaScript.getJSArrayAsObjectArray( var );
	    	if( arr != null )
	    	{
	    		return arr;
	    	}	    	
			return new Object[]{var};
		}
		return null;
	}
}
