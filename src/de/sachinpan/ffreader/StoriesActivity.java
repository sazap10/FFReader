package de.sachinpan.ffreader;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import de.sachinpan.ffreader.utils.Filter;
import de.sachinpan.ffreader.utils.Story;
import de.sachinpan.ffreader.utils.Utils;

public class StoriesActivity extends FragmentActivity implements
		OnRefreshListener {
	private String title = "";
	private String link = "";
	private ListView listView;
	private ArrayList<Story> stories;
	private StoryAdapter adapter;
	private static ArrayList<Filter> sort;
	private static ArrayList<Filter> timerange;
	private static ArrayList<Filter> genre1;
	private static ArrayList<Filter> genre2;
	private static ArrayList<Filter> censor;
	private static ArrayList<Filter> language;
	private static ArrayList<Filter> length;
	private static ArrayList<Filter> status;
	private static ArrayList<Filter> character1;
	private static ArrayList<Filter> character2;
	private static ArrayList<Filter> character3;
	private static ArrayList<Filter> character4;
	private static ArrayList<Filter> withoutGenre1;
	private static ArrayList<Filter> withoutCharacter1;
	private static ArrayList<Filter> withoutCharacter2;
	public static int[] filterOptions;
	public static boolean plusPairing = false, withoutPairing = false;
	public static String filter = "";
	public static final String DEFAULT_FILTER = "?&srt=1&r=10";
	private PullToRefreshLayout mPullToRefreshLayout;
	static View noConnectionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(Utils.TITLE);
		link = bundle.getString(Utils.LINK);
		setContentView(R.layout.activity_stories);
		noConnectionText = findViewById(R.id.connectTV);
		noConnectionText.setVisibility(View.INVISIBLE);
		stories = new ArrayList<Story>();
		setupActionBar();
		setupListView();

		sort = new ArrayList<Filter>();
		timerange = new ArrayList<Filter>();
		genre1 = new ArrayList<Filter>();
		genre2 = new ArrayList<Filter>();
		censor = new ArrayList<Filter>();
		language = new ArrayList<Filter>();
		length = new ArrayList<Filter>();
		status = new ArrayList<Filter>();
		character1 = new ArrayList<Filter>();
		character2 = new ArrayList<Filter>();
		character3 = new ArrayList<Filter>();
		character4 = new ArrayList<Filter>();
		withoutGenre1 = new ArrayList<Filter>();
		withoutCharacter1 = new ArrayList<Filter>();
		withoutCharacter2 = new ArrayList<Filter>();
		filter = DEFAULT_FILTER;
		filterOptions = new int[15];
		// Now find the PullToRefreshLayout to setup
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		// Now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(this)
		// Mark All Children as pullable
				.allChildrenArePullable()
				// Set the OnRefreshListener
				.listener(this)
				// Finally commit the setup to our PullToRefreshLayout
				.setup(mPullToRefreshLayout);
		new GetStories().execute(1);
	}

	private void setupListView() {
		listView = (ListView) findViewById(R.id.listView);
		adapter = new StoryAdapter(StoriesActivity.this, R.layout.stories_row,
				stories);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new EndlessScrollListener());
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private int nr = 0;

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				adapter.clearSelection();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				nr = 0;
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.item_download:
					nr = 0;
					for(Integer itemPosition: adapter.getCurrentCheckedPosition()){
						Log.d("Selected Stories", adapter.getItem(itemPosition).getLink());
					}
					adapter.clearSelection();
					mode.finish();
				}
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					nr++;
					adapter.setNewSelection(position, checked);
				} else {
					nr--;
					adapter.removeSelection(position);
				}
				mode.setTitle(nr + " selected");

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				listView.setItemChecked(position,
						!adapter.isPositionChecked(position));
				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stories, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ArrayAdapter<String> arrayAdapter;
		switch (item.getItemId()) {
		case R.id.filter:
			showDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupActionBar() {
		setTitle(title);

	}

	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int visible = (Integer) msg.obj;
			noConnectionText.setVisibility(visible);
			// call setText here
		}
	};

	private class GetStories extends AsyncTask<Integer, Void, Void> {
		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(StoriesActivity.this, null,
					"Loading stories...");
		}

		protected Void doInBackground(Integer... arg) {
			Message msg = new Message();
			Integer textTochange = View.INVISIBLE;
			msg.obj = textTochange;
			mHandler.sendMessage(msg);
			// noConnectionText.setVisibility(View.INVISIBLE);
			// Void list = new ArrayList<String>();
			String url = link + filter + "&p=" + arg[0];
			try {
				if (arg[0] == 1) {
					stories.clear();
				}
				Log.d("Stories URL", url);
				Document doc = Jsoup.connect(url).get();
				Elements filterForm = doc.select("#myform select");
				for (Element selectTag : filterForm) {
					String nameAttr = selectTag.attr("name");
					if (nameAttr.equals("sortid")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							sort.add(filter);
						}
					}
					if (nameAttr.equals("timerange")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							timerange.add(filter);
						}
					}
					if (nameAttr.equals("genreid1")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							genre1.add(filter);
						}
					}
					if (nameAttr.equals("genreid2")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							genre2.add(filter);
						}
					}
					if (nameAttr.equals("censorid")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							censor.add(filter);
						}
					}
					if (nameAttr.equals("languageid")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							language.add(filter);
						}
					}
					if (nameAttr.equals("length")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							length.add(filter);
						}
					}
					if (nameAttr.equals("statusid")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							status.add(filter);
						}
					}
					if (nameAttr.equals("characterid1")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							character1.add(filter);
						}
					}
					if (nameAttr.equals("characterid2")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							character2.add(filter);
						}
					}
					if (nameAttr.equals("characterid3")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							character3.add(filter);
						}
					}
					if (nameAttr.equals("characterid4")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							character4.add(filter);
						}
					}
					if (nameAttr.equals("_genreid1")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							withoutGenre1.add(filter);
						}
					}
					if (nameAttr.equals("_characterid1")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							withoutCharacter1.add(filter);
						}
					}
					if (nameAttr.equals("_characterid2")) {
						Elements optionTags = selectTag.select("option");
						for (Element options : optionTags) {
							Filter filter = new Filter(options.val(), options
									.text().replaceAll(".*: (.*)", "$1"));
							withoutCharacter2.add(filter);
						}
					}
				}
				Elements storyDivs = doc.select(".z-list.zhover.zpointer");
				// Log.d("Story desc",storyDivs.first().select("div").text());
				for (Element storyDiv : storyDivs) {
					Element storyTitle = storyDiv.select("a.stitle").first();
					// Log.d("title", storyTitle.text());
					Element storyAuthor = storyDiv.select(
							"a:not(:has(span),.stitle)").first();
					Element storyDescription = storyDiv.select(
							"div.z-indent.z-padtop").first();
					Element storyChapters = storyDescription.select(
							"div.z-padtop2.xgray").first();
					String chapters = storyChapters.text().replaceAll(
							".*Chapters: (\\d*).*", "$1");
					// Log.d("StoryChapter",chapters);
					Story story = new Story(storyTitle.text(),
							storyAuthor.text(), storyDescription.ownText(),
							chapters, storyTitle.attr("abs:href"));
					stories.add(story);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg = new Message();
				textTochange = View.VISIBLE;
				msg.obj = textTochange;
				mHandler.sendMessage(msg);
				// noConnectionText.setVisibility(View.VISIBLE);
			}

			return null;
		}

		protected void onPostExecute(Void result) {

			adapter.notifyDataSetChanged();
			if (mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshComplete();
			dialog.dismiss();
		}

	}

	void showDialog() {
		DialogFragment newFragment = new FilterDialogFragment();
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	public static String filterString(int position, int option, int value,
			String prepend) {
		if (position > -1) {
			filterOptions[position] = option;
		}
		return value > 0 ? prepend + String.valueOf(value) : "";
	}

	void getStories() {
		new GetStories().execute(1);
	}

	public static class FilterDialogFragment extends DialogFragment {
		Spinner sortSpinner, timerangeSpinner, genre1Spinner, genre2Spinner,
				censorSpinner, languageSpinner, lengthSpinner, statusSpinner,
				character1Spinner, character2Spinner, character3Spinner,
				character4Spinner, without_genre1Spinner,
				without_character1Spinner, without_character2Spinner;
		CheckBox plusCB, withoutCB;

		public FilterDialogFragment() {

		}

		public void processFilter() {
			filter = "?";
			filter += filterString(0, sortSpinner.getSelectedItemPosition(),
					((Filter) sortSpinner.getSelectedItem()).getValue(),
					"&srt=");
			filter += filterString(1,
					timerangeSpinner.getSelectedItemPosition(),
					((Filter) timerangeSpinner.getSelectedItem()).getValue(),
					"&t=");
			filter += filterString(2, genre1Spinner.getSelectedItemPosition(),
					((Filter) genre1Spinner.getSelectedItem()).getValue(),
					"&g1=");
			filter += filterString(3, genre2Spinner.getSelectedItemPosition(),
					((Filter) genre2Spinner.getSelectedItem()).getValue(),
					"&g2=");
			filter += filterString(4, censorSpinner.getSelectedItemPosition(),
					((Filter) censorSpinner.getSelectedItem()).getValue(),
					"&r=");
			filter += filterString(5,
					languageSpinner.getSelectedItemPosition(),
					((Filter) languageSpinner.getSelectedItem()).getValue(),
					"&lan=");
			filter += filterString(6, lengthSpinner.getSelectedItemPosition(),
					((Filter) lengthSpinner.getSelectedItem()).getValue(),
					"&len=");
			filter += filterString(7, statusSpinner.getSelectedItemPosition(),
					((Filter) statusSpinner.getSelectedItem()).getValue(),
					"&s=");
			filter += filterString(8,
					character1Spinner.getSelectedItemPosition(),
					((Filter) character1Spinner.getSelectedItem()).getValue(),
					"&c1=");
			filter += filterString(9,
					character2Spinner.getSelectedItemPosition(),
					((Filter) character2Spinner.getSelectedItem()).getValue(),
					"&c2=");
			filter += filterString(10,
					character3Spinner.getSelectedItemPosition(),
					((Filter) character3Spinner.getSelectedItem()).getValue(),
					"&c3=");
			filter += filterString(11,
					character4Spinner.getSelectedItemPosition(),
					((Filter) character4Spinner.getSelectedItem()).getValue(),
					"&c4=");
			filter += filterString(12,
					without_genre1Spinner.getSelectedItemPosition(),
					((Filter) without_genre1Spinner.getSelectedItem())
							.getValue(), "&_g1=");
			filter += filterString(13,
					without_character1Spinner.getSelectedItemPosition(),
					((Filter) without_character1Spinner.getSelectedItem())
							.getValue(), "&_c1=");
			filter += filterString(14,
					without_character2Spinner.getSelectedItemPosition(),
					((Filter) without_character2Spinner.getSelectedItem())
							.getValue(), "&_c2=");
			if (plusPairing = plusCB.isChecked()) {
				filter += filterString(-1, -1, 1, "&pm=");
			}
			if (withoutPairing = withoutCB.isChecked()) {
				filter += filterString(-1, -1, 1, "&_pm=");

			}

		}

		private void setupSpinners(View root) {
			sortSpinner = (Spinner) root.findViewById(R.id.sort);
			timerangeSpinner = (Spinner) root.findViewById(R.id.timerange);
			genre1Spinner = (Spinner) root.findViewById(R.id.genre1);
			genre2Spinner = (Spinner) root.findViewById(R.id.genre2);
			censorSpinner = (Spinner) root.findViewById(R.id.censor);
			languageSpinner = (Spinner) root.findViewById(R.id.language);
			lengthSpinner = (Spinner) root.findViewById(R.id.length);
			statusSpinner = (Spinner) root.findViewById(R.id.status);
			character1Spinner = (Spinner) root.findViewById(R.id.character1);
			character2Spinner = (Spinner) root.findViewById(R.id.character2);
			character3Spinner = (Spinner) root.findViewById(R.id.character3);
			character4Spinner = (Spinner) root.findViewById(R.id.character4);
			without_genre1Spinner = (Spinner) root
					.findViewById(R.id.without_genre1);
			without_character1Spinner = (Spinner) root
					.findViewById(R.id.without_character1);
			without_character2Spinner = (Spinner) root
					.findViewById(R.id.without_character2);
			plusCB = (CheckBox) root.findViewById(R.id.plusPairingCB);
			withoutCB = (CheckBox) root.findViewById(R.id.withoutPairingCB);

			setSpinnerAdapter(sortSpinner, sort, 0);
			setSpinnerAdapter(timerangeSpinner, timerange, 1);
			setSpinnerAdapter(genre1Spinner, genre1, 2);
			setSpinnerAdapter(genre2Spinner, genre2, 3);
			setSpinnerAdapter(censorSpinner, censor, 4);
			setSpinnerAdapter(languageSpinner, language, 5);
			setSpinnerAdapter(lengthSpinner, length, 6);
			setSpinnerAdapter(statusSpinner, status, 7);
			setSpinnerAdapter(character1Spinner, character1, 8);
			setSpinnerAdapter(character2Spinner, character2, 9);
			setSpinnerAdapter(character3Spinner, character3, 10);
			setSpinnerAdapter(character4Spinner, character4, 11);
			setSpinnerAdapter(without_genre1Spinner, withoutGenre1, 12);
			setSpinnerAdapter(without_character1Spinner, withoutCharacter1, 13);
			setSpinnerAdapter(without_character2Spinner, withoutCharacter2, 14);
			plusCB.setChecked(plusPairing);
			withoutCB.setChecked(withoutPairing);

		}

		private void setSpinnerAdapter(Spinner spinner,
				ArrayList<Filter> items, int i) {
			ArrayAdapter<Filter> adapter = new ArrayAdapter<Filter>(
					getActivity(), android.R.layout.simple_spinner_item, items);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setSelection(filterOptions[i]);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.stories_filter_dialog,
					null);
			TextView plusTV = (TextView) dialogView.findViewById(R.id.plusTV);
			plusTV.setText(Html.fromHtml("<font color=\"#04761c\">" + "Plus"
					+ "</font>" + " Filters"));
			TextView withoutTV = (TextView) dialogView
					.findViewById(R.id.withoutTV);
			withoutTV.setText(Html.fromHtml("<font color=\"#c00000\">"
					+ "Without" + "</font>" + " Filters"));
			setupSpinners(dialogView);
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog
			// layout
			return new AlertDialog.Builder(getActivity())
					.setView(dialogView)
					.setTitle("Filter Stories")
					.setPositiveButton("Filter",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									processFilter();
									Log.d("Stories", filter);
									((StoriesActivity) getActivity())
											.getStories();
								}
							}).setNegativeButton("Cancel", null).create();
		}
	}

	public class EndlessScrollListener implements OnScrollListener {

		private int visibleThreshold = 1;
		private int currentPage = 1;
		private int previousTotal = 0;
		private boolean loading = true;

		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// int previous = 0;
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					currentPage++;
				}
			}
			if (!loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				// I load the next page of stories using a background task,
				// but you can call any function here.
				new GetStories().execute(currentPage + 1);
				loading = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getStories();
	}
}
