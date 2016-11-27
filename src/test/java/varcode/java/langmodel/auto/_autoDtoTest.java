/*
 * Copyright 2016 eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.langmodel.auto;

import java.math.BigDecimal;
import java.util.Map;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.JavaCase;

/**
 *
 * @author eric
 */
public class _autoDtoTest
    extends TestCase
{
    
    //tests the easy one-liner DT
    public void testOneStatement()
    {
        Object instance = 
            _autoDto.of("ex.varcode.dto.FluentDto")
                .property( String.class, "name" )
                .property( int.class, "age")
                .property( String[].class, "aliases")
                .instance();
        
    }
    
    public void testAutoDto()
    {
        _autoDto d = _autoDto.of( "MyDto" );
        Object o = d.toJavaCase( ).instance();
        assertEquals( o.getClass().getName(), "MyDto" );
        
        
        d = _autoDto.of("ex.varcode.dto.MyDto");
        o = d.toJavaCase( ).instance();
        
        assertEquals( "MyDto", o.getClass().getSimpleName() );
        assertEquals( "ex.varcode.dto.MyDto", o.getClass().getCanonicalName() );
        
        d.property( "public String name;" );
        
        o = d.toJavaCase( ).instance();
        
        assertEquals( null, _Java.invoke( o, "getName" ) );
        _Java.invoke(o, "setName", "A");
        assertEquals( "A", _Java.invoke( o, "getName" ) );
        
        
        d.property( int.class, "count" );
        o = d.toJavaCase( ).instance();
        
        _Java.invoke(o, "setCount", 5 );
        assertEquals( 5, _Java.invoke( o, "getCount" ) );
        
        //make sure when I create a field whos type requires an import it
        //works
        d.property( BigDecimal.class, "value" );
        o = d.toJavaCase( ).instance();
        
        _Java.invoke( o, "setValue", new BigDecimal( Math.PI ) );
        assertEquals( new BigDecimal( Math.PI ), _Java.invoke( o, "getValue" ) );        
    }
    
    //verify that _autoDto works when dealing with final fields
    // (i.e. final fields that have no init() must be passed in via 
    // the constructor
    public void testFinals()
    {
        _autoDto d = _autoDto.of("ex.varcode.dto.MyDto")
            .imports( Map.class)    
            .property( "public String name;" )
            .property( "private final int count = 100;" )
            .property( "public final int id;" )
            .property( "public Map<String,Integer> nameToCount;" );
        
        JavaCase jc = d.toJavaCase( );
        
        //System.out.println( jc );
        Object myVoInstance = jc.instance( 12345 );
        assertEquals( null, _Java.invoke( myVoInstance, "getName" ) );
        assertEquals( 100, _Java.invoke( myVoInstance, "getCount" ) );
        assertEquals( 12345, _Java.invoke( myVoInstance, "getId" ) );
    }
    
}
