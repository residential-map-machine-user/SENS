package laklab.inc.sens;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yukimatsuyama on 2015/01/19.
 */
public class EventListAdapter extends ArrayAdapter<String> {

    private List<String> _dataSet;
    private Map<String, String> _eventMap;
    private Map<String, Integer> _memberBuffer;
    /**
     *
     */
    private boolean _useForMyPage = false;

    /**
     *コンストラクタ
     * @param context　コンテキスト
     * @param resource　どのリスト化を示す
     * @param objects 表示するためのデータセット
     * @param eventMap イベントのnameがkeyイベントIDがvalue
     */
    public EventListAdapter(Context context, int resource, List<String> objects, Map<String, String> eventMap) {
        super(context, resource, objects);
        _dataSet = objects;
        _eventMap = eventMap;
        _memberBuffer = new HashMap<>();
    }

    /**
     *
     * @param position 生成されるリストの位置
     * @param convertedView　表示する１行分のView
     * @param parent リストビュー
     * @return　表示する１行分のView
     */
    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        //初期化
        ViewHolder viewHolder = null;
        //表示したいデータのセット
        /**
         * この分岐は抜け目なくデータをセットするための分岐
         * nullな場合はタグでインスタンスを保持
         * null出ない場合はインスタンスを取得してくる
         */
        if (convertedView == null){
            //システムで用意されているViewではないViewを使うことを宣言
            convertedView = View.inflate(getContext(), R.layout.list_item_original, null);
            //常時するデータを保持するインスタンスの生成
            viewHolder = new ViewHolder();
            //表示するためのViewを取得
            viewHolder._eventIcon = (ImageView)convertedView.findViewById(R.id.eventIcon);
            viewHolder._eventTitle = (TextView)convertedView.findViewById(R.id.eventTitle);
            viewHolder._attendUserStatus = (TextView)convertedView.findViewById(R.id.likesUser);
            //タグ付けしてデータを保存
            convertedView.setTag(viewHolder);
        }else{
            //タグ付けしてあったデータを取得
            viewHolder = (ViewHolder)convertedView.getTag();
        }
        //タイトルのセット
        viewHolder._eventTitle.setText(_dataSet.get(position));
        //イベントのIdを取得
        final String eventId = _eventMap.get(_dataSet.get(position));
        /**
         * この分岐はデータを持っていなかったらデータをセットする仕組み
         * メリット　データの重複がなく取得したデータを保存できる
         */
        if(!_memberBuffer.containsKey(eventId)){
            Session session = Session.getActiveSession();
            Bundle bundle = new Bundle();
            //いいねしているユーザーの総数を取得するためのパラメータ
            bundle.putBoolean("summary", true);
            new Request(session,
                    "/" + eventId + "/likes",
                    bundle,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Log.i("likesUserの中身", response.toString());
                            try {
                                if(!_useForMyPage) {
                                    if(response.getGraphObject() != null) {
                                        //JsonObjectからint型で値を取得
                                        int likesUser = ((JSONObject) response.getGraphObject().getProperty("summary")).getInt("total_count");
                                        //イベントIdと参加者数を保持
                                        _memberBuffer.put(eventId, likesUser);
                                        //動的なデータ変更がある場合にデータはcallbackする
                                        notifyDataSetChanged();
                                    }else{
                                        _memberBuffer.put(eventId, 0);
                                        notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException dataNotFound){
                                //ここは処理しない
                            }
                        }
                    }).executeAsync();
        } else {
            viewHolder._attendUserStatus.setText("参加者" + _memberBuffer.get(eventId));
        }
        //eventMapがnullの時は何も表示しない
        return convertedView;
    }

    /**
     * 外部のクラスから扱う時に使う。
     * @param useForMyPage　
     */
    public void setUseForMyPage(boolean useForMyPage){
        _useForMyPage = useForMyPage;
    }

    class ViewHolder {
        public ImageView _eventIcon;
        public TextView _eventTitle;
        public TextView _attendUserStatus;
    }
}
