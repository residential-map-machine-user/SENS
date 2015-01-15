package laklab.inc.sens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class ChooseEventActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] sampleString = {"桜Discussion","Ruby Discussion","Freshman Speech","Welcome Freshman Party","Summer Camp"};
        setContentView(R.layout.activity_choose_event);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,sampleString);
        ListView listView =(ListView) findViewById(R.id.choose_event_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                //listView　parentでlistView全体を取得する
                ListView listView = (ListView) parent;
                //getItemAtPositionでクリックされたItemを取得する
                String item = (String) listView.getItemAtPosition(position);
                //Toastでクリックされたitem が取得されているか確認する
                Toast.makeText(ChooseEventActivity.this,item, Toast.LENGTH_SHORT).show();
                //つぎのアクティビティを開始する
                Intent intent = new Intent(ChooseEventActivity.this, MakeTaskActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("tasks", MODE_PRIVATE);
        String taskName = pref.getString("TASK_NAME", null);
        String taskLimit = pref.getString("TASK_LIMIT", null);
        System.out.println(taskName);
        System.out.println(taskLimit);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_event, menu);
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
}
