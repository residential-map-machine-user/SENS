package laklab.inc.sens;
//名前空間という
//packageで分かれているので追加する

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

//extends は継承publicとprivateはよく使う

/**
 * クラスの前とメソッドの前にこのコメントで保管する
 * ここにはhtmlがける
 * 引数の説明
 * @param v イベント発火したビュー
 * 一行コメントは何に使うかというとメソッドの中でちょっと複雑だなという時に使う
 * コメントは自分のためのコメントと人に見せるためのコメント。
 *(TODO) FacebookSDK経由でfacebookに保存するようにする
 *
 */
public class MakeTaskActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView _eventName;
    private Button _send;
    private EditText _dueDate;
    private EditText _taskContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_task);
        _eventName = (TextView) findViewById(R.id.eventName);
        _dueDate = (EditText) findViewById(R.id.dueDate);
        _taskContent = (EditText) findViewById(R.id.taskContent);
        _send = (Button) findViewById(R.id.send);
        Intent intent = getIntent();
        ArrayList<String> eventName = intent.getStringArrayListExtra("eventInfo");
        Log.i("maketask", eventName.get(0).toString());
        _eventName.setText(eventName.get(0));
        _send.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_making_task, menu);
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
        //
        String taskName = _taskContent.getText().toString();
        String taskLimit = _dueDate.getText().toString();
        SharedPreferences pref = getSharedPreferences("tasks", MODE_PRIVATE);
        boolean saveState = pref.edit().putString("TASK_NAME", taskName).putString("TASK_LIMIT", taskLimit).commit();
        if (saveState) {
            finish();
        }
    }
}
