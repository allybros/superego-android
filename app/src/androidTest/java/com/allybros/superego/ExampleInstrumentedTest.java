package com.allybros.superego.elestirapp1;


import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.allybros.superego.api.LoadProfileTask;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.elestirapp1.allybros.elestirapp1", appContext.getPackageName());
    }

    @Test
    public void loadProfileTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String sessionToken = "9885baf124eb9320c19732ba649d85d8";
        LoadProfileTask.loadProfileTask(appContext, sessionToken);

    }
    @Test
    public void loadTraits(){
        Context appContext=InstrumentationRegistry.getTargetContext();
        LoadProfileTask.getAllTraits(appContext);
    }
}
