package com.github.bednar.base.utils.resource;

import java.nio.file.Path;

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
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified(Mockito.<Path>any());

        LOG.info("write changes 2");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 2");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(2)).modified(Mockito.<Path>any());
    }

    @Test
    public void modifiedPath() throws Exception
    {
        FileChangeAnnounce announce = Mockito.mock(FileChangeAnnounce.class);

        FluentChange
                .byResources(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes");
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified(resourceForChange.asPath());
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
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified(Mockito.<Path>any());
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
        resourceForChange.update(String.valueOf(System.currentTimeMillis()));
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(1)).modified(Mockito.<Path>any());

        fluentChange.addResources(resourceNotInterest);

        LOG.info("write changes 1");
        resourceNotInterest.update(String.valueOf(System.currentTimeMillis()));

        LOG.info("wait for detect changes 2");
        Thread.sleep(10000);

        Mockito.verify(announce, Mockito.times(2)).modified(Mockito.<Path>any());
    }
}
