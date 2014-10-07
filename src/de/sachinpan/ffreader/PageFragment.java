package de.sachinpan.ffreader;

import de.sachinpan.ffreader.utils.Utils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sachin on 23/01/14.
 */
public class PageFragment extends Fragment {
    private final static String PAGE_TEXT = "PAGE_TEXT";

    public static PageFragment newInstance(CharSequence pageText) {
        PageFragment frag = new PageFragment();
        Bundle args = new Bundle();
        args.putLong(PAGE_TEXT, Utils.getObjectStorage().putSharedObject(pageText));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CharSequence text = (CharSequence) Utils.getObjectStorage().getSharedObject(getArguments().getLong(PAGE_TEXT));
        View v = inflater.inflate(R.layout.fragment_reader, container, false);

        TextView pageView = (TextView) v.findViewById(R.id.textView);
        pageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
        pageView.setText(text);
        return v;
    }
}