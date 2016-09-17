package varcode.java.code;

import varcode.java.JavaCase.JavaCaseAuthor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.Template;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.JavaCase;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.code._methods._method;
import varcode.java.code._nest._nestGroup;
import varcode.java.code._nest.component;
import varcode.markup.bindml.BindML;

//allow default methods
public class _interface 
	implements JavaCaseAuthor, _nest.component
{
	public static final Dom INTERFACE = 
		BindML.compile( 
			"{+pckage+}" +
			"{{+?imports:{+imports+}" + N +"+}}" +
			"{+javaDoc+}" +
			"{+signature*+}" + N +
			"{" + N +			
			"{{+?members:{+$>(members)+}" + N +
			"+}}" +
			"{{+?methods:{+$>(methods)+}" + N + 
			"+}}" +
			"{{+?nests:{+$>(nests)+}" + N +
			"+}}" +
			"}" );
	
	public VarContext getContext() 
	{
		String[] n = null;
		if( nests.count() > 0 )
		{
			//I need to go to each of the nested classes/ interfaces/ etc.
			// and read what thier imports are, then add these imports to my imports
			String[] nested = new String[ nests.count() ];
			for( int i = 0; i < nests.count(); i++ )
			{
				component comp = nests.components.get( i );
				VarContext vc = comp.getContext();
				vc.getScopeBindings().remove("pckage");
				vc.getScopeBindings().remove("imports");
				nested[ i ] = Author.code( comp.getDom(), vc );				
			}
			n = nested;			
		}
		
		//_nesteds n = null;
		
		//if( this.nests.count() > 0 )
		//{
		//	n = this.nests;
		//}
		_imports imp = null;
		if( this.getImports().count() > 0 )
		{
			imp = this.getImports();
		}
		_fields mem = null;
		if( this.fields.count() > 0 )
		{
			mem = this.fields;
		}
		_methods meth = null;
		if( this.methods.count() > 0 )
		{
			meth = this.methods;
		}
		
		return VarContext.of( 
			"pckage", interfacePackage,
			"imports", imp,
			"javaDoc", javadoc,
			"signature", interfaceSignature,
			"members", mem,
			"methods", meth, 
			"nests", n );		
	}
	
	/**
	 * i.e.<PRE>
	 * _interface inter = 
	 *    _interface.of("public interface MyInterface extends Serializable");
	 * </PRE>    
	 * @param interfaceSignature the signature of the interface
	 * @return the interface
	 */
	public static _interface of( String interfaceSignature )
	{
		return of( "", interfaceSignature );
	}
	
	public static _interface from( _interface prototype )
	{
		return new _interface( prototype );
	}
	
	/**
	 * i.e.<PRE>
	 * _interface inter = 
	 *    _interface.of(
	 *        "io.varcode.mypackage", 
	 *        "public interface MyInterface extends Serializable");
	 * </PRE>  
	 * @param packageName
	 * @param interfaceSignature
	 * @return
	 */
	public static _interface of( String packageName, String interfaceSignature )
	{
		return new _interface( packageName, interfaceSignature );
	}
	
	private _package interfacePackage;
	private _javadoc javadoc;
	private _signature interfaceSignature;
	private _fields fields;
	private _methods methods;
	private _imports imports;
	private _nestGroup nests;
	
    public void replace( String target, String replacement )
    {
        this.interfacePackage.replace( target, replacement );
        this.javadoc.replace( target , replacement );
        this.interfaceSignature.replace( target, replacement );
        this.fields.replace( target, replacement );
        this.methods.replace( target, replacement );
        this.imports.replace( target, replacement );
        this.nests.replace( target, replacement );
    }
    
	/**
	 * Create and return a mutable clone given the prototype
	 * @param prototype the prototype
	 */
	public _interface( _interface prototype )
	{
		this.interfacePackage = _package.from( prototype.interfacePackage );
		this.javadoc = _javadoc.from( prototype.javadoc );
		this.interfaceSignature = _signature.from( prototype.interfaceSignature  );
		this.fields = _fields.from( prototype.fields );
		this.methods = _methods.from( prototype.methods );
		this.imports = _imports.from( prototype.imports );
		
		//NESTEDS
		this.nests = _nestGroup.from( prototype.nests );
	}
	
	public _interface( String packageName, String interfaceSignature )
	{
		this.interfacePackage = _package.of( packageName );
		this.interfaceSignature = _signature.of( interfaceSignature );
		this.javadoc = new _javadoc();
		this.methods = new _methods();
		this.fields = new _fields();
		this.imports = new _imports();
		this.nests = new _nestGroup();
	}

	public _interface packageName( String packageName )
	{
		this.interfacePackage = _package.of( packageName );
		return this;
	}
	
	public String getPackageName()
	{
		return this.interfacePackage.getName();
	}
	
	public _signature getSignature()
	{
		return this.interfaceSignature;
	}
	
	public String getJavadoc()
	{
		return this.javadoc.getComment();
	}
	
	public _fields getFields()
	{
		return this.fields;
	}
	
	public _methods getMethods()
	{
		return this.methods;
	}
	
	public _nestGroup getNests()
	{
		return this.nests;
	}
	public _interface imports( Object... imports )
	{
		this.imports.addImports( imports );
		return this;
	}
	
	public _imports getImports()
	{
		for( int i = 0; i < nests.count(); i++ )
		{
			this.imports.merge( nests.components.get( i ).getImports() );
		}
		return this.imports;
	}
	
    public _interface method( String javadoc, String signature )
    {
        _method method = _method.of( signature );
		
        if( javadoc != null  )
        {
            method.javadoc( javadoc );
        }
		_methods._method.signature sig = method.getSignature();
		
		//Default Method
		if( sig.getModifiers().contains( Modifier.PRIVATE ) )
		{
			throw new VarException( "Cannot add a private method " + N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.FINAL ) )
		{
			throw new VarException( "Cannot add a final method " + N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.PROTECTED ) )
		{
			throw new VarException( "Cannot add a protected method " + N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().containsAny(Modifier.NATIVE, Modifier.STRICT, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE))
		{
			throw new VarException( "Invalid Modifiers for interface method "+ N + sig );
		}
		method.getSignature().getModifiers().set( "abstract" );
		this.methods.addMethod( method );
		return this;		
    }
    
	public _interface method( String signature )
	{
		return method( null, signature );
	}
	
	public _interface staticMethod( String signature, String...linesOfCode )
	{
		_method method = _method.of( null, signature, linesOfCode);
		
		_methods._method.signature sig = method.getSignature();
		if( !sig.getModifiers().contains( Modifier.STATIC ) )
		{			
			sig.getModifiers().set( "static" );
		}
		if( sig.getModifiers().contains( Modifier.PRIVATE ) )
		{
			throw new VarException( "Cannot add a private method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.FINAL ) )
		{
			throw new VarException( "Cannot add a final method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.PROTECTED ) )
		{
			throw new VarException( "Cannot add a protected method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().containsAny( 
			Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE))
		{
			throw new VarException( "Invalid Modifiers for interface method "+ N + sig );
		}
		this.methods.addMethod( method );
		return this;
	}
	public _interface defaultMethod( String signature, String...linesOfCode )
	{
		_method method = _method.of( null, signature, linesOfCode);
		
		_methods._method.signature sig = method.getSignature();
		if( !sig.getModifiers().contains( _modifiers._mod.INTERFACE_DEFAULT.getBitValue() ) )
		{			
			sig.getModifiers().set( "default" );
		}
		if( sig.getModifiers().contains( Modifier.PRIVATE ) )
		{
			throw new VarException( "Cannot add a private method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.FINAL ) )
		{
			throw new VarException( "Cannot add a final method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().contains( Modifier.PROTECTED ) )
		{
			throw new VarException( "Cannot add a protected method " +N + sig + N +" to an interface ");
		}
		if( sig.getModifiers().containsAny( Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE))
		{
			throw new VarException( "Invalid Modifiers for interface method "+ N + sig );
		}
		this.methods.addMethod( method );
		return this;
	}
	
	public _interface field( String fieldSignature )
	{
		_fields._field m = _fields._field.of( fieldSignature );
		if( m.getInit() == null || m.getInit().getCode().trim().length() == 0 )
		{
			throw new VarException("Field : " + N + m +  N 
				+ " has not been initialized for interface ");
		}
		fields.addFields( m );		
		return this;
	}

	public _interface nest( _nest.component component )
	{
		this.nests.add( component );
		return this;
	}
	
	public static class _signature
		extends Template.Base
	{
		private _modifiers modifiers = new _modifiers();
		private String interfaceName;		
		private _extends extendsFrom = new _extends();
		
		public static final Dom INTERFACE_SIGNATURE = 
			BindML.compile("{+modifiers+}interface {+interfaceName*+}{+extendsFrom+}" );

		public String author( Directive... directives ) 
		{
			return Author.code( INTERFACE_SIGNATURE, 
				VarContext.of(
					"modifiers", modifiers,
					"interfaceName", interfaceName,				
					"extendsFrom", extendsFrom ), 
				directives );
		} 
		
		public static _signature from( _signature prototype ) 
		{
			_signature clone = new _signature( );
			clone.modifiers = _modifiers.from( prototype.modifiers );
			clone.interfaceName = prototype.interfaceName + "";
			clone.extendsFrom = _extends.from( prototype.extendsFrom );
			return clone;
		}
		
        public void replace( String target, String replacement )
        {
            this.interfaceName = this.interfaceName.replace( target, replacement );
            this.extendsFrom.replace( target, replacement );            
        }

		public String getName()
		{
			return this.interfaceName;
		}
		
		public String toString()
		{
			return author();
		}
		
		public _extends getExtends()
		{
			return extendsFrom;
		}

		public static _signature of( String interfaceSignature )
		{
			_signature sig = new _signature();
		
			//MUST have sequence
			//   ...interface 
			String[] tokens = interfaceSignature.split(" ");
			int interfaceTokenIndex = -1;
			int extendsTokenIndex = -1;
		
			if( tokens.length < 2 )
			{
				throw new VarException( "interface signature must have at least (2) tokens interface <name>" );	
			}
			//	"public final class HelloWorld extends Blah implements Serializable, Externalizable" 
		
			for( int i = 0; i < tokens.length; i++ )
			{
				if( tokens[ i ].equals( "interface" ) )
				{
					interfaceTokenIndex = i;
				}
				else if( tokens[ i ].equals( "extends" ) )
				{
					extendsTokenIndex = i;
				}			
			}  
		
			if(( interfaceTokenIndex < 0 ) || ( interfaceTokenIndex > tokens.length -1 ) )
			{   //cant be 
				throw new VarException(
						"interface token cant be not found or the last token"); 
			}
			sig.interfaceName = 
                tokens[ interfaceTokenIndex + 1 ];
		
			if( interfaceTokenIndex > 0 )
			{   //modifiers provided
				String[] mods = new String[ interfaceTokenIndex ];
				System.arraycopy( tokens, 0, mods, 0, interfaceTokenIndex );
				sig.modifiers = varcode.java.code._modifiers.of( mods );
			}
			
			if( extendsTokenIndex > interfaceTokenIndex + 1 )
			{
				if( extendsTokenIndex == tokens.length -1 )
				{
					throw new VarException( 
						"extends token cannot be the last token" );
				}
				
				int tokensLeft = tokens.length - ( extendsTokenIndex + 1 );
				String[] extendsTokens = new String[ tokensLeft ];
				
				System.arraycopy( tokens, extendsTokenIndex + 1, extendsTokens, 0, tokensLeft );
				List<String>normalExtendsTokens = new ArrayList<String>();
				for( int i = 0; i < extendsTokens.length; i++)
				{
					if( extendsTokens[ i ].contains( "," ) )
					{
						String[] splitTokens = extendsTokens[ i ].split( "," );
						for( int j = 0; j < splitTokens.length; j++ )
						{
							String tok = splitTokens[ j ].trim();
							if( tok.length() > 0 )
							{
								normalExtendsTokens.add( tok );
							}
						}
					}
					else
					{
						String[] splitTokens = extendsTokens[ i ].split( " " );
						for( int j = 0; j < splitTokens.length; j++ )
						{
							String tok = splitTokens[ j ].trim();
							if( tok.length() > 0 )
							{
								normalExtendsTokens.add( tok );
							}
						}
					}
				}
				sig.extendsFrom = varcode.java.code._extends.of( 
					normalExtendsTokens.toArray( new String[ 0 ] ) ); //className.of( implementsTokens );
			}
			return sig;		
		}	
	}

	public JavaCase toJavaCase( Directive... directives ) 
	{
		return JavaCase.of( getFullyQualifiedClassName(), INTERFACE, getContext(), directives );
	}

    /** 
     * authors, compiles and loads this interface in an AdHocClassLoader 
     * and returns the Class.
     * 
     * @return Class the class representing this interface
     */
    public Class loadClass( )
    {
        return toJavaCase().loadClass();
    }
    
    /** 
     * authors, compiles and loads this interface in an AdHocClassLoader 
     * and returns the Class.
     * 
     * @param adHocClassLoader the class loader to load the class
     * @return Class the class representing this interface
     */
    public Class loadClass( AdHocClassLoader adHocClassLoader )
    {
        return toJavaCase().loadClass( adHocClassLoader );
    }
    
	public String getFullyQualifiedClassName()
	{
		
		if( this.interfacePackage != null && ! this.interfacePackage.isEmpty() )
		{
			return this.interfacePackage.getName() + "." + this.interfaceSignature.getName();
		}
		else
		{
			return this.interfaceSignature.getName();
		}
	}
	
	public String author( Directive... directives ) 
	{
		//return toJavaCase( directives ).toString();
        return Author.code( INTERFACE, getContext(), directives);
	}

    
	public JavaCase toJavaCase( VarContext context, Directive...directives ) 
	{
        String FullClassName = this.getFullyQualifiedClassName();
        Dom classNameDom = BindML.compile( FullClassName );
        
        String theClassName = Author.code( classNameDom, context ); 
        
        System.out.println( theClassName );
        //first lets print out the structure and optional Marks
        String authored = JavaCase.of( 
            theClassName,
            INTERFACE, 
            getContext() ).toString();
        
        //now compile the marks and fill them in with the context
		return JavaCase.of(
			theClassName,
			BindML.compile( authored ), 
			context,
			directives );			
	}
	public String toString()
	{
		return author();
	}
	
	public Dom getDom() 
	{
		return INTERFACE;
	}
}
