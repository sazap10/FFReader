package de.sachinpan.ffreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import de.sachinpan.ffreader.utils.Item;
import de.sachinpan.ffreader.utils.Utils;

public class ItemsActivity extends Activity implements OnRefreshListener,
		OnQueryTextListener {

	private String title = "";
	private boolean isCrossover = false;
	private String link = "";
	private ListView listView;
	private ArrayList<Item> itemLinks;
	private ItemAdapter adapter;
	// private EditText inputSearch;
	private PullToRefreshLayout mPullToRefreshLayout;
	private static View noConnectionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(Utils.TITLE);
		link = bundle.getString(Utils.LINK);
		isCrossover = bundle.getBoolean(Utils.CROSSOVER_FLAG);
		setContentView(R.layout.activity_items);
		noConnectionText = findViewById(R.id.connectTV);
		noConnectionText.setVisibility(View.INVISIBLE);
		// Show the Up button in the action bar.
		setupActionBar();
		setupLayout();
		itemLinks = new ArrayList<Item>();
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		// Now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(this)
		// Mark All Children as pullable
				.allChildrenArePullable()
				// Set the OnRefreshListener
				.listener(this)
				// Finally commit the setup to our PullToRefreshLayout
				.setup(mPullToRefreshLayout);
		new GetItems().execute();
	}

	private void setupLayout() {
		listView = (ListView) findViewById(R.id.listView);
		// inputSearch = (EditText) findViewById(R.id.editText1);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String storiesLink = ((Item) parent.getItemAtPosition(position))
						.getLink();
				String name = ((Item) parent.getItemAtPosition(position))
						.getTitle();
				Intent intent = new Intent(ItemsActivity.this,
						StoriesActivity.class);
				intent.putExtra(Utils.TITLE, name);
				intent.putExtra(Utils.LINK, storiesLink);
				startActivity(intent);

			}
		});

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(title);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.items, menu);
		getMenuInflater().inflate(R.menu.items_actionbar, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ArrayAdapter<String> arrayAdapter;
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.sort_alpha:
			Collections.sort(itemLinks, new AlphabeticalComparator());
			adapter.notifyDataSetChanged();
			return true;
		case R.id.sort_number:
			Collections.sort(itemLinks, new NumberOfStoriesComparator());
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetItems extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(ItemsActivity.this, null,
					"Loading items...");
		}

		protected Void doInBackground(Void... arg) {
			// Void list = new ArrayList<String>();
			String url = (isCrossover) ? Utils.BASE_URL + "crossovers/" + link
					+ "/?" : Utils.BASE_URL + link + "/?";
			Message msg = new Message();
			Integer textTochange = View.INVISIBLE;
			msg.obj = textTochange;
			mHandler.sendMessage(msg);
			try {
				itemLinks.clear();
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("#list_output div");
				for (Element itemLink : links) {
					Element linkTag = itemLink.select("a").first();
					Element spanTag = itemLink.select("span").first();
					Item item = new Item(linkTag.text(), spanTag.text()
							.substring(1, spanTag.text().length() - 1),
							linkTag.attr("abs:href"));
					itemLinks.add(item);
					// list.add(linkTag.text());
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg = new Message();
				textTochange = View.VISIBLE;
				msg.obj = textTochange;
				mHandler.sendMessage(msg);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			adapter = new ItemAdapter(ItemsActivity.this, R.layout.items_row,
					itemLinks);
			listView.setAdapter(adapter);
			if (mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshComplete();
			dialog.dismiss();
		}

	}

	class AlphabeticalComparator implements Comparator<Item> {
		@Override
		public int compare(Item a, Item b) {
			return a.getTitle().compareToIgnoreCase(b.getTitle());
		}
	}

	class NumberOfStoriesComparator implements Comparator<Item> {
		@Override
		public int compare(Item a, Item b) {
			/*
			 * String strNumA = a.getNumberOfStories(); String strNumB =
			 * b.getNumberOfStories(); double numA =
			 * (strNumA.charAt(strNumA.length() - 1) == 'K') ? Double
			 * .parseDouble(strNumA.substring(0, strNumA.length() - 1)) * 1000 :
			 * Double.parseDouble(strNumA); double numB =
			 * (strNumB.charAt(strNumB.length() - 1) == 'K') ? Double
			 * .parseDouble(strNumB.substring(0, strNumB.length() - 1)) * 1000 :
			 * Double.parseDouble(strNumB);
			 */
			double numA = a.getNumberOfStories();
			double numB = b.getNumberOfStories();
			return numA > numB ? -1 : numA == numB ? 0 : 1;
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		new GetItems().execute();
	}

	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int visible = (Integer) msg.obj;
			noConnectionText.setVisibility(visible);
			// call setText here
		}
	};

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(arg0)) {
			adapter.getFilter().filter("");
			listView.clearTextFilter();
		} else {
			adapter.getFilter().filter(arg0.toString());
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
