/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.whaleread.codegen.api.dom;

import com.whaleread.codegen.api.XmlFormatter;
import com.whaleread.codegen.api.dom.xml.Document;
import com.whaleread.codegen.config.Context;

/**
 * This class is the default formatter for generated XML.  This class will use the
 * built in formatting of the DOM classes directly.
 * 
 * @author Jeff Butler
 *
 */
public class DefaultXmlFormatter implements XmlFormatter {
    protected Context context;

    @Override
    public String getFormattedContent(Document document) {
        return document.getFormattedContent();
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
