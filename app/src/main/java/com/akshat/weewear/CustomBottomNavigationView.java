package com.akshat.weewear;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomBottomNavigationView extends BottomNavigationView {

    public CustomBottomNavigationView(Context context) {
        super(context);
        alignMenuItems();
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        alignMenuItems();}

    public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        alignMenuItems();
    }

    private void alignMenuItems() {
        BottomNavigationView menuView = (BottomNavigationView) getChildAt(0);
//        for (int i = 0; i < menuView.getChildCount(); i++) {
//            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
//            // Adjust layout parameters to align text to the right
//            itemView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            itemView.setGravity(Gravity.END); // Align text to the right
//        }
    }
}