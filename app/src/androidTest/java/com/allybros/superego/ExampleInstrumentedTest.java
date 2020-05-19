package com.allybros.superego;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.SearchTask;
import com.allybros.superego.unit.ConstantValues;

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
    public void searchQuery(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        BroadcastReceiver searchResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Received", intent.getStringExtra("result"));
            }
        };

        //TODO: Replace when the API is updated
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(searchResponseReceiver, new IntentFilter(ConstantValues.getActionSearch()));

        SearchTask.searchTask(appContext, "test");

    }

}
