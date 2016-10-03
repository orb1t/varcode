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
package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.CodeAuthor;
import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * Model of an if statement 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _if
    implements Template, CodeAuthor
{        
    
    public static _if is( Object condition, Object... bodyLines )
    {
        return new _if( condition, bodyLines );
    }
    
    public _code condition;
    public _code body;
    public List<_elseIf>elseIfs;
    public _code elseBody;
    
    public _if( Object condition, Object...bodyLines )
    {
        this.condition = _code.of( condition );
        this.body = _code.of( bodyLines );        
        this.elseIfs = new ArrayList<_elseIf>();
        this.elseBody = new _code();        
    }

    @Override
    public _if replace(String target, String replacement)
    {
        this.condition = this.condition.replace( target, replacement );
        this.body = this.body.replace( target, replacement );
        this.elseBody = this.elseBody.replace ( target, replacement );
        for(int i=0; i< this.elseIfs.size(); i++)
        {
            this.elseIfs.get( i ).replace( target, replacement );
        }
        return this;
    }

    public VarContext getContext()
    {
        return VarContext.of(
            "condition", this.condition, 
            "body", this.body,
            "elseIf", this.elseIfs,
            "elseBody", this.elseBody );
    }
    
    public static final Dom IF_BLOCK = BindML.compile(
        "if( {+condition*+} )" + N +
        "{" + N +
        "{+$>(body)*+}" + N +
        "}" + N + 
        "{{+?elseIf:{+elseIf+}"+ N +        
        "+}}" +        
        "{{+?elseBody:else"+ N +
        "{" + N +
        "{+$>(elseBody)+}" + N +
        "}" + N +
        "+}}");
    
    @Override
    public String author( Directive... directives )
    {
        return Author.code( IF_BLOCK, getContext(), directives );
    }

    @Override
    public _if bindIn( VarContext context )
    {
        this.condition = this.condition.bindIn( context );
        this.body = this.body.bindIn( context );
        this.elseBody = this.elseBody.bindIn( context );
        this.body.bindIn( context );
        return this;
    }
    
    @Override
    public String bind( VarContext context, Directive...directives )
    {
        String elseIfs = "";
        for( int i = 0; i < elseIfs.length(); i++ )
        {
            elseIfs += this.elseIfs.get( i ).bind( context, directives );
        }
        VarContext vc = VarContext.of(
            "condition", this.condition.bind( context, directives ), 
            "body", this.body.bind( context, directives ), 
            "elseIfs", elseIfs,
            "elseBody", this.elseBody.bind( context, directives )                        
        );        
        return Author.code( IF_BLOCK, vc, directives );
    }

    
    @Override
    public String toString()
    {
        return author();
    }

    public _if _else( Object... elseBodyLines )
    {
        return _else( _code.of( elseBodyLines ) ); 
    }
    
    public _if _else( _code elseBody )
    {
        this.elseBody = elseBody;
        return this;
    }
      
    public _if elseIf( String condition, Object... codeBody )
    {
        _elseIf elf = new _elseIf( _code.of( condition ), _code.of( codeBody ) );
        this.elseIfs.add( elf );
        return this;
    }
    
    public _if elseIf( String elseCondition, _code elseIfBody )
    {
        _elseIf elf = new _elseIf(_code.of( elseCondition ), elseIfBody );
        this.elseIfs.add( elf );
        return this;
    }
        
    public static class _elseIf
        implements Template, CodeAuthor
    {        
        /**
        * 
        * @param context contains bound variables and scripts to bind data into
        * the template
        * @param directives pre-and post document directives 
        * @return the populated Template bound with Data from the context
        */
        @Override
        public String bind( VarContext context, Directive...directives )
        {
            Dom dom = BindML.compile( author() ); 
            return Author.code( dom, context, directives );
        }
        
        public static final Dom ELSEIF = BindML.compile( 
            "else if( {+condition*+} )" + N +  
            "{" + N + 
            "{+$>(elseIfBody)*+}" + N +
            "}" );
        
        private _code condition;
        private _code elseIfBody;
        
        public _elseIf( _code condition, _code elseIfBody )
        {
            this.condition = condition;
            this.elseIfBody = elseIfBody;
        }
        
        @Override
        public _elseIf bindIn( VarContext context )
        {
            this.condition.bindIn( context );
            this.elseIfBody.bindIn( context );
            return this;
        }
        
        //TODO
        @Override
        public _elseIf replace(String target, String replacement)
        {  
            this.condition.replace(target, replacement);
            this.elseIfBody.replace(target, replacement);
            return this;
        }

        public VarContext getContext()
        {
            return VarContext.of(
                "condition", this.condition,
                "elseIfBody", this.elseIfBody );
        }
        @Override
        public String author(Directive... directives)
        {
            return Author.code( ELSEIF, getContext(), directives );
        }      
        
        @Override
        public String toString()
        {
            return author();
        }
    }
}
