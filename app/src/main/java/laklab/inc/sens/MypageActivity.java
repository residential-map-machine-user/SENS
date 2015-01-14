package laklab.inc.sens;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MypageActivity extends ActionBarActivity{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sampleでlistViewに表示する配列を作成
        String[] sampleEvent = {"新入生歓迎会","卒部式","ワカサギ釣り","SummerCamp"};
        String[] sampleTask = {"Prize購入","審査員に歓迎の品購入","場所の手配","参加者にリマインドを送る"};
        setContentView(R.layout.activity_mypage);
        //ArrayAdapterを作成する
        ArrayAdapter <String> eventAdapter =  new ArrayAdapter <String>(this, android.R.layout.simple_list_item_1,sampleEvent);
        ArrayAdapter <String> taskAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_list_item_1, sampleTask);

        ListView eventListView = (ListView) findViewById(R.id.my_event);
        ListView taskListView = (ListView) findViewById(R.id.my_task);
        //Arrayアダプターをセットすることで配列をlistViewに連続して配置する
        eventListView.setAdapter(eventAdapter);
        taskListView.setAdapter(taskAdapter);
        /**
         * eventListViewがクリックされた時にクリックされたListViewのToastを表示する。
         * TODO クリックされた　listViewの情報をsharedPreferencesに保存する。保存したデータを次のActivityに送る.
         *
         */
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //listView　parentでlistView全体を取得する
                ListView listView = (ListView) parent;
                //getItemAtPositionでクリックされたItemを取得する
                String item = (String) listView.getItemAtPosition(position);
                //Toastでクリックされたitem が取得されているか確認する
                Toast.makeText(MypageActivity.this,item,Toast.LENGTH_SHORT).show();
                //つぎのアクティビティを開始する
                Intent intent = new Intent(MypageActivity.this, DetailEventActivity.class);
                startActivity(intent);
            }
        });
        /**
         * taskListViewがクリックされた時にクリックされたlistViewのToastを表示する
         */
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(MypageActivity.this,item,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MypageActivity.this, DetailTaskActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mypage, menu);
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
