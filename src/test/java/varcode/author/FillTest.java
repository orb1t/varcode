/*
 * Copyright 2017 Eric.
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
package varcode.author;

import varcode.markup.Fill;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class FillTest
    extends TestCase
{
        
    public void testAuthorSerial()
    {
        //a capital letter
        assertEquals( "A", Fill.of( "{+A+}", "a" ));         
        assertEquals( "A", Fill.of( "{+Z+}", "a" )); 
        assertEquals( "Ayyy", Fill.of( "{+Z+}", "ayyy" )); 
        
        assertEquals( "count", Fill.of( "{+name+}", "count" ));         
        assertEquals( "Count", Fill.of( "{+Name+}", "count" )); 
        assertEquals( "COUNT", Fill.of( "{+NAME+}", "count" ));         
    }
    
}