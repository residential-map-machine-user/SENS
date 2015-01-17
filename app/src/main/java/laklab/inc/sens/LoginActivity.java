package laklab.inc.sens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends Activity {

    private final static String TAG = "MainActivity";

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private TextView userInfoTextView;
    private Button batchRequestButton;
    private TextView textViewResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        userInfoTextView = (TextView)findViewById(R.id.userInfoTextView);
        LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));
        batchRequestButton = (Button)findViewById(R.id.batchRequestButton);
        batchRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBatchRequest();
            }
        });
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (state.isOpened()) {
//            userInfoTextView.setVisibility(View.VISIBLE);
//            Intent intent = new Intent(LoginActivity.this, TopActivity.class);
//            startActivity(intent);
//        } else if (state.isClosed()) {
//            userInfoTextView.setVisibility(View.INVISIBLE);
//        }
        if (state.isOpened()) {
            userInfoTextView.setVisibility(View.VISIBLE);
            batchRequestButton.setVisibility(View.VISIBLE);

            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfoTextView.setText(buildUserInfoDisplay(user));
                    }
                }
            });
        }else if (state.isClosed()) {
            userInfoTextView.setVisibility(View.INVISIBLE);
            batchRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
//        JSONArray languages = (JSONArray)user.getProperty("languages");
//        if (languages.length() > 0) {
//            ArrayList<String> languageNames = new ArrayList<String> ();
//
//            // Get the data from creating a typed interface
//            // for the language data.
//            GraphObjectList<MyGraphLanguage> graphObjectLanguages =
//                    GraphObject.Factory.createList(languages,
//                            MyGraphLanguage.class);
//
//            // Iterate through the list of languages
//            for (MyGraphLanguage language : graphObjectLanguages) {
//                // Add the language name to a list. Use the name
//                // getter method to get access to the name field.
//                languageNames.add(language.getName());
//            }
//
//            userInfo.append(String.format("Languages: %s\n\n",
//                    languageNames.toString()));
//        }
        GraphObjectList<MyGraphLanguage> languages =
                (user.cast(MyGraphUser.class)).getLanguages();
        if (languages.size() > 0) {
            ArrayList<String> languageNames = new ArrayList<String> ();
            // Iterate through the list of languages
            for (MyGraphLanguage language : languages) {
                // Add the language name to a list. Use the name
                // getter method to get access to the name field.
                languageNames.add(language.getName());
            }

            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }
    private interface MyGraphLanguage extends GraphObject {
        // Getter for the ID field
        String getId();
        // Getter for the Name field
        String getName();
    }

    private interface MyGraphUser extends GraphUser {
        // Create a setter to enable easy extraction of the languages field
        GraphObjectList<MyGraphLanguage> getLanguages();
    }

    private void doBatchRequest() {
//        getViewが何の働きをしているのかいまいちよくわからないs
//        textViewResults = (TextView) this.getView().findViewById(R.id.textViewResults);
        textViewResults = (TextView) findViewById(R.id.textViewResults);
        textViewResults.setText("");

        String[] requestIds = {"me", "4"};

        RequestBatch requestBatch = new RequestBatch();
        for (final String requestId : requestIds) {
            requestBatch.add(new Request(Session.getActiveSession(),
                    requestId, null, null, new Request.Callback() {
                public void onCompleted(Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    String s = textViewResults.getText().toString();
                    if (graphObject != null) {
                        if (graphObject.getProperty("id") != null) {
                            s = s + String.format("%s: %s\n",
                                    graphObject.getProperty("id"),
                                    graphObject.getProperty("name"));
                        }
                    }
                    textViewResults.setText(s);
                }
            }));
        }
        requestBatch.executeAsync();
    }


}
