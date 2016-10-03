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

import varcode.CodeAuthor;
import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * model of a while loop
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _while
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
    
    public static _while is( Object condition, Object... bodyLines )
    {
        return new _while( condition, bodyLines );
    }
    
    public _code condition;
    public _code body;
    
    public _while( Object whileCondition, Object...bodyLines )
    {
        this.condition = _code.of( whileCondition );
        this.body = _code.of( bodyLines );        
    }

    @Override
    public _while bindIn( VarContext context )
    {
        this.condition.bindIn( context );
        this.body.bindIn( context );
        return this;
    }
    
    @Override
    public _while replace(String target, String replacement)
    {
        this.condition = this.condition.replace( target, replacement );
        this.body = this.body.replace( target, replacement );
        return this;
    }

    public VarContext getContext()
    {
        return VarContext.of(
            "condition", this.condition, "body", this.body );
    }
    public static final Dom WHILE_LOOP = BindML.compile(
        "while( {+condition*+} )" + N +
        "{" + N +
        "{+$>(body)*+}" + N +
        "}");
    
    @Override
    public String author( Directive... directives )
    {
        return Author.code( WHILE_LOOP, getContext(), directives );
    }
    
    @Override
    public String toString()
    {
        return author();
    }
}
