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
    public EventListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        _src = objects;
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertedView == null){
            convertedView = View.inflate(getContext(), R.layout.list_item_original, null);
            viewHolder = new ViewHolder();
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
