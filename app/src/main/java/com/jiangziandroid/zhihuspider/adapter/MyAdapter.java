//package com.jiangziandroid.zhihuspider.adapter;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.jiangziandroid.zhihuspider.R;
//
///**
// * Created by JeremyYCJiang on 2015/5/11.
// */
//
//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//
//
//    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
//    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java
//
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textView;
//        ImageView imageView;
//
//
//
//        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
//            super(itemView);
//
//                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
//                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
//        }
//
//
//    }
//
//
//
//    public MyAdapter(String Titles[], int Icons[]){ // MyAdapter Constructor with titles and icons parameter
//        // titles, icons, name, email, profile pic are passed from the main activity as we
//        mNavTitles = Titles;                //have seen earlier
//        mIcons = Icons;
//                   //here we assign those passed values to the values we declared here
//        //in adapter
//
//
//
//    }
//
//
//
//    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
//    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
//    // if the viewType is TYPE_HEADER
//    // and pass it to the view holder
//
//    @Override
//    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_drawer_item_row,parent,false); //Inflating the layout
//
//            ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
//
//            return vhItem; // Returning the created object
//
//            //inflate your layout and pass it to view holder
//
//    }
//
//    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
//    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
//    // which view type is being created 1 for item row
//    @Override
//    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {         // as the list view is going to be called after the header view so we decrement the
//            // position by 1 and pass it to the holder while setting the text and image
//            holder.textView.setText(mNavTitles[position]); // Setting the Text with the array of our Titles
//            holder.imageView.setImageResource(mIcons[position]);// Settimg the image with array of our icons
//
//    }
//
//    // This method returns the number of items present in the list
//    @Override
//    public int getItemCount() {
//        return mNavTitles.length; // the number of items in the list will be +1 the titles including the header view.
//    }
//
//}