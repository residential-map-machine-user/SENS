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
        //Tasakの内容を表示するためのViewを取得する
        _eventName = (TextView) findViewById(R.id.eventName);
        _dueDate = (EditText) findViewById(R.id.dueDate);
        _taskContent = (EditText) findViewById(R.id.taskContent);
        _send = (Button) findViewById(R.id.send);
        Intent intent = getIntent();
        ArrayList<String> eventName = intent.getStringArrayListExtra("eventInfo");
        _eventName.setText(eventName.get(0));
        _send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TODO taskの内容をfacebookのコメント欄に送るためのRequestを書く
        String taskName = _taskContent.getText().toString();
        String taskLimit = _dueDate.getText().toString();
    }
}