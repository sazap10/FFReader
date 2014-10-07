package de.sachinpan.ffreader;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.manuelpeinado.numericpageindicator.NumericPageIndicator;



public class ReaderActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager pagesView;
	private NumericPageIndicator pageIndicator;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reader);
        pagesView = (ViewPager) findViewById(R.id.pager);
        pagesView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //Remove it here unless you want to get this callback for EVERY
                //layout pass, which can get you into infinite loops if you ever
                //modify the layout from within this method.

                int width = pagesView.getMeasuredWidth();
                int height = pagesView.getMeasuredHeight();
                Log.d("page view", "width= "+width+" height= "+height);

                PageSplitter pageSplitter = new PageSplitter(width, height, 1, 0);

                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
                String text = getString(R.string.lorem);
                for (int i = 0; i < 5; i++) {

                    pageSplitter.append(text, textPaint);

                }

                pagesView.setAdapter(new TextPagerAdapter(getSupportFragmentManager(), pageSplitter.getPages()));
                pageIndicator = (NumericPageIndicator) findViewById(R.id.pageIndicator);
                pageIndicator.setViewPager(pagesView);
                pageIndicator.setTextTemplate("#i/#N");
                pageIndicator.setTextColor(Color.argb(128, 255, 255, 255));
                pageIndicator.setPageNumberTextColor(Color.argb(192, 255, 255, 255));
                pageIndicator.setPageNumberTextBold(false);
                final float scale = getResources().getDisplayMetrics().density;
                pageIndicator.setTextSize((int)(12 * scale + 0.5f));
                pageIndicator.setTopPadding((int)(7 * scale + 0.5f));
                pageIndicator.setBottomPadding((int)(7 * scale + 0.5f));
                pageIndicator.setBackgroundColor(Color.rgb(47, 79, 79));
                pagesView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        
        
       /* PageSplitter pageSplitter = new PageSplitter(width-128, height-32, 1, 0);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        String text = getString(R.string.lorem);
        for (int i = 0; i < 5; i++) {

            pageSplitter.append(text, textPaint);

        }*/

       // pagesView.setAdapter(new TextPagerAdapter(getSupportFragmentManager(), pageSplitter.getPages()));
       // ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
       // Log.d("Reader","Height: "+pagesView.findViewById(R.id.textView).getMeasuredHeight()+"Width: "+pagesView.findViewById(R.id.textView).getMeasuredWidth() );
    }






}
