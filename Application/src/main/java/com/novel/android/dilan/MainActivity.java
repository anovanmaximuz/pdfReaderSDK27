/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novel.android.dilan;

import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";

    Typeface face;
    Toolbar toolbarTop;
    TextView mTitle;
    TextView pagePosition;
    private PdfRendererBasicFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_real);

        // Get parameter
        int novel = getIntent().getIntExtra("novel",0);

        // Fonts
        face = Typeface.createFromAsset(getAssets(), "fonts/mileadilan.otf");

        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        toolbarTop.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbarTop);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        pagePosition = (TextView) toolbarTop.findViewById(R.id.page_position);
        mTitle.setTypeface(face);
        if (savedInstanceState == null) {
            String[] dilan = {"dilan_1.pdf","dilan_2.pdf","dilan_3.pdf"};
            Bundle bundle = new Bundle();
            bundle.putString("novel",dilan[novel]);
            mFragment = new PdfRendererBasicFragment();
            mFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                   .add(R.id.container, mFragment,
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
        }

        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void setTitleToolbar(String title){
        mTitle.setText(title);
    }

    public void setPage(String page){
        pagePosition.setText(page);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.intro_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
