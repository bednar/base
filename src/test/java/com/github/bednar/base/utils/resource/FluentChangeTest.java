package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
        FluentChange.byResource(new CounterAnnounce(), resourceForChange);
    }

    @Test
    public void watchAssync() throws Exception
    {
        CounterAnnounce announce = new CounterAnnounce();

        FluentChange
                .byResource(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes 1");
        changeFile(resourceForChange);

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Assert.assertEquals((Object) 1, announce.getCount());

        LOG.info("write changes 2");
        changeFile(resourceForChange);

        LOG.info("wait for detect changes 2");
        Thread.sleep(10000);

        Assert.assertEquals((Object) 2, announce.getCount());
    }

    @Test
    public void watchAssyncOnlyInterestFile() throws Exception
    {
        CounterAnnounce announce = new CounterAnnounce();

        FluentChange
                .byResource(announce, resourceForChange)
                .watchAssync();

        LOG.info("wait for register watch service");
        Thread.sleep(2000);

        LOG.info("write changes");
        changeFile(resourceForChange);
        changeFile(resourceNotInterest);

        LOG.info("wait for detect changes 1");
        Thread.sleep(10000);

        Assert.assertEquals((Object) 1, announce.getCount());
    }

    private class CounterAnnounce implements FileChangeAnnounce
    {
        private Integer count = 0;

        @Override
        public void modified()
        {
            this.count++;
        }

        @Nonnull
        public Integer getCount()
        {
            return count;
        }
    }

    private void changeFile(@Nonnull final FluentResource resource) throws Exception
    {
        byte[] data = Long.valueOf(System.currentTimeMillis()).toString().getBytes();
        Path path   = Paths.get(resource.asURL().toURI());

        Files.write(path, data, StandardOpenOption.SYNC);
    }
}
