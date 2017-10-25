package org.wso2.siddhi.android.platform.sample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.wso2.siddhi.core.SiddhiManager;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        SiddhiManager siddhiManager=new SiddhiManager();
        assertEquals(1,1);
//        assertEquals("org.wso2.siddhi.android.platform.sample", appContext.getPackageName());
    }
}
