package edu.nyu.cs.omnidroid.tests;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.nyu.cs.omnidroid.external.eventapp.ExternalCPActivity;
import edu.nyu.cs.omnidroid.ui.CheckListViewDemo;
import edu.nyu.cs.omnidroid.ui.PackageLister;

/**
 * This class will present a list of test applications that we can run via UI selection.
 * 
 * @author acase
 */
public class TestApp extends ListActivity {

  static final String[] TESTS = new String[] {
      "Test CP",
      "Test Service",
      "PackageLister",
      "External Event App",
      "Test Fancy List",
      "Test User Config",
      "Test App Config",
      "Test Exception Handler"
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.i(this.getLocalClassName(), "onCreate");
    super.onCreate(savedInstanceState);
    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TESTS));
    getListView().setTextFilterEnabled(true);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Log.i(this.getLocalClassName(), "Enter");
    Log.i(this.getLocalClassName(), "listview: " + l);
    Log.i(this.getLocalClassName(), "view: " + v);
    Log.i(this.getLocalClassName(), "position: " + position);
    Log.i(this.getLocalClassName(), "id: " + id);

    Intent i = new Intent();

    switch (position) {
    case 5:
      i.setClass(this.getApplicationContext(), TestUserConfig.class);
      break;
    case 6:
      i.setClass(this.getApplicationContext(), TestAppConfig.class);
      break;
    case 0:
      i.setClass(this.getApplicationContext(), TestCP.class);
      break;
    case 7:
      i.setClass(this.getApplicationContext(), TestExceptionHandler.class);
      break;
    case 1:
      i.setClass(this.getApplicationContext(), TestService.class);
      break;
    case 2:
      i.setClass(this.getApplicationContext(), PackageLister.class);
      break;
    case 3:
      i.setClass(this.getApplicationContext(), ExternalCPActivity.class);
      break;
    case 4:
      i.setClass(this.getApplicationContext(), CheckListViewDemo.class);
      break;
    default:
      Log.i(this.getLocalClassName(), "Invalid Test Selection");
      // TODO (acase): Catch this as an exception
      break;
    }
    startActivity(i);
  }
}