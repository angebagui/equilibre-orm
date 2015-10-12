Equilibre
=========

Connect to your database reactively with Equilibre in Android. Write my same code with my Ormlite every thime is not possible, so i decided to develop that library. And just create my differents pojo classes that extends to one class with reactive method. I have meet Parse Android SDK and Retrofit after, so i imagine a combination of HTTP client, ORM and Reactive Programming for one library. 


Before
======
You have to know this project is beta version. This version is a game version.
Type-safe HTTP client for Android will be inclued in the release version. 
Equilibre is inspired from Parse SDK and reactive programming.

Download
========

Add to your gradle before dependencies:
```groovy
repositories {
    maven {
        url 'https://dl.bintray.com/angebagui/maven'
    }
}
```
In your dependences
```groovy
dependencies {
...
compile 'com.equilibre:equilibre:0.1.0-beta2'
}
```

Usage
=====

Equilibre required that you created a subclass that extends to Application and your override the oncreate method like this:
```java
public class MyApp extends Application {
    private static String DATABASE_NAME = "dummyexampledb.db";
    private static int DATABASE_VERSION = 1;
    @Override
    public void onCreate() {
        super.onCreate();

        //Equilibe need to be initialzed
        Equilibre.initialize(getApplicationContext(), DATABASE_NAME, DATABASE_VERSION);
        //Register our differentes models
        EquilibreObject.registerSubclass(Dummy.class);

        // DON'T FORGET TO ADD MyApp in The MANIFEST OF YOUR PROJECT
    }
}
```

Here we have created a pojo named Dummy which extends to EquilibleObject. Like this
```java
//Required the use that annotation with the name the table
@EquilibreClassName("Dummy")
public class Dummy extends EquilibreObject { // The EquilibreObject have already an integer property named id has primary key

    @DatabaseField
    public String name;


    /**
     * It's required to create this default constructor
     */
    public Dummy(){

    }

    /**
     * It's required to write this method to build beautiful query with Equilibre
     * @return
     */
    public static EquilibreQuery<Dummy> getQuery(){
        return EquilibreQuery.getQuery(Dummy.class);
    }
}
```
Now everywhere in your code you came persiste data like this:
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        //Get our EquilibreQuery for request
        EquilibreQuery<Dummy> query = Dummy.getQuery();
        //make a request
        query.findInBackground(new FindCallback<Dummy>(){
            @Override
            public void done(List<Dummy> dummies, EquilibreException e) {
                if (e==null){ //it's good
                    Log.d("Ange BAGUI", "DEBUG ===> Dummies available: "+dummies+" size: "+dummies.size()+" <<<==== "+ new Date());
                }else{
                    // nothing to do
                    Log.e("Ange BAGUI", "ERROR ===> "+e+" <<<==== "+ new Date());
                }
            }
        });
    }

```

Save data Asynchronously
=======================
```java
//we don't have data so we create
        Dummy dummy1 = new Dummy("Ange");
        Dummy dummy2 = new Dummy("Bagui");
        Dummy dummy3 = new Dummy("Cyrille");
        Dummy dummy4 = new Dummy("Gupie");

        //Create a list
        List<Dummy> dummies = new ArrayList<>();

        //Add these to the list
        dummies.add(dummy1);
        dummies.add(dummy2);
        dummies.add(dummy3);
        dummies.add(dummy4);

        Log.d("Ange BAGUI", "DEBUG ===> Dummies created " + dummies + " <<<==== " + new Date());

        Dummy.saveAllInBackground(dummies, new SaveCallback(){
            @Override
            public void done(EquilibreException e) {
                if(e==null){
                    Log.d("Ange BAGUI", "DEBUG ===> Dummies saved successfully <<<==== " + new Date());
                }else{
                   //something is wrong
                    Log.e("Ange BAGUI", "ERROR ===> "+e+" <<<==== " + new Date());
                }
            }
        });
```

Licences
=======
    Copyright 2015 Ange Bagui.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


