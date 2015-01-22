package laklab.inc.sens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
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

    private final static String TAG = "LoginActivity";

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
    private ListView _eventList;
    private String _pageId = "684530848329994";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        userInfoTextView = (TextView) findViewById(R.id.userInfoTextView);
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);

//        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));

        /////////////////投稿用の機能実装/////////////////
        authButton.setPublishPermissions(Arrays.asList("publish_actions"));
        Button postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Session session = Session.getActiveSession();
                String sendText = "テストですよ";

                if (session.isOpened()) {
                    Request.newStatusUpdateRequest(session, sendText, new
                            Request.Callback() {
                                @Override
                                public void onCompleted(Response response) {
                                    if (response.getError() == null) {
                                        Log.i(TAG, "投稿しました。");
                                    } else {
                                        Log.i(TAG, "投稿エラー");
                                    }
                                }
                            }).executeAsync();
                }
            }
        });
        /////////////////////////////////////////////////

        /////////////投稿情報取得機能の実装/////////////////
        Button getWallInfo = (Button) findViewById(R.id.getWallinfo);
        final Bundle params = new Bundle();
        params.putString("limit", "100");
        getWallInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Session session = Session.getActiveSession();
                new Request(
                        session,
                        "/" + _pageId + "/feed",
                        params,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                try {
                                    Log.i(TAG, response.toString());
                                    String feedArr = (String) response.getGraphObject().getProperty("name");
                                    Log.i(TAG, feedArr);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                if (response.getError() == null) {
                                    Log.i(TAG, "投稿取得成功");

                                } else {
                                    Log.i(TAG, "投稿取得エラー");
                                }
                            }
                        }
                ).executeAsync();

            }
        });
        ////////////////////////////////////////////////////////////////////

        ///////////////////////batch requestのためのコード///////////////////////////
        batchRequestButton = (Button) findViewById(R.id.batchRequestButton);
        batchRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        //////////////////////////////////////////////////////////////////////////
    }

    /**
     * ここではsessionがどの状態を取っているかによって処理を実装している主にボタンが見えるか見えないかを決めている
     * @param session
     * @param state
     * @param exception
     */

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
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
    ///////////////// ユーザーデータを取得するための機能を実装/////////////////////////

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
    /////////////////////////////////////////////////////////////////
}

