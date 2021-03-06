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
package varcode.markup;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import varcode.markup.FillInTheBlanks.BlankBinding;
import varcode.markup.form.Form;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.Bind;

/**
 * An Immutable {@code Template} that containing static text and {@link Mark}s 
 * that are dynamically evaluated and bound to produce a target document.
 *
 * <A HREF="https://en.wikipedia.org/wiki/Document_Object_Model">Document Object
 * Model</A>- like model from the compiled (<CODE>BindML, CodeML</CODE>) Markup.
 * It provides an API consisting of {@code Mark}s for manipulating the text of a
 * Document.
 * <UL>
 * <LI>BindML is a Markup Language for "Authoring" structured text
 * <CODE>"{+type+} {+name+}={+value+};"</CODE>
 * </UL>
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A>.
 * <BR><BR>
 *
 * Contains the static text (Code) along with {@code Mark}s to Specialize /
 * Tailor new source code.
 * <PRE><CODE>
 * Template template = BindML.compile(
 *     "public class {+className+} {}" );
 *
 * String tailored = Tailor.code( template,
 *     VarContext.of(
 *         "className", "MyClass" ) ); // = "public class MyClass {}";
 * </CODE></PRE>
 */
public class Template
{
    public static final Template EMPTY = textOnly( "" );
    

    /**
     * Builds and returns a Dom implementation that is "only" text... (no Marks)
     *
     * @param documentText the text contents of the document
     * @return a Dom document containing only text
     */
    public static Template textOnly( String documentText )
    {
        return new Template(
            FillInTheBlanks.of( documentText ),
            new Mark[ 0 ],
            new BitSet() ); //,VarContext.of() );
    }

    /**
     * ALL {@code Mark}s on the template
     */
    private final Mark[] marks;

    /**
     * Static Text and "blanks" where ALL {@code Mark}s occur (Useful if we want
     * to "derive" the original markup text {@code Mark}s)
     */
    private final BlankBinding marksBinding;

    /**
     * All Text and {@code MarkAction}s that Fill Blanks (with text) as they
     * appear in the var source to be populated when tailoring i.e.
     * <PRE>
     * I _____________________, do solemnly swear to tell the truth.
     *        (fullName)
     * </PRE>
     *
     * ...where "fullName" is the {@code BindMark}s' name".<BR>
     *
     * NOTE: MAY CONTAIN DUPLICATE NAMES, for instance, given the sequence:
     * <PRE>
     * _________, is the number of the counting, again the number is _______.
     *   count                                                        count
     * </PRE> the name "count" appears (2) times at [0] and at [1].
     */
    private final Mark.Bind[] blankFillMarks;

    /**
     * static text and "blanks" corresponding to the {@code BlankFiller}s
     */
    private final BlankBinding blankBinding;

    /**
     * Creates a {@code Dom} containing {@code Mark}s and text
     *
     * <UL>
     * <LI>{@code AddVar}
     * <LI>{@code AddScriptResult}
     * <LI>{@code ReplaceWithScriptResult}
     * <LI>{@code IfAdd}
     * <LI>{@code IfAddWithForm}
     * <LI>{@code Replace}
     * <LI>{@code ReplaceWithForm}
     * </UL>
     *
     * (Other {@code Mark}s like {@code Cut}, {@code CutComment} ,
     * {@code CutJavaDoc} contain information that is not included in the
     * tailored source.
     *
     * @param blankBinding {@code FillInTheBlanks.FillOrder} static text and
     * blanks in the document
     * @param marks the marks that occur within the document (in order)
     * @param allMarkLocations the set bits mark the character index of Marks    
     */
    public Template(
        FillInTheBlanks.BlankBinding blankBinding,
        Mark[] marks,
        BitSet allMarkLocations )
    {
        this.blankBinding = blankBinding;
        this.marks = marks;
        this.marksBinding
            = BlankBinding.of(blankBinding.getStaticText(),
                allMarkLocations );

        List<Bind> blankFillMarksList = new ArrayList<Bind>();
        for( int i = 0; i < marks.length; i++ )
        {
            if( (marks[ i ] instanceof Bind) )
            {
                blankFillMarksList.add( (Bind)marks[ i ] );
            }
        }
        this.blankFillMarks = blankFillMarksList.toArray( 
            new Mark.Bind[ blankFillMarksList.size() ] );
    }

