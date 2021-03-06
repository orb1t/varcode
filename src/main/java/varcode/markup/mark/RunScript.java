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
package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarBindException.NullResult;
import varcode.context.VarBindException;
import varcode.context.Context;
import varcode.markup.mark.Mark.Derived;
import varcode.markup.mark.Mark.MayBeRequired;
import varcode.context.VarScript;
import varcode.markup.mark.Mark.HasVarScript;

/**
 * Calls a script with some input
 * <B>Typically used for context / input validation/ assertion </B>
 * (will "fail early" if any of the validation routines throws an exception)
 *
 * RunScript should be IMMUTABLE, and IDEMPOTENT with NO SIDE EFFECTS.
 *
 * THIS DOES NOT:
 * <UL>
 * <LI>"write" anything to the tailored code
 * <LI> modify the context (any vars / forms)
 * </UL>
 *
 * Why is RunScript Useful:
 * <UL>
 * <LI>It can signal to the outside world (so technically this isn't strictly
 * immutable)
 * <LI>it can "FAIL", elegantly which is a good way of performing input
 * validation
 * <LI>it can "print out" the state of a Var, the VarContext / Bindings / Etc."
 * (debugging)
 * </UL>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class RunScript
    extends Mark
    implements HasVarScript, Derived, MayBeRequired
{
    /**
     * the name of the script to eval
     */
    private final String scriptName;

    /**
     * input to the script
     */
    private final String scriptInput;

    private final boolean isRequired;

    /*{$maxCount(fieldName,8)}*/
 /*{$maxCount(fieldName,8)*}*/ //REQUIRED
    public RunScript(
        String text,
        int lineNumber,
        String scriptName,
        String scriptInput,
        boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;
        this.isRequired = isRequired;
    }

    @Override
    public Object derive( Context context )
    {
        VarScript theScript = context.resolveScript( scriptName, scriptInput );
        if( theScript != null )
        {
            Object result = null;
            try
            {
                //System.out.println( "Evaluating SCRIPT" );
                result = theScript.eval( context, scriptInput );
            }
            catch( Exception e )
            {
                if( e instanceof VarException )
                {
                    throw (VarException)e;
                }
                throw new VarBindException(
                    "Script \"" + scriptName + "\" for mark :" + N + text + N
                    + " on line [" + lineNumber + "] could not be evaluated", e );
            }
            if( isRequired && result == null )
            {
                throw new NullResult( scriptName, scriptInput, text, lineNumber );
                // "Required Script \"" + scriptName + "\" for mark :" + N + text + N
                //+ " on line [" + lineNumber + "] returned null ( failed )" ); 
            }
            return result;
        }
        throw new VarBindException(
            "No script named \"" + scriptName + "\" found for mark: " + N + text
            + N + "on line [" + lineNumber + "]" );

    }

    @Override
    public String getVarScriptName()
    {
        return scriptName;
    }

    @Override
    public boolean isRequired()
    {
        return isRequired;
    }

    @Override
    public String getVarScriptInput()
    {
        return scriptInput;
    }
}
