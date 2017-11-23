Android Siddhi Platform provides a android service that can execute Siddhi apps. Siddhi apps has capability of gathering data and events, process them and trigger actions. By this platform those capabilities are extended to Android environment.  

#### Step 1: Creating Android Project

* Create Android App with the minimum API level 19
![](../images/tutorial/create-project.png?raw=true "Overview")
![](../docs/images/tutorial/create-project.png?raw=true "Overview")

![](../images/tutorial/target-project.png?raw=true "Overview")
![](../docs/images/tutorial/target-project.png?raw=true "Overview")

![](../images/tutorial/select-project.png?raw=true "Overview")
![](../docs/images/tutorial/select-project.png?raw=true "Overview")

![](../images/tutorial/activity-project.png?raw=true "Overview")
![](../docs/images/tutorial/activity-project.png?raw=true "Overview")

#### Step 2: Add required repositories to project build.gradle

To the Project level build gradle add WSO2 nexus repository and the jitpack repository

```groovy
allprojects {
   repositories {
       google()
       jcenter()
       maven { url 'http://maven.wso2.org/nexus/content/groups/wso2-public/' }
       maven { url 'https://jitpack.io' }
   }
}
```
    
#### Step 3: Add required dependencies to module build.gradle

```groovy

implementation('org.wso2.siddhi:siddhi-core:4.0.0-M86') {
   transitive = false
}
annotationProcessor('org.wso2.siddhi:siddhi-annotations:4.0.0-M86') {
   transitive = false
}
implementation('org.wso2.siddhi:siddhi-annotations:4.0.0-M86') {
   transitive = false
}
implementation('org.wso2.siddhi:siddhi-query-api:4.0.0-M86') {
   transitive = false
}
implementation('org.wso2.siddhi:siddhi-query-compiler:4.0.0-M86') {
   transitive = false
}
implementation('org.wso2.extension.siddhi.map.text:siddhi-map-text:1.0.2') {
   transitive = false
}
implementation('org.wso2.extension.siddhi.map.keyvalue:siddhi-map-keyvalue:1.0.1') {
   transitive = false
}
implementation "com.google.guava:guava:19.0"
implementation("org.apache.log4j.wso2:log4j:1.2.17.wso2v1") {
   transitive = false
}
implementation "org.osgi:org.osgi.core:6.0.0"
implementation "org.wso2.orbit.com.lmax:disruptor:3.3.2.wso2v2"
implementation "org.antlr:antlr4-runtime:4.5.1"
implementation 'com.github.chamathabeysinghe:siddhi-android-platform:test-SNAPSHOT'
implementation 'com.github.chamathabeysinghe:siddhi-io-android:test-SNAPSHOT'

```

#### Step 4: Set build configurations for the app
Set packaging options 
```groovy
compileOptions {
   sourceCompatibility JavaVersion.VERSION_1_8
   targetCompatibility JavaVersion.VERSION_1_8
}
packagingOptions {
   pickFirst 'META-INF/DEPENDENCIES'
   merge 'META-INF/annotations/org.wso2.siddhi.annotation.Extension'
}
```
Enable multidex 
Add following lines to defaultConfig block in the module level build.gradle. 
```groovy
multiDexEnabled true
android.defaultConfig.javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true
```
Now you are ready to execute Siddhi Apps in Android Platform. 

#### Step 5: Build user interface
Create the user interface to start and stop the Siddhi App. Edit the layout for MainActivity. 


```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
       xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:tools="http://schemas.android.com/tools"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       tools:context="com.example.chamath.simplesiddhi.MainActivity">

   <Button
           android:text="Start App"
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:id="@+id/button2"
           android:layout_marginTop="8dp" app:layout_constraintTop_toTopOf="parent"
           app:layout_constraintEnd_toEndOf="parent" android:layout_marginEnd="8dp"
           app:layout_constraintStart_toStartOf="parent" android:layout_marginStart="8dp"
           app:layout_constraintBottom_toBottomOf="parent" android:layout_marginBottom="8dp"
           app:layout_constraintHorizontal_bias="0.501"
           app:layout_constraintVertical_bias="0.398" android:onClick="startApp"/>
   <Button
           android:text="Stop App"
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:id="@+id/button3"
           android:layout_marginTop="8dp" app:layout_constraintTop_toBottomOf="@+id/button2"
           app:layout_constraintEnd_toEndOf="parent" android:layout_marginEnd="8dp"
           app:layout_constraintStart_toStartOf="parent" android:layout_marginStart="8dp"
           android:onClick="stopApp"/>
</android.support.constraint.ConstraintLayout>
```

####Step 6: Bind with the Siddhi Android Platform Service 
To execute Siddhi Apps, SimpleSiddhi App should be connected to the Android SiddhiService and then get a reference to that service so it can execute methods. For that first create required SiddhiAppController instance and ServiceConnection objects. 

```java

private SiddhiAppController appController;
private ServiceConnection serviceConnection = new ServiceConnection() {
   @Override
   public void onServiceConnected(ComponentName name, IBinder service) {
       appController = SiddhiAppController.Stub.asInterface(service);
   }

   @Override
   public void onServiceDisconnected(ComponentName name) {
       appController = null;
   }
};

```
Then invoke the service in onCreate of the Activity. 

```java
Intent intent = new Intent(this, SiddhiAppService.class);
startService(intent);
bindService(intent,serviceConnection,BIND_AUTO_CREATE);
```
####Step 7: Implement Functionality
Create the Siddhi App 
```java
private String app = "@app:name('foo')@source(type='android-humidity', @map(type='keyvalue'," +
       "fail.on.missing.attribute='false',@attributes(sensor='sensor',vector='humidity')))" +
       "define stream sensorInStream ( sensor string, vector float);" +
       "@sink(type='android-notification' , title='Details',multiple.notifications = 'true'," +
       " @map(type='keyvalue'))define stream outputStream (sensor string, vector float); " +
       "from sensorInStream select * insert into outputStream";

```
And finally you implement the startApp and stopApp functions.
```java
private String appName;
public void startApp(View view) throws RemoteException{
   appName = appController.startSiddhiApp(app);
}
public void stopApp(View view) throws RemoteException{
   appController.stopSiddhiApp(appName);
}
```
Run the app in emulator. Click the Send App button it will execute the app and will generate notifications for humidity sensor changes.  

For more information about writing Siddhi Apps visit [here](https://wso2.github.io/siddhi/)
