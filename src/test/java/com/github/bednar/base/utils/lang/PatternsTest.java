package com.github.bednar.base.utils.lang;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (11/01/2014 16:23)
 */
public class PatternsTest
{
    @Test
    public void fileNameExtension()
    {
        Matcher matcher = Patterns.FILE_NAME_EXTENSION.matcher("test.xml");
        matcher.find();

        Assert.assertEquals(null, matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
        Assert.assertEquals(".xml", matcher.group(3));

        matcher = Patterns.FILE_NAME_EXTENSION.matcher("/test.xml");
        matcher.find();

        Assert.assertEquals("/", matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
        Assert.assertEquals(".xml", matcher.group(3));

        matcher = Patterns.FILE_NAME_EXTENSION.matcher("jade/path/test.xml");
        matcher.find();

        Assert.assertEquals("jade/path/", matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
        Assert.assertEquals(".xml", matcher.group(3));

        matcher = Patterns.FILE_NAME_EXTENSION.matcher("/jade/path/test.xml");
        matcher.find();

        Assert.assertEquals("/jade/path/", matcher.group(1));
        Assert.assertEquals("test", matcher.group(2));
        Assert.assertEquals(".xml", matcher.group(3));

        matcher = Patterns.FILE_NAME_EXTENSION.matcher("file:/Users/jade/assignTo.jade");
        matcher.find();

        Assert.assertEquals("file:/Users/jade/", matcher.group(1));
        Assert.assertEquals("assignTo", matcher.group(2));
        Assert.assertEquals(".jade", matcher.group(3));
    }
}
