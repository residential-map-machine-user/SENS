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
    /**
     * itemのtextViewに表示するためのコレクション
     */
    private List<String> _obj;
    /**
     * イベントIdをkeyにイベントネームを取得
     */
    private Map<String, String> _eventMap;
    /**
     * キー：　イベントId　値： いいね数　
     */
    private Map<String, String> _memberBuffer;
    /**
     *マイページのために使うフラグ
     */
    private boolean _useForMyPage = false;

    /**
     *コンストラクタ
     * @param context　コンテキスト
     * @param resource　TextViewを含むレイアウトファイルをインスタンス化したときのためのresourceID
     * @param objects 表示するためのデータセット
     * @param eventMap イベントリストの名前を値に含む
     */
    public EventListAdapter(Context context, int resource, List<String> objects, Map<String, String> eventMap) {
        super(context, resource, objects);
        //クラスのグローバル変数として保存
        _obj = objects;
        _eventMap = eventMap;
        _memberBuffer = new HashMap<>();
    }

    /**
     *Itemにレイアウトファイルとviewに値をセットするメソッド
     * @param position 生成されるリストの位置
     * @param convertedView　表示する１行分のView
     * @param parent リストビュー
     * @return　表示する１行分のView
     */
    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        //初期化
        ViewHolder viewHolder = null;
        //itemの再利用
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
            //すでに再利用しているViewがある場合はデータだけを取得
            viewHolder = (ViewHolder)convertedView.getTag();
        }
        //タイトルのセットをする
        viewHolder._eventTitle.setText(_obj.get(position));
        //イベントのIdを取得 ::_obj.get(postion) == 押され場所と同じ位置のイベント名
        /////////////////////////////////////////////////////////////////////////////////////////
        //リストでイベント名を渡す　マップでkeyイベント名　valueイベントIdにするこイベントIdの取得が可能になる
        //メリットとしてはeventMap.containsKeyなどひも付けられた情報に対して操作ができること
        ////////////////////////////////////////////////////////////////////////////////////////
        final String eventId = _eventMap.get(_obj.get(position));
        //memberbufferに値を保存していく処理
        /////////////////////////////////////////////////////////
        //ここではwhile文のような使いかたがされている
        //1.getViewはviewが生成されるたびに呼ばれるメソッド
        //2.コレクションの中に存在したら保存しない
        //3.存在しなかったら保存する
        /////////////////////////////////////////////////////////
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
                            //if elseを使うことで絶対に例外が起こらない仕組みを作れる
                            try {
                                //マイページ以外に使い場合
                                if(!_useForMyPage) {
                                    if(response.getGraphObject() != null) {
                                        //JsonObjectからint型で値を取得
                                        int likesUser = ((JSONObject) response.getGraphObject().getProperty("summary")).getInt("total_count");
                                        //イベントIdと参加者数を保持
                                        _memberBuffer.put(eventId, likesUser + "");
                                        //動的なデータ変更がある場合にデータを更新する。
                                        ////////////////////////////////////////////////////////
                                        //アクティビティのインスタンスがすでに生成されていたとしても
                                        //notifyDataSetChanged()によってデータが変更される
                                        //具体的にはリフレッシュというアクションが実行された時変更される
                                        ////////////////////////////////////////////////////////
                                        notifyDataSetChanged();
                                    }else{
                                        _memberBuffer.put(eventId,"-----");
                                        notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException dataNotFound){
                                //ここは処理しない
                            }
                        }
                    }).executeAsync();
        } else {
            //itemのtextViewに文字をセット
            viewHolder._attendUserStatus.setText("参加者" + _memberBuffer.get(eventId));
        }
        //レイアウトと値がセットされたViewを返す
        return convertedView;
    }

    /**
     * 外部のクラスから扱う時に使う。
     * @param useForMyPage　マイページに使うときはtrueにする
     */
    public void setUseForMyPage(boolean useForMyPage){
        _useForMyPage = useForMyPage;
    }


    //変数がpublic なのでアクセスしやすくなっている
    class ViewHolder {
        public ImageView _eventIcon;
        public TextView _eventTitle;
        public TextView _attendUserStatus;
    }
}
