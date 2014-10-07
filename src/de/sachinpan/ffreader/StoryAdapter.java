package de.sachinpan.ffreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.sachinpan.ffreader.utils.Story;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class StoryAdapter extends ArrayAdapter<Story> {

	Context context;
	int layoutResourceId;
	ArrayList<Story> data = null;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

	public StoryAdapter(Context context, int layoutResourceId,
			ArrayList<Story> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StoryHolder();
			holder.storyTitle = (TextView) row.findViewById(R.id.story_title);
			holder.storyAuthor = (TextView) row.findViewById(R.id.story_author);
			holder.storyDescription = (TextView) row
					.findViewById(R.id.story_desc);
			holder.storyChapters = (TextView) row.findViewById(R.id.story_num);

			row.setTag(holder);
		} else {
			holder = (StoryHolder) row.getTag();
		}

		Story story = data.get(position);
		holder.storyTitle.setText(story.getTitle());
		holder.storyAuthor.setText(story.getAuthor());
		holder.storyDescription.setText(story.getDescription());
		holder.storyChapters
				.setText("Chapters: " + story.getNumberOfChapters());

		return row;
	}

	static class StoryHolder {
		TextView storyTitle;
		TextView storyAuthor;
		TextView storyDescription;
		TextView storyChapters;
	}

	public void setNewSelection(int position, boolean value) {
		mSelection.put(position, value);
		notifyDataSetChanged();
	}

	public boolean isPositionChecked(int position) {
		Boolean result = mSelection.get(position);
		return result == null ? false : result;
	}

	public Set<Integer> getCurrentCheckedPosition() {
		return mSelection.keySet();
	}

	public void removeSelection(int position) {
		mSelection.remove(position);
		notifyDataSetChanged();
	}

	public void clearSelection() {
		mSelection = new HashMap<Integer, Boolean>();
		notifyDataSetChanged();
	}

}