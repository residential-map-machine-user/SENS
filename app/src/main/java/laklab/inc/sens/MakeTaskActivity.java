package laklab.inc.sens;
//名前空間という
//packageで分かれているので追加する

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import java.util.List;

public class MakeTaskActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView _eventName;
    private Button _send;
    private EditText _dueData;
    private EditText _taskContent;
    private String _inputtedTask;
    private String _eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_task);
        _eventName = (TextView) findViewById(R.id.eventName);
        _dueData = (EditText) findViewById(R.id.dueDate);
        _taskContent = (EditText) findViewById(R.id.taskContent);
        _send = (Button) findViewById(R.id.send);
        _send.setOnClickListener(this);
        List<String> eventInfo = (List)getIntent().getExtras().get("eventInfo");
        System.out.println(eventInfo.get(0));
        _eventName.setText(eventInfo.get(1));
        _eventId = eventInfo.get(0);
        _inputtedTask = "";
    }


    @Override
    public void onClick(View v) {
        _inputtedTask += _dueData.getText().toString() + ",";
        _inputtedTask += _taskContent.getText().toString();
        Bundle params = new Bundle();
        params.putString("message", _inputtedTask);
        params.putBoolean("is_hidden", true);
        Session session = Session.getActiveSession();
        new Request(
                session,
                _eventId + "/comments",
                params,
                HttpMethod.POST,
                new Request.Callback() {
                    public void onCompleted(Response response) {
            /* handle the result */
                        Log.i("publish", response.toString());
                        Log.i("publish", _inputtedTask);
                    }
                }
        ).executeAsync();
        finish();
    }
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューの要素を追加して取得
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        switch(item.getItemId()){
            case R.id.event_list:
                Intent listEventIntent = new Intent(MakeTaskActivity.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(MakeTaskActivity.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(MakeTaskActivity.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}