package rodrigodavy.com.github.pixelartist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawerMenuItemAdapter extends ArrayAdapter<DrawerMenuItem> {

    DrawerMenuItemAdapter(@NonNull Context context, @NonNull ArrayList<DrawerMenuItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.drawer_list_item, parent, false);
        }

        DrawerMenuItem drawerMenuItem = getItem(position);

        TextView text = listItemView.findViewById(R.id.drawer_item_text);

        assert drawerMenuItem != null;
        text.setCompoundDrawablesWithIntrinsicBounds(drawerMenuItem.getIconId(), 0, 0, 0);
        text.setText(drawerMenuItem.getStringId());

        return listItemView;
    }
}
