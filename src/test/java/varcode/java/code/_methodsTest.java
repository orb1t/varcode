/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import junit.framework.TestCase;
import varcode.CodeAuthor;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.code._methods._method;
import varcode.java.code._methods._method.signature;

/**
 *
 * API is it "structurally correct"?
 * is it "logically correct"? (we'll use the compiler for this)
 * does it do what I want (well, test it)
 * 
 * @author eric
 */
public class _methodsTest
    extends TestCase
{
    public void testNoMethods()
    {
        _methods m = new _methods();
        assertEquals( 0, m.count() );
        assertEquals( null, m.getMethodsByName("anything") );
        assertTrue( m.isEmpty() );
        m.replace("A", "Z");
    }

    public void testOneReplace()
    {
        _methods m = new _methods();
        m.addMethod("public abstract int methodName()");
        
        assertEquals( 1, m.count() );
        assertEquals( 1, m.getMethodsByName( "methodName" ).size() );
        assertFalse( m.isEmpty() );
        m.replace( "methodName", "blahName" );
        
        //verify that if I change the name of a method it works
        assertEquals( 1, m.count() );
        assertEquals( 1, m.getMethodsByName( "blahName" ).size() );
        assertFalse( m.isEmpty() );
        
    }
    
    public static final String N = "\r\n";
    
    public void testParameterizedMethodName()
    {        
        _methods m = new _methods();
        m.addMethod( "public String {+methodName+}()" );
        String res = m.bind( VarContext.of("methodName", "M") );
        System.out.println( res );
        assertEquals( "public String M(  )" + N + "{"+ N + N + "}", res.trim() );        
    }
    
    public void testParameterizedReturnType()
    {        
        _methods m = new _methods();
        m.addMethod( "public {+returnType+} method()" );
        String res = m.bind( VarContext.of( "returnType", "Gizmo") );
        System.out.println( res );
        assertEquals( "public Gizmo method(  )" + N + "{"+ N + N + "}", res.trim() );        
    }
    
    	public void testMethods()
	{
		_methods m = new _methods();
		
		m.addMethod( "public void setX( int x ) throws InvalidOperationException, BadThingsException", "this.x = x;" );
		m.addMethod( "protected void setY( int y )", "this.y = y;" );
		m.addMethod( "public int getY( )", "this.y = y;" );
		m.addMethod( "public static final String getID( )", "return UUID.randomUUID.toString();" );
		
		assertEquals( 4, m.count() );
		
		m.author( );
		
		//System.out.println( m );
		//System.out.println( m.toCode( m.INDENT ) );
	}
	

	public void testComplicatedParameterList()
	{
		_methods m = new _methods();
		m.addMethod( 
			"final SomeObject<String,UUID> methodName( Double d, int[] arr ) throws SomeException, AnotherException",
			"return new SomeObject<String,UUID>();" );
		m.addMethod(
			"protected synchronized Map<String,List<Integer>> someMethod() throws SomeException",
			"return null;" );
		
		// NOTE: this space causes a bug-----------|  fix later
		//m.addMethod(                             |
		//		"protected synchronized Map<String, List<Integer>> someMethod() throws SomeException",
		//		"return null;" );
				
		m.author( );
		//System.out.println( m );
	}
	
	public void testBadModifiers()
	{
		_methods m = new _methods();
		try
		{
			m.addMethod(
				"transient void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
				
		try
		{
			m.addMethod(
				"volatile void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
		
		try
		{
			m.addMethod(
				"public private void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
	
	public void testMethodMatches()
	{
		_methods m = new _methods();
		m.addMethod( "public void setX( int x )", "this.x = x;" );
		try
		{
			m.addMethod( "public void setX( int anyVar ) throws IOException", "doesnt matter should fail;" );
			fail( "expected exception" );
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
	public void testSignature()
	{
		assertEquals("void method(  )", 
			signature.of("void method()").toString() );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator() +
			"    throws IOException", 
		signature.of(
			"public static final void main( String[] args ) throws IOException" ).toString() );
		//System.out.println( 
		//	) );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator()+
			"    throws IOException, ReflectiveOperationException",
		signature.of(
			"public static final void main( String[] args ) "
			+ "throws IOException, ReflectiveOperationException" ).toString() );
	}

	
	public void testMethod()
	{
		_method m = 
			_method.of( "public final String getX()")
			.body( "return this.x;" );
		
		assertEquals(
			"public final String getX(  )" + System.lineSeparator()+ 
			"{" + N +
			"    return this.x;" + N +
			"}",
			m.toString() );
		
		String indented = m.author( CodeAuthor.INDENT );
		assertEquals(
		"    public final String getX(  )" + System.lineSeparator()+ 
		"    {" + N +
		"        return this.x;" + N +
		"    }",
		indented );
				
	}	
 
    //make sure there is no method body for abstract methods
    public void testAbstractMethods()
    {
        _methods m = new _methods();
        m.addMethod("public abstract int blah()");
        String res = m.author( );
        assertEquals("public abstract int blah(  );", res.trim());
    }
}