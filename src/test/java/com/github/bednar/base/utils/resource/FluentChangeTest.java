package com.github.bednar.base.utils.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (13/01/2014 18:55)
 */
@SuppressWarnings("ConstantConditions")
public class FluentChangeTest
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentChangeTest.class);

    private static final int REGISTER_WAIT = 5000;
    private static final int CHANGE_WAIT = 20000;

    private FluentResource resourceForChange;
    private FluentResource resourceNotInterest;

    @Before
    public void before()
    {
        resourceForChange = FluentResource.byPath("/com/github/bednar/base/fileToChange.txt");
        resourceNotInterest = FluentResource.byPath("/com/github/bednar/base/notInterestFile.txt");
    }

    @After
    public void after()
    {
        resourceForChange.close();
        resourceNotInterest.close();
    }

    @Test
    public void createInstance()
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange.byResources(announce, resourceForChange);
    }

    @Test
    public void watchAssync() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FileChangeContext context = FileChangeContext
                .byResource(resourceForChange);

        FluentChange
                .byResources(announce)
                .addFileChangeContext(context)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(REGISTER_WAIT);

        LOG.info("write changes 1");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.atLeast(1)).modified(context);

        LOG.info("write changes 2");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 2");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.atLeast(2)).modified(context);
    }

    @Test
    public void modifiedChangeContext() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FileChangeContext changeContext = FileChangeContext
                .byResource(resourceForChange);

        FluentChange
                .byResources(announce)
                .addFileChangeContext(changeContext)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(REGISTER_WAIT);

        LOG.info("write changes");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.atLeastOnce()).modified(changeContext);
    }

    @Test
    public void watchAssyncOnlyInterestFile() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FileChangeContext context = FileChangeContext
                .byResource(resourceForChange);

        FluentChange
                .byResources(announce)
                .addFileChangeContext(context)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(REGISTER_WAIT);

        LOG.info("write changes");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.times(1)).modified(context);
    }

    @Test
    public void addNewResource() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange fluentChange = FluentChange
                .byResources(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(REGISTER_WAIT);

        LOG.info("write changes 1");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.times(1)).modified(Mockito.<FileChangeContext>any());

        fluentChange.addResources(resourceNotInterest);

        LOG.info("write changes 1");
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 2");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.times(2)).modified(Mockito.<FileChangeContext>any());
    }

    @Test
    public void byPathPattern() throws IOException, InterruptedException
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange fluentChange = FluentChange
                .byResources(announce)
                .watchAssync();

        Path dir;
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            dir = resource
                    .asPath()
                    .getParent()
                    .resolve("directoryForChange");
        }

        FileChangeContext context = FileChangeContext
                .byPathPattern(dir, Pattern.compile(".*\\.txt"));

        fluentChange.addFileChangeContext(context);

        Path testTxt = dir.resolve("changeInPattern.txt");

        LOG.info("write to file: " + testTxt.toString());
        FileUtils.write(testTxt.toFile(), "data", "UTF-8");

        LOG.info("wait for changes");
        Thread.sleep(CHANGE_WAIT);

        Mockito.verify(announce, Mockito.times(1)).modified(Mockito.<FileChangeContext>any());
    }
}
