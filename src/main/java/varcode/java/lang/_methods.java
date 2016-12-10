package varcode.java.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.doc.translate.JavaTranslate;
import varcode.markup.bindml.BindML;

/**
 * Grouping of methods belonging to an entity (class, enum, interface)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _methods
    implements JavaMetaLang
{        
    private Map<String, List<_method>>methodsByName = 
	new HashMap<String, List<_method>>();
	
    public _method getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            String[] names = getNames();
            int idx = 0;
            for( int i = 0 ; i < names.length; i++ )
            {
                List<_method>byName = getByName( names[ i ] );
                if( ( byName.size() + idx ) > index )
                {
                    int offset = index - idx; 
                    return byName.get( offset );
                }
                idx += byName.size();
            }
        }
        throw new ModelException(
            "unable to get method at [" + index + "], out of range" );
    }
    
    public static _methods cloneOf( _methods prototype )
    {
	_methods m = new _methods();
        String[] methodNames = 
            prototype.methodsByName.keySet().toArray( new String[ 0 ] );
        for( int i = 0; i < methodNames.length; i++)
	{
            List<_method> methodsWithName = 
		prototype.methodsByName.get( methodNames[ i ] );
			
            for( int j = 0; j < methodsWithName.size(); j++ )
            {
		m.addMethod( _method.cloneOf( methodsWithName.get( j ) ) );				
            }			
	}
	return m;
    }
	
    public _methods()
    {				
    }

    public static final Dom METHODS = BindML.compile( 
	"{{+?staticMethods:" + N + "{+staticMethods+}+}}" + 
	"{{+?nonStaticMethods:" + N + "{+nonStaticMethods+}+}}" +
	"{{+?abstractMethods:" + N + "{+abstractMethods+};+}}" );
	
    public String[] getNames()
    {
        return methodsByName.keySet().toArray( new String[ 0 ] );
    }     
    
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
        
    @Override
    public String author( Directive... directives ) 
    {
	List<_method>nonStaticMethods = new ArrayList<_method>();
	List<_method>staticMethods = new ArrayList<_method>();
	List<_method._signature>abstractMethods = new ArrayList<_method._signature>();
		
	String[] methodNames = methodsByName.keySet().toArray( new String[ 0 ] );
		
	for( int i = 0; i < methodNames.length; i++ )
	{
            List<_method> oMethods = methodsByName.get( methodNames[ i ] );
            for( int j = 0; j < oMethods.size(); j++ )
            {
		if( oMethods.get( j ).signature.modifiers.contains( Modifier.ABSTRACT ) )
		{
                    abstractMethods.add(oMethods.get( j ).signature );
		}					
		else if( oMethods.get( j ).signature.modifiers.contains( Modifier.STATIC ) )
		{
                    staticMethods.add( oMethods.get( j ) ); 
		}
		else
		{
                    nonStaticMethods.add( oMethods.get( j ) );
		}
            }			
	}
	return Compose.asString( 
            METHODS, 
            VarContext.of( 
            "staticMethods", staticMethods,
            "nonStaticMethods", nonStaticMethods,
            "abstractMethods", abstractMethods ), 
            directives );		
    }
	
    @Override
    public String toString()
    {
	return author();
    }
	
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
    public int count()
    {
	return methodsByName.size();
    }
	
    public _methods addMethod( _method method )
    {
	verifyAndAddMethod( method );
	return this;
    }
	
    public _methods addMethod( String signature )
    {
	return addMethod( signature, (Object[])null );
    }
	
    public _methods addMethod( String signature, Object... body )
    {
	_method _m = _method.of( (_javadoc)null, signature, (Object[]) body );
	verifyAndAddMethod( _m );
	return this;
    }
	
    /** returns all of the methods by the name
     * @param name the name of the method
     * @return all methods with this name 
     */
    public List<_method> getByName( String name )
    {
        return this.methodsByName.get( name );
    }
    
    private void verifyAndAddMethod( _method m )
    {
	List<_method> methodsWithTheSameName = 
            methodsByName.get( m.signature.methodName );
	if( methodsWithTheSameName == null )
	{
            List<_method> methods = new ArrayList<_method>();
            methods.add( m );
            methodsByName.put( m.signature.methodName, methods );
	}
	else
	{   //removed check for method of the same name... it's actually
            // a little involved, and it was generating false positives (ouch)
            // this seems to be a job the Javac compiler will help with anyways
            methodsWithTheSameName.add( m );
	}
    }

    @Override
    public _methods replace( String target, String replacement )
    {
        Map<String, List<_method>> replacedMethods = 
            new HashMap<String, List<_method>>();
        
        String[] names = this.methodsByName.keySet().toArray( new String[ 0 ] );        
        for( int i = 0; i < names.length; i++ )
        {
            List<_method> methods = this.methodsByName.get( names[ i ] );
            
            for( int j = 0; j < methods.size(); j++  )
            {
                _method thisOne = methods.get( j );
                thisOne.replace( target, replacement );
                List<_method> ex = 
                    replacedMethods.get( thisOne.getName() );
                if( ex == null )
                {
                    ex = new ArrayList<_method>();        
                    replacedMethods.put( thisOne.getName(), ex );
                }
                ex.add( thisOne );
            }
        }
        this.methodsByName = replacedMethods;
        return this;
    }
    
    @Override
    public _methods bind( VarContext context )
    {
        Map<String, List<_method>> replacedMethods = 
            new HashMap<String, List<_method>>();
        
        String[] names = this.methodsByName.keySet().toArray( new String[ 0 ] );        
        for( int i = 0; i < names.length; i++ )
        {
            List<_method> methods = this.methodsByName.get( names[ i ] );
            
            for( int j = 0; j < methods.size(); j++  )
            {
                _method thisOne = methods.get( j );
                thisOne.bind( context );
                List<_method> ex = 
                    replacedMethods.get( thisOne.getName() );
                if( ex == null )
                {
                    ex = new ArrayList<_method>();        
                    replacedMethods.put( thisOne.getName(), ex );
                }
                ex.add( thisOne );
            }
        }
        this.methodsByName = replacedMethods;
        return this;
    }

    public _methods addMethods( _method...methods )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            addMethod( methods[ i ] );
        }
        return this;
    }
    
    public _methods addMethods( _methods methods )
    {
        String[] methodNames = methods.getNames();
        
        for( int i = 0; i < methodNames.length; i++ )
        {
            List<_method> byName = methods.getByName( methodNames[ i ] );
            for( int j = 0; j < byName.size(); j++ )
            {
                this.addMethod( byName.get( i ) );
            }
        }
        return this;            
    }
    
    /** model of a method */
    public static class _method		
        implements JavaMetaLang, _facet
    {              
	public static final Dom METHOD = 
            BindML.compile(
		"{+javadocComment+}" +	
                "{+methodAnnotations+}" +        
		"{+methodSignature*+}" + N +
		"{" + N +
		"{+$indent4Spaces(methodBody)+}" + N +
		"}" );

        //abstract, native methods with no body
	public static final Dom NO_BODY_METHOD = 
            BindML.compile(
            "{+javadocComment+}" +
            "{+methodAnnotations+}" +    
            "{+methodSignature*+};" + N );
	
    
        /**
         * Holds builder params to simplify construction / initialization
         * (we use the static of(...) "builder" method.)
         */
        private static class MethodParams
        {
            _javadoc javadoc; // starts with /* ends with */
            _annotations annots = new _annotations(); //starts with @
            String signature;
            List<Object> body = new ArrayList<Object>(); //anything AFTER signature is populated
        }
    
        
        private static void addBody( MethodParams mp, Object body )
        {
            if( body.getClass().isArray() )
            {            
                for( int i = 0; i < Array.getLength( body ); i++ )      
                {
                    mp.body.add( Array.get( body, i ) );
                }
            }
            else if( body.getClass().isAssignableFrom( List.class ) )
            {
                List lBody = (List) body;
                mp.body.addAll( lBody );
            }
            else
            {
                mp.body.add( body );
            }        
        }
    
        /**
         * A flexible fluent API for allowing the client 
         * to create _methods i.e.<PRE>
         * public static final _method _m = _method.of( 
         *     "/*comment* /",  //provide the javadoc comment for the method
         *     "@Deprecated",   //provide one or more annotations to the method
         *     "public static final String createId()", //the method signature
         *     "return UUID.randomUUID().toString();"   //the method body
         * );
         * 
         * //the idea is to provide the ability to declare the _method 
         * (metalanguage model) as succinctly as possible.
         * 
         * @param parts the parts of a method 
         * @return a _method
        */
        public static _method of( Object...parts )
        {
            MethodParams mp = new MethodParams();
            for( int i = 0; i < parts.length; i++ )
            {
                if( parts[ i ] == null )
                {
                    continue;
                }
                else if( mp.signature != null )
                {   //ANYTHING i pass in AFTER the signature
                    // is assumed to be the BODY of the method
                    addBody(mp, parts[ i ] );
                }
                else if( parts[ i ] instanceof String )
                {
                    partFromString(mp, (String)parts[ i ] );
                }            
                else if( parts[ i ] instanceof _javadoc )
                {
                    mp.javadoc = (_javadoc)parts[ i ];
                }
                else if( parts[ i ] instanceof _annotations._annotation )
                {
                    mp.annots.add((_annotations._annotation)parts[ i ] );
                }
                else if( parts[ i ] instanceof _annotations )
                {
                    mp.annots = (_annotations)parts[ i ];
                }            
            }
        
            _method _f = new _method( mp.signature );
            for( int i = 0; i < mp.annots.count(); i++ )
            {
                _f.annotate( mp.annots.getAt( i ) );
            }
            if( mp.javadoc != null && !mp.javadoc.isEmpty())
            {
                _f.javadoc(  mp.javadoc.getComment() );
            }        
            if( mp.body != null && !mp.body.isEmpty() )
            {
                _f.body( mp.body.toArray() );
            }
            return _f;
        }
    
        private static void partFromString( MethodParams mp, String component )
        {
            if( component.startsWith( "/**" ))
            {
                mp.javadoc = _javadoc.of( component.substring( 3, component.length() -2 ) );            
            }
            else if( component.startsWith( "/*" ))
            {
                mp.javadoc = _javadoc.of( component.substring( 2, component.length() -2 ) );            
            }
            else if( component.startsWith( "@" ) )
            {
                mp.annots.add( _annotations._annotation.of( component ) );
            }        
            else
            {
                mp.signature =  (String)component;             
            }        
        }

        public String getName()
        {
            return this.signature.getName();
        }
        
        public _method setBody( Object...body )
        {
            this.methodBody = _code.of( body );
            return this;
        }
        
        public _annotations getAnnotations()
        {
            return this.annotations;
        }
        
        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }
        
	public static _method cloneOf( _method prototype ) 
	{
            _method m = 
		new _method( _signature.cloneOf( prototype.signature ) );
                    m.javadoc = _javadoc.cloneOf(prototype.javadoc );
                    m.methodBody = prototype.getBody();			
                    m.annotations = new _annotations( prototype.annotations.getAnnotations() );
		return m;
	}

	public _signature getSignature()
	{
            return signature;
	}
		
        /*
        public static _method of( _javadoc javadoc, String signature, Object...body )
        {
            _method m = new _method( signature );
            if( body != null && body.length > 0 )
            {
                m.body( body );
            }
            if( javadoc != null )
            {
                m.javadoc( javadoc );
            }
            return m;
        }
        */
        /*
	public static _method of( String comment, String signature, Object... body )
	{
            _method m = new _method( signature );
            if( body != null && body.length > 0 )
            {
                m.body( body );
            }			
            if( comment != null && comment.trim().length() > 0 )
            {
		m.javadoc( comment );
            }			
            return m;
	}
        */

	private _javadoc javadoc;
        private _annotations annotations;
	private _signature signature;
	private _code methodBody;
	
        public boolean isAbstract()
	{
            return this.signature.modifiers.containsAny( Modifier.ABSTRACT );
	}
		
	public _method( 
            _modifiers modifiers, 
            String returnType, 
            String methodName, 
            _parameters params,
            _throws throwsExceptions )
	{
            this( new _signature( modifiers, returnType, methodName, params, throwsExceptions ) );		            
	}
	
	public _method( _signature sig )
	{
            this.signature = sig;
            this.annotations = new _annotations();
            this.methodBody = new _code();
            this.javadoc = new _javadoc();
	}
		
	public _method( String methodSignature, Object...bodyLines )
	{
            this( _signature.of( methodSignature ) );
            this.methodBody = _code.of( bodyLines );
	}
	
	public _code getBody()
	{
            return this.methodBody;
	}
		
        public _method annotate( Object...annotations )
        {
            this.annotations.add( annotations );
            return this;
        }
        
        private _method body( _code body )
        {
            this.methodBody = body;
            return this;
        }
        
        /** Add code to the end (tail) of the current body of code 
         * @param code code to append
         * @return this
         */
        public _method addToBody( Object... code )
        {
            this.methodBody.addTailCode( code );
            return this;
        }
        
	public _method body( Object... linesOfCode )
	{
            if( this.isAbstract() && methodBody != null 
                    && linesOfCode != null 
                    && linesOfCode.length > 0 )
            {
		throw new ModelException(
                    "Abstract methods : "+ N + signature + N 
                  + "cannot have a method body" );
            }
            this.methodBody = _code.of( linesOfCode );
            return this;
	}
	
        public _method javadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }
        
	public _method javadoc( String javadocComment )
	{            
            this.javadoc = new _javadoc( javadocComment );
            return this;
	}
	
        @Override
	public String toString()
	{
            return author();
	}

        public VarContext getContext()
        {
            return VarContext.of( "javadocComment", javadoc,
                "methodAnnotations", annotations,    
                "methodSignature", signature,                        
		"methodBody", methodBody );
        }
        
        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
        public String author( Directive... directives ) 
	{            
            if( this.methodBody == null || this.methodBody.isEmpty() )
            {
                return Compose.asString( 
                    NO_BODY_METHOD, 
                    getContext(),
                    directives );
            }
            return Compose.asString( METHOD, 
		getContext(),
		directives );
        }
		
        public String getReturnType()
        {
            return this.signature.getReturnType();
        }
                
        public _modifiers getModifiers()
        {
            return this.signature.getModifiers();
        }
        
        public _parameters getParameters()
        {
            return this.signature.getParameters();
        }
        
        public _throws getThrows()
        {
            return this.signature.getThrows();
        }

        public _method setReturnType( String returnType ) 
        {
            this.signature.returnType = returnType;
            return this;
        }
        
	public static class _signature
            implements JavaMetaLang
        {                    
            public static _signature cloneOf( _signature prototype )
            {
		return new _signature(
                    _modifiers.cloneOf( prototype.modifiers),
                    prototype.returnType + "",
                    prototype.methodName + "",
                    _parameters.cloneOf( prototype.params ),
                    _throws.cloneOf( prototype.throwsExceptions )
		);
            }
            
            private _modifiers modifiers;
            private String returnType;
            private String methodName;
            private _parameters params;
            private _throws throwsExceptions;
	
            public _signature(
		_modifiers modifiers, 
                String returnType, 
                String methodName, 
                _parameters params,
		_throws throwsExceptions )
            {
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.methodName = methodName;
                this.params = params;
		this.throwsExceptions = throwsExceptions;
            }
	
            public _signature setName( String name )
            {
                this.methodName = name;
                return this;
            }
            
            public _signature setReturnType( Object returnType )
            {
                if( returnType == null )
                {
                    this.returnType = "void";
                }
                else
                {
                    this.returnType = JavaTranslate.INSTANCE.translate( returnType );
                }
                return this;
            }
            
            public _signature setModifiers( _modifiers mods )
            {
                this.modifiers = mods;
                return this;
            }
            
            public _signature setThrows( Object throwsException )
            {
                this.throwsExceptions = _throws.of( throwsException );
                return this;
            }
            
            public _signature setThrows( _throws throwsExceptions )
            {
                this.throwsExceptions = throwsExceptions;
                return this;
            }
            
            public _modifiers getModifiers()
            {
		return this.modifiers;
            }
			
            public String getReturnType()
            {
		return returnType;
            }
            
            @Override
            public _signature replace( String target, String replacement )
            {
                this.returnType = this.returnType.replace( target, replacement );
                this.params.replace( target, replacement ); 
                this.modifiers.replace( target, replacement );
                this.methodName = this.methodName.replace( target, replacement );
                this.throwsExceptions.replace(target, replacement);
                return this;
            }
            
            @Override
            public _signature bind( VarContext context )
            {
                this.methodName = Compose.asString( 
                BindML.compile( this.methodName ), 
                    context );
                                
                this.returnType = Compose.asString( 
                    BindML.compile( this.returnType ), 
                    context );
                this.params.bind( context ); 
                this.modifiers.bind( context );
                
                this.throwsExceptions.bind( context );
                return this;                
            }
            
            public String getName()
            {
		return methodName;
            }
			
            public _parameters getParameters()
            {
		return params;
            }
			
            public _throws getThrows()
            {
		return this.throwsExceptions;
            }
			
            public static _signature of( String methodSpec )
            {
		methodSpec = methodSpec.trim();
		
		// Get the parameters
		int openParenIndex = methodSpec.indexOf( "(" );

		int closeParenIndex = methodSpec.lastIndexOf( ")" );

                _parameters params = new _parameters( );
                if( openParenIndex < 0 && closeParenIndex < 0 )
                {
                    //no parameters
                    openParenIndex = methodSpec.length();
                    closeParenIndex = methodSpec.length();
                }                
                else
                {
                    String paramInside = methodSpec.substring( 
                        openParenIndex + 1 , closeParenIndex );
                    
                    if( paramInside.trim().length() > 0 )
                    {
                        params = _parameters.of( paramInside  ); 
                    }
                }
		
		String sig = methodSpec.substring( 0, openParenIndex );
			
		String[] tokens = sig.split( " " );
	
		_throws throwsExceptions = _throws.NONE;
				
                if( openParenIndex != closeParenIndex)
                {
                    String throwsTokens = methodSpec.substring( closeParenIndex + 1 ).trim();
                    if( throwsTokens.length() > 1 )
                    {
                        if( ! throwsTokens.startsWith( "throws" ) )
                        {
                            throw new VarException( methodSpec );
                                /*
                                "Tokens found after method parameters() in:" + N +
                                methodSig + N 
                                " only \"throws\" allowed, got \"" + throwsTokens + "\"" ); */
                        }
                        throwsTokens = throwsTokens.substring( "throws".length() );
					
                        String[] throwsTokensStrings = _var.normalizeTokens( throwsTokens );
			
                        if( throwsTokensStrings.length > 0 )
                        {
                            throwsExceptions = _throws.of( (Object[])throwsTokensStrings );
                        }			
                    }   
                }
                String methodName = tokens[ tokens.length - 1 ];
                String returnType = "void";
                        
                if( tokens.length >= 2 )
                {
                    returnType = tokens[ tokens.length - 2 ];
                }
                //String 
		_modifiers mods = new _modifiers();
		if( tokens.length > 2 )
		{
                    String[] modi = new String[ tokens.length - 2 ];
                    System.arraycopy( tokens, 0, modi, 0, modi.length );
                    mods = _modifiers.of( modi ); 
		}
			
		if( mods.containsAny( Modifier.TRANSIENT, Modifier.VOLATILE ) )
		{
                    throw new ModelException(
			"Invalid Modifiers for method; (cannot be transient or volatile)" );
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.FINAL ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and final )" );
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.NATIVE ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and native )" );				
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.PRIVATE ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and private )" );				
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.STATIC ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and static )" );				
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.STRICT ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and strictfp )" );				
		}
		if( mods.containsAll( Modifier.ABSTRACT, Modifier.SYNCHRONIZED ) )
		{
                    throw new ModelException( 
			"Invalid Modifiers for method; (cannot be BOTH abstract and synchronized )" );				
		}
		return new _signature( 
                    mods, returnType, methodName, params, throwsExceptions );			
            }
	
            /**
             * Does this method signature "match" this other method signature?
             * 
             * @param sig the signature
             * @return true if the signatures match (meaning these two methods
             * cannot exist on the same (enum, class)
             */
            public boolean matchesSignature( _signature sig )
            {
		if( sig.methodName.equals( this.methodName ) )
		{
                    if( sig.params.count() == this.params.count() )
                    {
			for( int i = 0; i < sig.params.count(); i++ )
			{
                            //System.out.println( "TYPE 1:"+sig.params.getAt( i ).toString() );
                            //System.out.println( "TYPE 2:"+this.params.getAt( i ).toString() );
                            if( !sig.params.getAt( i ).getType().equals( 
				this.params.getAt( i ).getType() ) )
                            {
				return false;
                            }
                        }
                    	return true;
                    }
                    return false;
		}
		return false;
            }
		
            public static final Dom METHOD_SIGNATURE = 
		BindML.compile(
                    "{+modifiers+}{+returnType+} {+methodName+}{+params+}{+throwsExceptions+}");
	
            @Override
            public String author( )
            {
                return author( new Directive[ 0 ] );
            }
        
            @Override
            public String author( Directive... directives ) 
            {
		return Compose.asString( METHOD_SIGNATURE, 
                    VarContext.of(
			"modifiers", modifiers,
			"returnType", returnType,
			"methodName", methodName,
			"params", params,
			"throwsExceptions", throwsExceptions ),
			directives );
            }
	
            @Override
            public String toString()
            {
		return author();
            }		
	}
        
	/** searches through the contents to find target and replaces with replacement */
        @Override
	public _method replace( String target, String replacement ) 
	{
            this.javadoc.replace( target, replacement );
            
            this.annotations.replace( target, replacement );
            this.methodBody.replace( target, replacement );            
            this.signature.replace( target, replacement );
            
            return this;
	}	
        
        @Override
        public _method bind( VarContext context )
        {
            this.javadoc.bind( context );
            this.annotations.bind( context );
            this.methodBody.bind( context );
            this.signature.bind( context );
            return this;
        }
    }
}
