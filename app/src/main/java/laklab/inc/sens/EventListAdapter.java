package laklab.inc.sens;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
    public View getView(int position, View contentView, ViewGroup parent){
        return contentView;
    }
}
