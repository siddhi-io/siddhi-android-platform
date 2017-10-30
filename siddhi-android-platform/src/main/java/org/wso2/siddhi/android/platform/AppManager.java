/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.android.platform;

import android.util.Log;
import org.wso2.siddhi.android.platform.util.SiddhiAndroidException;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.query.compiler.exception.SiddhiParserException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage Siddhi Apps sent to the Android Siddhi Service
 */
class AppManager {

    private SiddhiManager siddhiManager;
    private Map<String, SiddhiAppRuntime> siddhiAppList;

    public AppManager() {
        this.siddhiAppList = new HashMap<>();
        this.siddhiManager = new SiddhiManager();
    }

    public String startApp(String siddhiApp) {
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);
            String appIdentifier = siddhiAppRuntime.getName();
            if (siddhiAppList.containsKey(appIdentifier)) {
                Log.e("Siddhi Platform", "Similar App name already exists ins the list");
                return null;
            }
            siddhiAppList.put(appIdentifier, siddhiAppRuntime);
            siddhiAppRuntime.start();
            return siddhiAppRuntime.getName();
        } catch (SiddhiAppCreationException | SiddhiParserException e) {
            throw new SiddhiAndroidException(e);
        }
    }

    public void stopApp(String appName) {
        SiddhiAppRuntime siddhiAppRuntime = siddhiAppList.remove(appName);
        if (siddhiAppRuntime == null) {
            Log.e("Siddhi Platform", "No app with name, '" + appName + "', is currently" +
                    " executing. ");
            return;
        }
        siddhiAppRuntime.shutdown();
    }
}
