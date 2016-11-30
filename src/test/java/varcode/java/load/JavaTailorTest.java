package varcode.java.load;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;
import varcode.java.metalang._nests;

/**
 *
 * @author Eric DeFazio
 */
public class JavaTailorTest
    extends TestCase
{
    public static class MemberClass
    {
        
        public static class NestedClass
        {
            public static class DeepNest
            {
                
            }
        }
        
        public interface NestedInterface
        {
            
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testClassNesteds()
    {
        //read and parse a Member class that contains : 
        // NestedClass
        // NestedInterface
        // NestedEnum
        //_class _c = JavaLoad._classOf( MemberClass.class );
        _class _c = JavaMetaLangLoader._Class.from( MemberClass.class );
        _nests ng = _c.getNesteds();
        assertEquals( 3, ng.count() );
        _class _nc = (_class)_c.getNestedByName( "NestedClass");
        assertNotNull( _nc );
        assertNotNull( _c.getNestedByName("NestedInterface") );
        assertNotNull( _c.getNestedByName("NestedEnum") );                        
        assertNotNull( _nc.getNestedByName( "DeepNest" ) );
    }
    
    public interface MemberInterface
    {
        public static class NestedClass
        {
            
        }
        
        public interface NestedInterface
        {
            public static class DeepNest
            {
                
            }
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testInterfaceNesteds()
    {
        _interface _i = 
            //JavaLoad._interfaceOf( MemberInterface.class );
JavaMetaLangLoader._Interface.from( MemberInterface.class );
        _class _c = (_class)_i.getNesteds().getByName( "NestedClass" );
        _interface _ni = (_interface)_i.getNesteds().getByName( "NestedInterface" );
        _enum _e = (_enum)_i.getNestedByName( "NestedEnum" );
        
        List<String> names = new ArrayList<String>();
        _i.getAllNestedClassNames( names, _i.getFullyQualifiedClassName() );
        
        //System.out.println( MemberInterface.NestedClass.class.getName().replace( "." +this.getClass().getSimpleName(), "") );
        System.out.println( names );
        String packageName = this.getClass().getPackage().getName(); 
        assertTrue( names.contains( 
            packageName + ".MemberInterface$NestedClass" ) );
        assertTrue( names.contains( 
            packageName + ".MemberInterface$NestedInterface" ) );
        assertTrue( names.contains( 
            packageName + ".MemberInterface$NestedInterface$DeepNest" ) );
        assertTrue( names.contains( 
            packageName + ".MemberInterface$NestedEnum" ) );
        
        System.out.println( MemberInterface.class );
        System.out.println( MemberInterface.class.getName() );
        System.out.println( MemberInterface.class.getCanonicalName() );
               
    }
    
    enum MemberEnum
    {
        ;
            
        public static class NestedClass
        {
            
        }
        
        public interface NestedInterface
        {
            public static class DeepNest
            {
                
            }
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testEnumNesteds()
    {
        _enum _e = JavaMetaLangLoader._Enum.from( MemberEnum.class );
        //_enum _e = JavaLoad._enumOf( MemberEnum.class );
        
    }
}
