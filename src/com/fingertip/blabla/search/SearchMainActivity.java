package com.fingertip.blabla.search;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseFragmentActivity;
import com.fingertip.blabla.util.http.EventUtil.Type;

public class SearchMainActivity extends BaseFragmentActivity{
	
	private SearchFragment fragment_nearby;
	private SearchFragment fragment_hot;
	private SearchFragment fragment_recent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchmain);
		setupViews();
	}

	private void setupViews() {
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		if (fragment_nearby == null) 
			fragment_nearby = new SearchFragment(this, Type.nearest);
		getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, fragment_nearby).commit();
		((RadioGroup)findViewById(R.id.radiogroup)).setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
				if(checkId == R.id.radio2){
					if (fragment_hot == null) 
						fragment_hot = new SearchFragment(SearchMainActivity.this, Type.hotest);
					getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, fragment_hot).commit();
				}else if(checkId == R.id.radio3){
					if (fragment_recent == null) 
						fragment_recent = new SearchFragment(SearchMainActivity.this, Type.newest);
					getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, fragment_recent).commit();
				}else{
					if (fragment_nearby == null) 
						fragment_nearby = new SearchFragment(SearchMainActivity.this, Type.nearest);
					getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, fragment_nearby).commit();
				}
			}
		});
	}
}
