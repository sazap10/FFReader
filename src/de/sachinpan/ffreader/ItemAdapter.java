package de.sachinpan.ffreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import de.sachinpan.ffreader.utils.Item;

public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {

	Context context;
	int layoutResourceId;
	ArrayList<Item> data = null;
	private ArrayList<Item> origData;

	public ItemAdapter(Context context, int layoutResourceId,
			ArrayList<Item> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.origData = new ArrayList<Item>(this.data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ItemHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ItemHolder();
			holder.textNum = (TextView) row.findViewById(R.id.textNum);
			holder.textTitle = (TextView) row.findViewById(R.id.textTitle);

			row.setTag(holder);
		} else {
			holder = (ItemHolder) row.getTag();
		}

		Item item = data.get(position);
		holder.textTitle.setText(item.getTitle());
		holder.textNum.setText(item.getNumberOfStoriesStr());

		return row;
	}

	static class ItemHolder {
		TextView textNum;
		TextView textTitle;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					List<Item> founded = new ArrayList<Item>();
					for (Item item : data) {
						if (item.toString().toLowerCase().contains(constraint)) {
							founded.add(item);
						}
					}
					result.values = founded;
					result.count = founded.size();
				} else {
					result.values = origData;
					result.count = origData.size();
				}
				return result;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				clear();
				for (Item item : (List<Item>) results.values) {
					add(item);
				}
				notifyDataSetChanged();
			}
		};
	}
}