package com.example.androidproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class CustomCardView extends CardView {

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.card_item_layout, this, true);
        TextView textView = findViewById(R.id.cardText);
        ImageView imageView = findViewById(R.id.cardIcon);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomCardView);
            String text = a.getString(R.styleable.CustomCardView_cardText);
            int icon = a.getResourceId(R.styleable.CustomCardView_cardIcon, -1);
            if (text != null) textView.setText(text);
            if (icon != -1) imageView.setImageResource(icon);
            a.recycle();
        }
    }
}
