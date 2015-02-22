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

    private List<String> _src;
    private Map<String, String> _eventMap;
    private Map<String, Integer> _memberBuffer;
    //クラスと同じ名前のメソッドがクラスの中に記述されているとコンストラクタになる

    /**
     *
     * @param context
     * @param resource
     * @param objects 表示される
     */
    public EventListAdapter(Context context, int resource, List<String> objects, Map<String, String> eventMap) {
        //ここのsuperはメソッドとしてのsuper?
        super(context, resource, objects);
        //_src = objectsのobjectsは初期化されるときに引数として渡される
        _src = objects;
        _eventMap = eventMap;
        _memberBuffer = new HashMap<>();
    }
    //行数分だけ呼ばれるgetView
    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertedView == null){
            convertedView = View.inflate(getContext(), R.layout.list_item_original, null);
            viewHolder = new ViewHolder();
            viewHolder._eventIcon = (ImageView)convertedView.findViewById(R.id.eventIcon);
            viewHolder._eventTitle = (TextView)convertedView.findViewById(R.id.eventTitle);
            viewHolder._likesUser = (TextView)convertedView.findViewById(R.id.likesUser);
            //TODO setTagがどんな役割をしているのか調べる
            //情報を渡したい時に使われる
            //今回で言う所のiconとtitle
            convertedView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertedView.getTag();
        }
        viewHolder._eventTitle.setText(_src.get(position));
        final String objectId = _eventMap.get(_src.get(position));
        if(!_memberBuffer.containsKey(objectId)){
            Session session = Session.getActiveSession();
            Bundle bundle = new Bundle();
            bundle.putBoolean("summary", true);
            new Request(session,
                    "/" + objectId + "/likes",
                    bundle,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Log.i("likesUserの中身", response.toString());
                            try {
                                int likesUser = ((JSONObject) response.getGraphObject().getProperty("summary")).getInt("total_count");
                                Log.i("チェックグラフ", likesUser + "");
                                _memberBuffer.put(objectId, likesUser);
                                notifyDataSetChanged();
                            } catch (JSONException dataNotFound){
                                //ここは処理しない
                            }
                        }
                    }).executeAsync();
        } else {
            viewHolder._likesUser.setText("参加者" + _memberBuffer.get(objectId));
        }
        return convertedView;
    }

    class ViewHolder {
        public ImageView _eventIcon;
        public TextView _eventTitle;
        public TextView _likesUser;
    }
}
