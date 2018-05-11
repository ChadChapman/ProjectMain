//package tcss450.uw.edu.group2project.utils;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import tcss450.uw.edu.group2project.R;
//
//public class MyChatContactsAdapter extends RecyclerView.Adapter<MyChatContactsAdapter.ViewHolder> {
//    private String[] mDataset;
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public TextView mTextView;
//        public ViewHolder(TextView v) {
//            super(v);
//            mTextView = v;
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public MyChatContactsAdapter(String[] myDataset) {
//        mDataset = myDataset;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public MyChatContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//                                                   int viewType) {
//        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())//parent.getContext()
//                .inflate(R.layout.fragment_contact, parent, false);
//        //...
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.length;
//    }
//}
