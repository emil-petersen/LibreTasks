/*******************************************************************************
 * Copyright 2009, 2010 OmniDroid - http://code.google.com/p/omnidroid 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *******************************************************************************/
package edu.nyu.cs.omnidroid.app.view.simple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.nyu.cs.omnidroid.app.R;
import edu.nyu.cs.omnidroid.app.controller.external.attributes.EventMonitoringService;
import edu.nyu.cs.omnidroid.app.model.db.DbHelper;

/**
 * This is the main entry point of the application. Here the user will see a main menu where they
 * can choose to create a new rule, view existing rules, or see help items.
 */
public class ActivityMain extends Activity {
  private SharedPreferences settings;
  private static final int SETTINGS_ID = 0;
  private static final int LOG_ID = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize singleton instance of UIDbHelperStore, which is
    // our connection to the omnidroid database.
    UIDbHelperStore.init(this);

    // Make sure the background monitoring service is running.
    EventMonitoringService.startService(this);

    // Link up click handlers with their buttons.
    Button btnCreateRule = (Button) findViewById(R.id.activity_main_btnCreateRule);
    btnCreateRule.setOnClickListener(listenerBtnClickCreateRule);

    Button btnViewRules = (Button) findViewById(R.id.activity_main_btnViewRules);
    btnViewRules.setOnClickListener(listenerBtnClickViewRules);

    Button btnViewLogs = (Button) findViewById(R.id.activity_main_btnLogs);
    btnViewLogs.setOnClickListener(listenerBtnClickViewLogs);

    Button btnHelp = (Button) findViewById(R.id.activity_main_btnHelp);
    btnHelp.setOnClickListener(listenerBtnClickHelp);

    Button btnResetDB = (Button) findViewById(R.id.activity_main_btnResetDB);
    btnResetDB.setOnClickListener(listenerBtnClickResetDb);

    settings = UIDbHelperStore.instance().db().getSharedPreferences();
    if (settings.getBoolean(DbHelper.SETTING_ACCEPTED_DISCLAIMER, false) == false) {
      showDisclaimer();
    }

  }

  /**
   * Display our disclaimer dialog and require acceptance.
   */
  private void showDisclaimer() {
    Builder welcome = new AlertDialog.Builder(this);
    welcome.setTitle(R.string.disclaimer_title);
    welcome.setIcon(R.drawable.icon);
    welcome.setMessage(Html.fromHtml(getString(R.string.disclaimer_msg)));
    welcome.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        setDisclaimerAccepted(true);
      }
    });
    welcome.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        setDisclaimerAccepted(false);
        finish();
      }
    });
    welcome.show();
  }

  private void setDisclaimerAccepted(boolean accepted) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putBoolean(DbHelper.SETTING_ACCEPTED_DISCLAIMER, accepted);
    editor.commit();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    UIDbHelperStore.instance().releaseResources();
  }

  /** Create a options menu for the main screen */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, LOG_ID, Menu.NONE, getString(R.string.logs))
        .setAlphabeticShortcut('l');
    menu.add(Menu.NONE, SETTINGS_ID, Menu.NONE, getString(R.string.settings_label))
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setAlphabeticShortcut('s');
    return super.onCreateOptionsMenu(menu);
  }

  /** Called when an item of options menu is clicked */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case LOG_ID:
      // TODO (Fang Huang): migrate View Logs here
      return true;
    case SETTINGS_ID:
      startActivity(new Intent(this, ActivitySettings.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Launch the create-a-new-rule activity.
   */
  private OnClickListener listenerBtnClickCreateRule = new OnClickListener() {
    public void onClick(View v) {
      // User wants to create a new rule, move them to the ActivityChooseRootEvent
      // activity so they can choose the root event.
      ActivityChooseRootEvent.resetUI(v.getContext());
      Intent intent = new Intent();
      intent.setClass(getApplicationContext(), ActivityChooseRootEvent.class);
      startActivity(intent);
    }
  };

  /**
   * View saved rules.
   */
  private OnClickListener listenerBtnClickViewRules = new OnClickListener() {
    public void onClick(View v) {
      ActivitySavedRules.resetUI(v.getContext());
      Intent intent = new Intent();
      intent.setClass(getApplicationContext(), ActivitySavedRules.class);
      startActivity(intent);
    }
  };

  /**
   * View logs.
   */
  private OnClickListener listenerBtnClickViewLogs = new OnClickListener() {
    public void onClick(View v) {
      ActivityLogs.resetUI(v.getContext());
      Intent intent = new Intent();
      intent.setClass(getApplicationContext(), ActivityLogs.class);
      startActivity(intent);
    }
  };

  /**
   * Help info.
   */
  private OnClickListener listenerBtnClickHelp = new OnClickListener() {
    public void onClick(View v) {
      Builder help = new AlertDialog.Builder(v.getContext());
      help.setTitle(R.string.help);
      help.setIcon(R.drawable.icon);
      help.setMessage(Html.fromHtml(getString(R.string.help_activitymain)));
      help.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        }
      });
      help.show();
    }
  };

  /**
   * Cleanup the Database, all info will be reset, user set rules will be lost
   */
  private OnClickListener listenerBtnClickResetDb = new OnClickListener() {
    public void onClick(View v) {
      // Show a dialog to ask users if they're sure they want to cleanup the Database.
      new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(
          getString(R.string.reset_settings)).setPositiveButton(getString(R.string.ok),
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              UIDbHelperStore.instance().db().resetDB();
            }
          }).setNegativeButton(getString(R.string.cancel), null).show();
    }
  };
}