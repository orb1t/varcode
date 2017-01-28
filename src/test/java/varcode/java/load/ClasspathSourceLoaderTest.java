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
package varcode.java.load;

import varcode.java.load.ClassPathSourceLoader;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author eric
 */
public class ClasspathSourceLoaderTest
    extends TestCase
{
    //this assumes the source code is ON the classpath
    /*
    public void testReadJavaSourceForClass()
    {
        ClasspathScannerRepo csr = new ClasspathScannerRepo();
        MarkupStream ms = csr.loadMarkup( VarException.class );
        assertTrue( ms != null );
    }
    */
    
    public void testNothing()
    {
        ClassPathSourceLoader csr = new ClassPathSourceLoader();
        SourceStream ms = csr.sourceStream("THISDOES NOT EXIST.java");
        assertTrue( ms == null );
    }
    
    /* Commenting this out, because it depends on your environment
    public void testLog4J()
    {
        ClassPathScannerRepo csr = new ClassPathScannerRepo();
        MarkupStream ms = csr.markupStream( "log4j.properties" );
        assertTrue( ms != null );
    }
    */
}