    public Template(
        Mark[] marks,
        FillInTheBlanks.BlankBinding marksBinding,
        Mark.Bind[] fillMarks,
        FillInTheBlanks.BlankBinding fillMarksBinding ) 
    {
        this.blankBinding = fillMarksBinding;
        this.blankFillMarks = fillMarks;
        this.marksBinding = marksBinding;
        this.marks = marks;
    }

    public Mark[] getMarks()
    {
        return marks;
    }

    public Bind[] getBindMarks()
    {
        return blankFillMarks;
    }

    public int getBlanksCount()
    {
        return blankBinding.getBlanksCount();
    }

    public Form[] getForms()
    {
        List<Form> theForms = new ArrayList<Form>();
        for( int i = 0; i < this.marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasForm )
            {
                HasForm rf = (HasForm)marks[ i ];

                theForms.add( rf.getForm() );
            }
        }
        return theForms.toArray( new Form[ 0 ] );
    }

    /**
     * @return bitset that represents the character indexes of Marks
     * @see io.varcode.VarCodeMark#getAllMarkIndexes()
     */
    public BitSet getMarkIndicies()
    {
        return this.marksBinding.getBlanks();
    }

    /**
     * @return the document fill in template
     * @see io.varcode.VarCodeMark#getFillBlanks()
     */
    public BlankBinding getBlankBinding()
    {
        return blankBinding;
    }

    /**
     * Blank Binding where ALL Marks (not just Marks associated with Blanks 
     * in the document) after signified with _ "blanks"
     * 
     * @see #getMarks() 
     * @return the BlankBinding
     */
    public BlankBinding getAllMarksBinding()
    {
        return this.marksBinding;
    }

    /**
     * Gets the "Original" Markup Text (including the {@code Mark}s). That was
     * parsed to create the {@code Dom}
     *
     * @return the Markup Text
     */
    public String toSourceText()
    {
        String[] markFills = new String[ getMarkIndicies().cardinality() ];
        for( int i = 0; i < markFills.length; i++ )
        {
            markFills[ i ] = marks[ i ].getText();
        }
        return this.marksBinding.bind( (Object[])markFills );
    }

    @Override
    public String toString()
    {
        return "Template:" + "\r\n" + toSourceText();
    }    
    
 
    public Set<String> getAllVarNames()
    {
        return getVarNames( this );
    }
    
    private static Set<String> getVarNames( Template template )
    {
        Set<String> names = new HashSet<String>();        
        getAllVarNames( template.getMarks(), names );
        return names;
    }

    private static void getAllVarNames( Mark[] marks, Set<String> names )
    {
        for( int i = 0; i < marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasVar )
            {
                Mark.HasVar mhv = (Mark.HasVar)marks[ i ];
                names.add( mhv.getVarName() );
                //System.out.println( "VAR:" + mhv.getVarName() );
            }
            else if( marks[ i ] instanceof Mark.HasVarScript )
            {
                Mark.HasVarScript mhs = (Mark.HasVarScript)marks[ i ];
                if( mhs.getVarScriptInput() != null )
                {
                    String[] vn = mhs.getVarScriptInput().split( "," );
                    for( int j = 0; j < vn.length; j++ )
                    {
                        String varName = vn[ j ].trim();
                        if( varName.length() > 0 )
                        {
                            names.add( varName );
                        }
                    }
                }
            }
            else if( marks[ i ] instanceof Mark.HasVars )
            {
                Mark.HasVars mhvs = (Mark.HasVars)marks[ i ];
                names.addAll(  mhvs.getVarNames() );
            }
        }
    }
}
