package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import java.util.ArrayList;


public class DetailEventActivity extends ActionBarActivity implements View.OnClickListener {
    /**
     * _attendTokenはイベントに参加するかどうか判定するための変数
     */
    private boolean _attendToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        //参加表明のためのボタンをIdより取得してくる
        Button attend = (Button)findViewById(R.id.button_attend);
        //参加するボタンのにリスナーをつける
        attend.setOnClickListener(this);
        final TextView eventDay = (TextView) findViewById (R.id.eventDay);
        final TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        final TextView eventCost = (TextView) findViewById(R.id.eventCost);
        final TextView eventName = (TextView) findViewById(R.id.eventName);
        final TextView eventContent = (TextView) findViewById(R.id.eventContent);
        final Session session = Session.getActiveSession();
        //listEventsActivityでIntentにセットしたイベント情報を取得する
        Intent intent = getIntent();
        ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
        //それぞれのテキストviewにイベント情報をセット
        eventName.setText(eventInfo.get(0));
        eventDay.setText(eventInfo.get(1));
        eventPlace.setText(eventInfo.get(2));
        eventCost.setText(eventInfo.get(3));
        eventContent.setText(eventInfo.get(4));
    }

    @Override
    public void onClick(View v) {
        /**
         * TODO　ここでRequestを送るRequestの内容はPostされた投稿に対していいねを送る
         * TODO まずはpage/feed/likesのようなURIをしっかり把握する
         */
        Session session = Session.getActiveSession();
        new Request(
                session,
                getString(R.string.pageId) + "/feed",
                null,
                HttpMethod.POST,
                new Request.Callback() {
                    public void onCompleted(Response response) {
            /* handle the result */
                        Log.d("チェック", response.toString());
                    }
                }
        ).executeAsync();
    }
}
