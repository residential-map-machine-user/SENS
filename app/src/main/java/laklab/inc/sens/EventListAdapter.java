package laklab.inc.sens;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yukimatsuyama on 2015/01/19.

 */
public class EventListAdapter extends ArrayAdapter<String> {

    private List<String> _src;
    //クラスと同じ名前のメソッドがクラスの中に記述されているとコンストラクタになる
    public EventListAdapter(Context context, int resource, List<String> objects) {
        //ここのsuperはメソッドとしてのsuper?
        super(context, resource, objects);
        //_src = objectsのobjectsは初期化されるときに引数として渡される
        _src = objects;
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertedView == null){
            convertedView = View.inflate(getContext(), R.layout.list_item_original, null);
            viewHolder = new ViewHolder();
            //ここでしている処理はViewという方のconvertedViewをキャストしている.
            /**
             * いつものfindViewByIdとは若干違う
             * どこが違うかというといつもはInstanveにたいしてではなくキャストしたらそのままfindViewById
             * しかし今回はInstanceにたいして使用指定いる。これは
             * Button button = (Button) findViewById(R.id.button);
             * の場合はxmlの方ですでにInstanceを作成しているのでそのまま使える。けれど今回はまだViewは作成していないため
             * viewHolder._eventIcon = (ImageView)convertedView.findViewById(R.id.eventIcon);
             *
             */
            viewHolder._eventIcon = (ImageView)convertedView.findViewById(R.id.eventIcon);
            viewHolder._eventTitle = (TextView)convertedView.findViewById(R.id.eventTitle);
            convertedView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertedView.getTag();
        }
        viewHolder._eventTitle.setText(_src.get(position));
        return convertedView;
    }

    class ViewHolder {
        public ImageView _eventIcon;
        public TextView _eventTitle;
    }
}
