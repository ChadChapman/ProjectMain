//package tcss450.uw.edu.group2project.utils;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import java.util.List;
//
//import tcss450.uw.edu.group2project.R;
//import tcss450.uw.edu.group2project.model.ChatContact;
//
//public class ChatContactsArrayAdapter extends ArrayAdapter {
//
//    List<ChatContact> mChatContactList;
//
//    public ChatContactsArrayAdapter(Context paramContext, List<ChatContact> paramList) {
//        super(paramContext, 0, paramList);
//        mChatContactList = paramList;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.row,parent,false);
//// inflate custom layout called row
//            holder = new ViewHolder();
//            holder.tv =(TextView) convertView.findViewById(R.is.textView1);
//// initialize textview
//            convertView.setTag(holder);
//        }
//        else
//        {
//            holder = (ViewHolder)convertView.getTag();
//        }
//        ChatContact chatContactIn = (ChatContact) mChatContactList.get(position);
//        holder.tv.setText(chatContactIn.getUsername());
//        // set the name to the text;
//
//        return convertView;
//
//    }
//
//    static class ViewHolder
//    {
//
//        TextView tv;
//    }
//}
////}
