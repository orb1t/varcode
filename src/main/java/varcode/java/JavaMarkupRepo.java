package varcode.java;

import varcode.markup.repo.ClassPathScannerRepo;
import varcode.markup.repo.DirectoryRepo;
import varcode.markup.repo.MarkupPath;
import varcode.markup.repo.MarkupRepo;

/**
 * Where/How to FIND the Java Markup Source Code 
 * to Compile into a {@code Dom}.
 * 
 * This provides some "conventional" places where the source code might be
 * (as it relates to System Properties, specifically "user.dir" and "markup.dir")
 *  
 * this uses "conventional" places where java source code exists
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaMarkupRepo
    implements MarkupRepo
{
    INSTANCE;
    
    public static final DirectoryRepo MARKUP_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/markup/" );
    
    public static final DirectoryRepo SRC_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/" );
    
    public static final DirectoryRepo SRC_MAIN_JAVA_DIRECTORY = 
       new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/main/java/" );
    
    public static final DirectoryRepo TEST_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/test/" );
    
    public static final DirectoryRepo SRC_TEST_JAVA_DIRECTORY = 
        new DirectoryRepo( System.getProperty( "user.dir" ) + "/src/test/java/" );
    
    public static final ClassPathScannerRepo CLASSPATH_REPO = 
        new ClassPathScannerRepo();    
    /**
     * Where to look for Java Source that Corresponds to a specific class 
     */
    public static final MarkupPath SOURCE_PATH = 
       new MarkupPath( 
           MARKUP_DIRECTORY, 
           SRC_DIRECTORY, 
           SRC_MAIN_JAVA_DIRECTORY, 
           TEST_DIRECTORY,
           SRC_TEST_JAVA_DIRECTORY,
           CLASSPATH_REPO    
       );

    private static MarkupStream fromSystemProperty( 
        String systemProperty, String markupId )
    {
        
        String markupDir = System.getProperty( systemProperty );
        if( markupDir != null )
        {
            //Here I need to be able to parse with ";" separators
            // so I can test each path 
            // (I can set something like... Also i need to be able to
            // load from .jar files or .zip files that are directly
            // in the path (if the thing is not a directory)
            DirectoryRepo dr = new DirectoryRepo( markupDir );
            MarkupStream ms = dr.markupStream( markupId );
            if( ms != null )
            {
                return ms;
            }        
        }
        return null;
    }
    /**
     * 
     * @param markupId the Id of the Markup source to load
     * @return MarkupStream for reading the input
     */
    @Override
    public MarkupStream markupStream( String markupId )
    {
        MarkupStream ms = fromSystemProperty( "markup.dir", markupId );
        if( ms != null )
        {
            return ms;
        }
        ms = fromSystemProperty( "java.source.path", markupId );
        if( ms != null )
        {
            return ms;
        }
        /*
        String markupDir = System.getProperty( "markup.dir" );
        if( markupDir != null )
        {
            DirectoryRepo dr = new DirectoryRepo( markupDir );
            MarkupStream ms = dr.markupStream( markupId );
            if( ms != null )
            {
                return ms;
            }
        }
        */
        return SOURCE_PATH.markupStream( markupId );
    }

    /** 
     * given the Class looks in the "usual places" on the Path
     * to return the Source markup Stream 
     * @param localClass the local Class
     * @return the markupStream
     */
    public MarkupStream markupStream( Class<?> localClass )
    {
        //need to check if it's a member class 
        if ( localClass.isMemberClass() )
        {
            // at the moment any Member class just returns the Declaring Classes
            // full source code
            return markupStream( 
                localClass.getDeclaringClass().getCanonicalName() + ".java" ); 
        }
        else
        {
            return markupStream( localClass.getCanonicalName() + ".java" );
        }
    }
        
    public String describe()
    {
        return SOURCE_PATH.describe();
    } 
}
