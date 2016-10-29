package tutorial.varcode.chap1.author;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.code._class;
import varcode.java.code._interface;

/**
 *
 * @author Eric DeFazio
 */
public class _5_NestedClass 
    extends TestCase
{
    public static void main( String[] args )
    {
        new _5_NestedClass().testInterfaceWithNestedClass();
    }
    
    public void testInterfaceWithNestedClass()
    {
        _interface _genId = _interface.of( "tutorial.varcode.chap1.author", 
            "public interface GenId extends Serializable" )
            .defaultMethod( "public default String genId()",
                "return UUID.randomUUID().toString();" )
            .imports( UUID.class, Serializable.class );
        
        _class _genIdImpl = _class.of( "it.doesnt.matter",
            "public class GenIdImpl implements GenId" )
            .field("public static final GenDate = new Date();")
            .imports( Date.class );
        
        //an interface with a nested class (impl)
        _interface genId = _interface.cloneOf( _genId ); 
        genId.nest( _genIdImpl );
        
        System.out.println( genId );
        Class interfaceClass = 
            genId.toJavaCase( ).loadClass();
        
        Class nestedImplClass = 
            interfaceClass.getDeclaredClasses()[ 0 ];
        
        //create an instance of the nested class
        Object instance = Java.instance( nestedImplClass );
        
        //call the genId() default method on the nested Impl class
        String id = (String)Java.invoke( instance, "genId" );
        
        assertNotNull( id );
        System.out.println( id );
    }
    // concepts
    // 1) defaultMethod(...) creates a default method on an interface, to call
    //    a defaultMethod you must have an implementation class, and compile
    //    with java 8+ compatibility
    // 2) .cloneOf() creates and mutable copy of an _interface/_class/_enum
    // 3) .nest() accepts any _class/_interface/ _enum model. 
    //     the "package declaration" of the nest is repressed, 
    //     any imports for a nest (or any nested decendents) are aggregated 
    //     at the top level _class/_interface or _enum             
}