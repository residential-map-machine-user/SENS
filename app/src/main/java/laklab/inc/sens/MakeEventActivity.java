package laklab.inc.sens;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;

import java.util.ArrayList;


public class MakeEventActivity extends ActionBarActivity implements View.OnClickListener {

    private String _eventName;
    private String _eventDay;
    private String _eventPlace;
    private String _eventContent;
    private String _eventCost;
    private String _inputtedEventInfo= "";
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        TextView eventName = (TextView) findViewById(R.id.eventName);
        TextView eventDay = (TextView) findViewById(R.id.eventDay);
        TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        TextView eventContent = (TextView) findViewById(R.id.eventContent);
        TextView eventCost = (TextView) findViewById(R.id.eventCost);
        _eventName = eventName.getText().toString();
        _eventDay = eventDay.getText().toString();
        _eventPlace = eventPlace.getText().toString();
        _eventContent = eventContent.getText().toString();
        _eventCost = eventCost.getText().toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_making_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        _inputtedEventInfo += _eventName + ",";
        _inputtedEventInfo += _eventDay + ",";
        _inputtedEventInfo += _eventPlace + ",";
        _inputtedEventInfo += _eventCost + ",";
        _inputtedEventInfo += _eventContent + ",";
        Bundle params = new Bundle();
        params.putString("message", _inputtedEventInfo);
        Session session = Session.getActiveSession();
        new Request(
                session,
                getString(R.string.pageId) + "/feed",
                params,
                HttpMethod.POST,
                new Request.Callback() {
                    public void onCompleted(Response response) {
            /* handle the result */
                        Log.i("publish", response.toString());
                        Log.i("publish", _inputtedEventInfo);
                    }
                }
        ).executeAsync();
//        SharedPreferences pref = getSharedPreferences("EVENT_NAME", MODE_PRIVATE);
//        Boolean saveState = pref.edit().putString("EVENT_NAME",_eventName).commit();
//        if (saveState){
//            finish();
//        }
        finish();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
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
}
