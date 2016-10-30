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
package varcode.java.model.edit;

import varcode.VarException;
import varcode.java.model._fields;
import varcode.java.model._methods;
import varcode.java.model._nest;

public class ExpectInstanceField
    implements MethodEditor
{

    String fieldName;
    String type;

    public ExpectInstanceField( String type, String name )
    {
        this.type = type;
        this.fieldName = name;
    }

    public _nest.component edit(_nest.component component, _methods._method method)
    {
        _fields fields = component.getFields();
        _fields._field f = fields.getByName(fieldName);
        if (f == null) {
            throw new VarException(
                    "Expected field " + type + " " + fieldName + " NOT FOUND");
        }
        if (!f.getType().equals(type)) {
            throw new VarException(
                    "Expected field " + type + " " + fieldName + System.lineSeparator()
                    + "...found field with same name of different type "
                    + f.getType() + " " + fieldName);
        }
        return component;
    }
}
