package com.themike10452.purityu2d;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

public class DownloadActivity extends Activity {
    public static DownloadActivity THIS;
    private Activity thisActivity;
    private String[] INF;
    private final View.OnClickListener download = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (thisActivity != null) {
                final Intent intent = new Intent(thisActivity, DownloadService.class);
                intent.putExtra("0x0", INF[1]);
                intent.putExtra("0x1", INF[2]);
                intent.putExtra("0x2", ((CheckBox) findViewById(R.id.kmCB)).isChecked());
                intent.putExtra("0x3", ((CheckBox) findViewById(R.id.cdCB)).isChecked());
                (findViewById(R.id.button1Layout)).setVisibility(View.GONE);
                (findViewById(R.id.options)).setVisibility(View.GONE);
                (findViewById(R.id.message_downloading)).setVisibility(View.VISIBLE);
                startService(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if (THIS == null)
            THIS = this;

        thisActivity = this;
        INF = getIntent().getExtras().getString("0x0").split(">>");

        Button btnDownload = (Button) findViewById(R.id.btnDownload);
        TextView versionDisplay = (TextView) findViewById(R.id.newVersion);
        versionDisplay.setText(INF[0]);
        btnDownload.setText(INF[1]);
        btnDownload.setOnClickListener(download);
        ((TextView) findViewById(R.id.line01)).setText(
                String.format(
                        getString(R.string.info_line1),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                + File.separator + INF[1].trim()
                )
        );
        ((TextView) findViewById(R.id.line02)).setText(
                String.format(
                        getString(R.string.info_line2),
                        Environment.getExternalStorageDirectory() + File.separator + lib.onPostInstallFolder
                )
        );
        ((TextView) findViewById(R.id.line03)).setText(getString(R.string.info_line3));
        if (INF.length >= 4 && INF[3].trim().length() > 0)
            ((TextView) findViewById(R.id.noteDisplay)).setText(INF[3]);
        else
            findViewById(R.id.notes).setVisibility(View.GONE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(AutoCheckService.NOTIFICATION_TAG, AutoCheckService.NOTIFICATION_ID);

        if (!(new File(Environment.getExternalStorageDirectory() + "/TWRP")).isDirectory()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_twrpNF))
                    .setMessage(getString(R.string.messgae_failue_TWRP))
                    .setIcon(R.drawable.purity)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.button_proceed), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(getString(R.string.button_leave), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(thisActivity);
                dialog.setMessage(getString(R.string.dialog_message_su));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    Shell.SU.run("su -v").get(0);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                dialog.dismiss();
                if (!b) {
                    new AlertDialog.Builder(thisActivity)
                            .setTitle("SUPERUSER")
                            .setMessage(getString(R.string.message_failure_su))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.button_leave), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void updateMessage(int resID) {
        ((TextView) findViewById(R.id.message_downloading)).setText(getString(resID));
    }
}
