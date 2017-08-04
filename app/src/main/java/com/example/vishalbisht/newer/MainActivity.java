package com.example.vishalbisht.newer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "Newer";

    RecyclerView product;
    FrameLayout showJewel, showItem;

    List<Products> show;

    HorizontalAdapter Adapter1;
    Button inch, cm, line;
    Switch switchMode;

    String currentProduct;
    int productSelected;

    int parentHeight, parentWidth;
    ImageView imgJewel, imgItem, itemH, itemW, jewelH, jewelW;
    TextView itemHT, itemWT, jewelHT, jewelWT;

    RelativeLayout newJewelDisp, newItemDisp;
    RelativeLayout root;
    int index;
    float itemVertical[] = {1.5f, 7f, 1.6f, 2.5f};
    float itemHorizontal[] = {1.1f, 3.3f, 3.9f, 1.5f};

    float jewelRealHeight = 3.8f, jewelRealWidth = 1.4f;
    float itemHeight, itemWidth, jewelHeight, jewelWidth;
    int scalefactor;
    float dimenfactor = 1.0f;
    private float dX;
    private float dY;

    int jewelLocation[] = new int[2];
    int itemLocation[] = new int[2];

    private float mLastTouchX, mLastTouchY;
    int mode = 1;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    int downCounter=0,upCounter=0,moveCounter=0;


    /*String currentJewel;
    Horizontal Adapter2;
    int jewel_selected;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = loadData();
        //jewels=loadJewelData();

        product = (RecyclerView) findViewById(R.id.products);
        imgJewel = (ImageView) findViewById(R.id.jewel);
        imgJewel.bringToFront();
        imgItem = (ImageView) findViewById(R.id.item);

        newJewelDisp = (RelativeLayout) findViewById(R.id.fixedJewelFrame);
        newItemDisp = (RelativeLayout) findViewById(R.id.fixedItemFrame);
        root = (RelativeLayout) findViewById(R.id.root);

        showJewel = (FrameLayout) findViewById(R.id.jewelFrame);
        showItem = (FrameLayout) findViewById(R.id.itemFrame);

        itemH = (ImageView) findViewById(R.id.itemHeight);
        itemW = (ImageView) findViewById(R.id.itemWidth);
        jewelH = (ImageView) findViewById(R.id.jewelHeight);
        jewelW = (ImageView) findViewById(R.id.jewelWidth);

        itemHT = (TextView) findViewById(R.id.itemHeightText);
        itemWT = (TextView) findViewById(R.id.itemWidthText);
        jewelHT = (TextView) findViewById(R.id.jewelHeightText);
        jewelWT = (TextView) findViewById(R.id.jewelWidthText);

        newJewelDisp.setOnTouchListener(this);
        newJewelDisp.bringToFront();
        newItemDisp.setOnTouchListener(this);

        getParentPositions();

        inch = (Button) findViewById(R.id.inch);
        inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inch.setFocusable(true);
                inch.setFocusableInTouchMode(true);
                inch.requestFocus();
                dimenfactor = 2.54f;
                if (cm.hasFocus()) {
                    cm.clearFocus();
                }
                setItemDimens();
                setJewelDimens();
            }
        });

        cm = (Button) findViewById(R.id.cm);
        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.setFocusable(true);
                cm.setFocusableInTouchMode(true);
                cm.requestFocus();
                dimenfactor = 1.0f;
                if (inch.hasFocus()) {
                    inch.clearFocus();
                }
                setItemDimens();
                setJewelDimens();
            }
        });

        line = (Button) findViewById(R.id.line);
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jewelH.getVisibility() == View.VISIBLE) {
                    jewelH.setVisibility(View.INVISIBLE);
                    jewelW.setVisibility(View.INVISIBLE);
                    itemH.setVisibility(View.INVISIBLE);
                    itemW.setVisibility(View.INVISIBLE);
                    jewelHT.setVisibility(View.INVISIBLE);
                    jewelWT.setVisibility(View.INVISIBLE);
                    itemHT.setVisibility(View.INVISIBLE);
                    itemWT.setVisibility(View.INVISIBLE);

                } else {
                    jewelH.setVisibility(View.VISIBLE);
                    jewelW.setVisibility(View.VISIBLE);
                    itemH.setVisibility(View.VISIBLE);
                    itemW.setVisibility(View.VISIBLE);
                    jewelHT.setVisibility(View.VISIBLE);
                    jewelWT.setVisibility(View.VISIBLE);
                    itemHT.setVisibility(View.VISIBLE);
                    itemWT.setVisibility(View.VISIBLE);
                }
            }
        });

        switchMode = (Switch) findViewById(R.id.modeSwitch);
        switchMode.setChecked(false);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    mode=2;
                    upCounter=downCounter=moveCounter=0;
                } else {
                    mode=1;
                    upCounter=downCounter=moveCounter=0;
                }
            }
        });
        getDimens();
        scrollAdapter();
        //jewelAdapter();

    }

    private void getParentPositions() {
        newJewelDisp.getLocationOnScreen(jewelLocation);
        newItemDisp.getLocationOnScreen(itemLocation);
    }


    public List<Products> loadData() {

        List<Products> item = new ArrayList<>();
        item.add(new Products(R.drawable.memorycard, "SD Card"));
        item.add(new Products(R.drawable.scale, "Ruler"));
        item.add(new Products(R.drawable.chargerjack, "USB Plug"));
        item.add(new Products(R.drawable.simcard, "SIM Card"));
        return item;
    }


    public float convertDpToPixel(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void loadImage() {
        Glide.with(this).load(productSelected).fitCenter().into(imgItem);
        Glide.with(this).load(R.drawable.long_earring).fitCenter().into(imgJewel);
        //setTextValues();
        //lineResize();
    }

    public void scrollAdapter() {
        Adapter1 = new HorizontalAdapter(show, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        product.setLayoutManager(horizontalLayoutManager);
        product.setAdapter(Adapter1);
    }

    public void getScaleFactor() {

        itemHeight = convertDpToPixel(itemVertical[index]);
        itemWidth = convertDpToPixel(itemHorizontal[index]);
        jewelHeight = convertDpToPixel(jewelRealHeight);
        jewelWidth = convertDpToPixel(jewelRealWidth);
        float scalingCalc1 = parentHeight / itemHeight;
        float scalingCalc2 = parentWidth / itemWidth;
        float scalingCalc3 = parentHeight / jewelHeight;
        float scalingCalc4 = parentWidth / jewelWidth;
        double sf = Math.min(scalingCalc1, Math.min(scalingCalc2, Math.min(scalingCalc3, scalingCalc4)));
        scalefactor = (int) (sf - 5.0);

        //Resizing item image wrt scalefactor
        imgItem.getLayoutParams().height = (int) (itemHeight * scalefactor);
        imgItem.getLayoutParams().width = (int) (itemWidth * scalefactor);
        showItem.getLayoutParams().height = (int) (itemHeight * scalefactor);
        showItem.getLayoutParams().width = (int) (itemWidth * scalefactor);

        itemH.getLayoutParams().height = (int) (itemHeight * scalefactor);
        itemW.getLayoutParams().width = (int) (itemWidth * scalefactor);


        //Resizing jewel image wrt scalefactor
        imgJewel.getLayoutParams().height = (int) (jewelHeight * scalefactor);
        imgJewel.getLayoutParams().width = (int) (jewelWidth * scalefactor);
        showJewel.getLayoutParams().height = (int) (jewelHeight * scalefactor);
        showJewel.getLayoutParams().width = (int) (jewelWidth * scalefactor);

        jewelH.getLayoutParams().height = (int) (jewelHeight * scalefactor);
        jewelW.getLayoutParams().width = (int) (jewelWidth * scalefactor);

        if (imgItem.getHeight() < parentHeight && imgItem.getWidth() < parentWidth)
            if (imgJewel.getHeight() < parentHeight && imgJewel.getWidth() < parentWidth) {

                imgItem.invalidate();
                imgItem.requestLayout();

                itemH.invalidate();
                itemH.requestLayout();

                itemW.invalidate();
                itemW.requestLayout();

                showItem.invalidate();
                showItem.requestLayout();
                setItemDimens();

                newItemDisp.getLayoutParams().height = (int) (itemHeight * scalefactor) + 80;
                newItemDisp.getLayoutParams().width = (int) (itemWidth * scalefactor) + 80;


                imgJewel.invalidate();
                imgJewel.requestLayout();

                jewelH.invalidate();
                jewelH.requestLayout();

                jewelW.invalidate();
                jewelW.requestLayout();

                showJewel.invalidate();
                showJewel.requestLayout();
                setJewelDimens();

                newJewelDisp.getLayoutParams().height = (int) (jewelHeight * scalefactor) + 80;
                newJewelDisp.getLayoutParams().width = (int) (jewelWidth * scalefactor) + 80;


            }
    }

    private void setJewelDimens() {
        jewelHT.invalidate();
        jewelWT.invalidate();

        double height = jewelRealHeight / dimenfactor;
        height = Math.round(height * 10.0) / 10.0;
        jewelHT.setText(String.valueOf(height));

        double width = jewelRealWidth / dimenfactor;
        width = Math.round(width * 10.0) / 10.0;
        jewelWT.setText(String.valueOf(width));
    }

    private void setItemDimens() {
        itemHT.invalidate();
        itemWT.invalidate();

        double height = itemVertical[index] / dimenfactor;
        height = Math.round(height * 10.0) / 10.0;
        itemHT.setText(String.valueOf(height));

        double width = itemHorizontal[index] / dimenfactor;
        width = Math.round(width * 10.0) / 10.0;
        itemWT.setText(String.valueOf(width));
    }

    public void getDimens() {
        Log.v(TAG, "Got Parent Size");
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.fixedJewelFrame);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {

                parentWidth = layout.getMeasuredWidth() - 30;
                parentHeight = layout.getMeasuredHeight() - 30;

                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        int pointerCount = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downCounter++;
                Log.v(TAG,"Action Down"+downCounter);

                startClickTime = Calendar.getInstance().getTimeInMillis();

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();

                final float rawX = event.getRawX();
                final float rawY = event.getRawY();

                // Remember where we started
                mLastTouchX = rawX;
                mLastTouchY = rawY;
                break;

            case MotionEvent.ACTION_MOVE:
                moveCounter++;
                Log.v(TAG, "Action Move "+moveCounter);
                if (mode == 1) {

                    final float x1 = event.getRawX();
                    final float y1 = event.getRawY();

                    // Calculate the distance moved
                    final float dx = x1 - mLastTouchX;
                    final float dy = y1 - mLastTouchY;

                    // Make sure we will still be the in parent's container
                    Rect parent = new Rect(0, 0, root.getWidth(), root.getHeight());

                    //Find what the bounds of view will be after moving
                    int newLeft = (int) (view.getX() + dx),
                            newTop = (int) (view.getY() + dy),
                            newRight = newLeft + view.getWidth(),
                            newBottom = newTop + view.getHeight();

                    if (!parent.contains(newLeft, newTop, newRight, newBottom)) {
                        Log.v(TAG, "Out of Bounds");
                    } else {
                        // Move the object
                        view.animate().
                                x(x1 + dX).
                                y(y1 + dY).
                                setDuration(0).
                                start();
                        // Remember this touch position for the next move event
                        mLastTouchX = x1;
                        mLastTouchY = y1;
                        // Invalidate to request a redraw
                        root.invalidate();
                        break;
                    }
                }
                if(mode==2)
                //if (pointerCount == 2)
                {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if(clickDuration > MAX_CLICK_DURATION) {
                        //Log.v(TAG, "for rotation");

                        float degree = rotation(event, view.getWidth() / 2, view.getHeight() / 2);

                        //Log.v(TAG, "setting at angle" + degree);

                        view.setRotation(view.getRotation() + degree);
                        break;

                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                upCounter++;
                Log.v(TAG,"Action Up"+upCounter);


           /* case MotionEvent.ACTION_POINTER_DOWN:
                Log.v(TAG, "ACTION POINTER DOWN");
                if (pointerCount == 2) {
                    flagFirstTouch = 1;
                }
                break;*/

        }
        return true;
    }

    private float rotation(MotionEvent event, float midWidth,float midHeight) {
       // Log.v(TAG, "degree calculation");

        double delta_x = (event.getX(0) - midWidth);
        double delta_y = (event.getY(0) - midHeight);

        double radians = Math.atan2(delta_y, delta_x);

        float degree = (float) Math.toDegrees(radians);
        Log.v(TAG, "Angle = " + degree);

        return degree;
    }

    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        List<Products> HorizontalList = Collections.emptyList();
        Context context;


        HorizontalAdapter(List<Products> horizontalList, Context context) {
            this.HorizontalList = horizontalList;
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView txtview;

            MyViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.imageview);
                txtview = view.findViewById(R.id.txtview);
            }
        }

        @Override
        public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_menu, parent, false);
            return new MyViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final HorizontalAdapter.MyViewHolder holder, final int position) {
            holder.imageView.setImageResource(HorizontalList.get(position).imageId);

            holder.txtview.setText(HorizontalList.get(position).name);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentProduct = HorizontalList.get(position).name;
                    productSelected = HorizontalList.get(position).imageId;
                    index = position;

                    getScaleFactor();

                    loadImage();
                    //Toast.makeText(MainActivity.this, list, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return HorizontalList.size();
        }
    }

}
