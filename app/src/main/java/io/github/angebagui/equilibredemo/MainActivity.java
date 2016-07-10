package io.github.angebagui.equilibredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.angebagui.equilibre.EquilibreException;
import io.github.angebagui.equilibre.FindCallback;
import io.github.angebagui.equilibre.FunctionalUtils;
import io.github.angebagui.equilibre.SaveCallback;
import io.github.angebagui.equilibredemo.model.User;

public class MainActivity extends AppCompatActivity {

    public static final  String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final User user1 = new User("Ange Bagui", "angebagui@gmail.com");
        final User user2 = new User("Marc Bagui", "angenbagui@gmail.com");
        final User user3 = new User("Franck Bagui", "angmebagui@gmail.com");
/*
        final List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        User.saveAllInBackground(users, new SaveCallback() {


            @Override
            public void done(EquilibreException e) {
                if(e==null){
                    Log.d(TAG, " I have saved");
                }
            }
        });*/

        User.getQuery().findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, EquilibreException e) {

                if(e==null){
                    Log.d(TAG, " I have found data ==>> "+objects);

                   FunctionalUtils.forEach(FunctionalUtils.map(objects, new FunctionalUtils.Function1<User, String>() {
                       @Override
                       public String call(User user) {
                           return user.getName();
                       }
                   }), new FunctionalUtils.Function2<String>() {
                       @Override
                       public Void call(String s) {
                           Log.d(TAG, " I have found data ==>> "+s);
                           return null;
                       }
                   });
                }
            }

        });
    }
}
