package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

        FluentChange
                .byResources(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes 1");
        changeFile(resourceForChange);

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified();

        LOG.info("write changes 2");
        changeFile(resourceForChange);

        LOG.info("wait for detect changes 2");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(2)).modified();
    }

    @Test
    public void watchAssyncOnlyInterestFile() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange
                .byResources(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes");
        changeFile(resourceForChange);
        changeFile(resourceNotInterest);

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified();
    }

    @Test
    public void addNewResource() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange fluentChange = FluentChange
                .byResources(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes 1");
        changeFile(resourceForChange);
        changeFile(resourceNotInterest);

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified();

        fluentChange.addResources(resourceNotInterest);

        LOG.info("write changes 1");
        changeFile(resourceNotInterest);

        LOG.info("wait for detect changes 2");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(2)).modified();
    }

    private void changeFile(@Nonnull final FluentResource resource) throws Exception
    {
        byte[] data = Long.valueOf(System.currentTimeMillis()).toString().getBytes();
        Path path   = Paths.get(resource.asURL().toURI());

        Files.write(path, data, StandardOpenOption.SYNC);
    }
}
