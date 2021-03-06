/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.author.lib;

import java.lang.reflect.Array;
import java.util.Collection;

import varcode.context.Context;
import varcode.context.VarScript;
//import varcode.translate.JSArrayToArrayTranslate;

/**
 * Given a Java Object or primitive
 *
 * @author eric
 */
public enum PrintAsLiteral
    implements VarScript
{
    //simple list of elements                "1,2,3,4"
    COMMA_SEPARATED_LIST( false ),

    //this will encase the data in { }'s so "{1,2,3,4}"
    // for use in initialization, assignment i.e. int[] arr = {1,2,3,4};
    USE_ARRAY_NOTATION( true );

    private final boolean useArrayNotation;

    private PrintAsLiteral( boolean useArrayNotation )
    {
        this.useArrayNotation = useArrayNotation;
    }

    @Override
    public String eval( Context varContext, String varName )
    {
        return printAsLiteral( varContext.resolveVar( varName ), this.useArrayNotation );
    }

    public static String printAsLiteral( Object object )
    {
        return printAsLiteral( object, false );
    }

    /**
     * Given an Object, print it as a literal
     *
     * This includes:
     *
     * @param object
     * @param useArrayNotation
     * @return
     */
    public static String printAsLiteral( Object object, boolean useArrayNotation )
    {
        if( object == null )
        {
            return "null";
        }
        /*
        //Javascript array
        if( jdk.nashorn.api.scripting.ScriptObjectMirror.class.isAssignableFrom(
            object.getClass() ) )
        {
            return printAsLiteral(
                JSArrayToArrayTranslate.getJSArrayAsObjectArray( object ),
                useArrayNotation );
        }
        */
        //collection
        if( Collection.class.isAssignableFrom( object.getClass() ) )
        {
            Collection<?> coll = (Collection<?>)object;
            Object[] arr = coll.toArray( new Object[ 0 ] );

            return printAsLiteral( arr, useArrayNotation );
        }
        //
        if( object.getClass().isArray() )
        {
            StringBuilder sb = new StringBuilder();
            if( useArrayNotation )
            {
                sb.append( "{" );
            }
            for( int i = 0; i < Array.getLength( object ); i++ )
            {
                if( i > 0 )
                {
                    sb.append( ", " );
                }
                sb.append( printAsLiteral( Array.get( object, i ), useArrayNotation ) );
            }
            if( useArrayNotation )
            {
                sb.append( "}" );
            }

            return sb.toString();
        }

        if( object instanceof Number )
        {
            if( object instanceof Integer )
            {
                return object.toString();
            }
            if( object instanceof Long )
            {
                return object.toString() + "L";
            }
            if( object instanceof Byte )
            {
                return "(byte)" + object.toString();
            }
            if( object instanceof Short )
            {
                return "(short)" + object.toString();
            }
            if( object instanceof Float )
            {
                return object.toString() + "f";
            }
            if( object instanceof Double )
            {
                return object.toString() + "d";
            }
        }
        if( object instanceof Boolean )
        {
            return object.toString();
        }
        if( object instanceof Character )
        {
            return "'" + EscapeString.escapeJavaString( ((Character)object).toString() ) + "'";
        }
        if( object instanceof String )
        {
            return "\"" + EscapeString.escapeJavaString( (String)object ) + "\"";
        }
        return "\"" + object.toString() + "\"";
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
