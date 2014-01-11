package com.github.bednar.base.utils.lang;

import java.util.regex.Pattern;

/**
 * @author Jakub Bednář (11/01/2014 16:19)
 */
public final class Patterns
{
    private Patterns()
    {
    }

    /**
     * <code>
     *     Matcher matcher = FILE_NAME_EXTENSION}.matcher("...");
     *     matcher.find();
     *
     *     String group1 = matcher.group(1);
     *     String group2 = matcher.group(2);
     *     String group3 = matcher.group(3);
     * </code>
     *
     * <ul>
     *     <li>test.xml => Group1=null Group2='test' Group3='.xml'</li>
     *     <li>/test.xml => Group1='/' Group2='test' Group3='.xml'</li>
     *     <li>jade/path/test.xml => Group1='jade/path/' Group2='test' Group3='.xml'</li>
     *     <li>/jade/path/test.xml => Group1='/jade/path/' Group2='test' Group3='.xml'</li>
     *     <li>file:/Users/jade/assignTo.jade => Group1='file:/Users/jade/' Group2='test' Group3='.xml'</li>
     * </ul>
     */
    public static final Pattern FILE_NAME_EXTENSION = Pattern.compile("^(.*/)*(\\w+)(.*)$");
}
