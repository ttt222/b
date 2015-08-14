package com.fingertip.blabla.my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.ContactEntity;
import com.fingertip.blabla.my.adapter.AdapterContact;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.DefaultCallback;
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.fingertip.blabla.util.http.UserUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MyContactsActivity extends BaseNavActivity implements View.OnClickListener {
	
	private LinearLayout empty_area;
	private ListView listView;
	private AdapterContact listAdapter;
	private List<ContactEntity> list;
	private UserSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contacts);
		findViews();
		setupViews();
		loadData();
	}
	
	protected void findViews() {
		super.findViews();
		empty_area = (LinearLayout) findViewById(R.id.contact_empty);
		listView = (ListView) findViewById(R.id.contact_listView);
		listAdapter = new AdapterContact(this, empty_area);
		listView.setAdapter(listAdapter);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_contact);
		session = UserSession.getInstance();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactEntity contact = (ContactEntity)listAdapter.getItem(position);
				if (contact.status == ContactEntity.REGISTERED)
					watch(contact);
				else if (contact.status == ContactEntity.NOT_REGISTERED)
					sendSms(contact);
			}
		});
	}
	
	private void loadData() {
		
		list = getLocalContactsInfos();
		listAdapter.addAllList(list);
		
		if (!list.isEmpty()){
			showProgressDialog(false);
			checkReg();
		}
	}

	private void watch(final ContactEntity contact) {
		final String phone = contact.phone_numbers.get(0);
		showProgressDialog(false);
		UserUtil.editWatch(phone, PARAM_VALUES.LINK_WATCH, new DefaultCallback() {
			
			@Override
			public void succeed() {
				toastShort("关注成功");
				contact.status = ContactEntity.WATCHED;
				listAdapter.notifyDataSetChanged();
				session.getWatcher_list().add(phone);
				dimissProgressDialog();
			}
			
			@Override
			public void fail(String error) {
				toastShort("关注失败\n" + error);
				dimissProgressDialog();
			}
		});
	}

	private static String SMS = "，你好，我正在使用图丁，一起来玩吧，下载地址http://tutuapp.aliapp.com.";
	private void sendSms(ContactEntity contact) {
		String phone = contact.phone_numbers.get(0);
		String name = contact.name;
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));             
        intent.putExtra("sms_body", name + SMS);             
        startActivity(intent);   
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void checkReg() {
//		{"fc":"phonelist_reg_check", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",
//			 "phonelist":"13500000001,13800000001,13900000001,13900000002,18900000003,18979528420"}
		StringBuilder buffer = new StringBuilder();
		for (ContactEntity contact : list)
			buffer.append(contact.phone_numbers.get(0)).append(",");
		JSONObject data = new JSONObject();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_CHECK_PHONE_REG);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.PHONELIST, buffer.subSequence(0, buffer.length() - 1));
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.CHECK_PHONE_REG, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					Log.e("checkReg", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取数据失败:" + e.getMessage();
				}
				if (error != null)
					toastShort(error);
				else
					setList(json);
				dimissProgressDialog();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				toastShort(ServerConstants.NET_ERROR_TIP);
				dimissProgressDialog();
			}
		});
	}
	
	private void setList(JSONObject json) {
		try {
			HashMap<String, Boolean> map = new HashMap<String, Boolean>();
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject jo = array.getJSONObject(i);
				map.put(jo.getString(PARAM_KEYS.PHONE), PARAM_VALUES.RESULT_SUCCEED.equals(jo.getString(PARAM_KEYS.CHECKED)));
			}
			for (ContactEntity contact : list) {
				String phone = contact.phone_numbers.get(0);
				if (map.containsKey(phone) && map.get(phone)) {
					if (session.getWatcher_list().contains(phone))
						contact.status = ContactEntity.WATCHED;
					else
						contact.status = ContactEntity.REGISTERED;
				}
			}
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("checkReg", json.toString());
		}
	}
	
	public List<ContactEntity> getLocalContactsInfos() {
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		ContactEntity contact;
        ContentResolver cr = this.getContentResolver();  
        String str[] = {Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, Phone.HAS_PHONE_NUMBER};  
        Cursor cur = cr.query(Phone.CONTENT_URI, str, null, null, null);  
        if (cur != null) {
            while (cur.moveToNext()) {
            	contact = new ContactEntity();
            	contact.name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            	String phone_number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            	if (Validator.isMobilePhone(phone_number)) {
            		contact.phone_numbers.add(phone_number);
            		result.add(contact);
            	}
//            	获取所有电话号码的方法，速度过慢
//            	long contactid = cur.getLong(cur.getColumnIndex(Phone.CONTACT_ID)); 
//				int phoneCount = cur.getInt(cur.getColumnIndex(Phone.HAS_PHONE_NUMBER));
//                if (phoneCount > 0) {
//                    // 获得联系人的电话号码列表
//                    Cursor phoneCursor = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=" + contactid, null, null);
//                    if (phoneCursor != null) {
//                    	while (phoneCursor.moveToNext())
//                    		contact.phone_numbers.add(phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)));
//                    	phoneCursor.close();
//                    }
//                }
//                long photoid = cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID));  
//                // 如果photoid 大于0 表示联系人有头像 ，如果没有给此人设置头像则给他一个默认的   
//                if (photoid > 0) {  
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);  
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);  
//                    contactsInfo.setBitmap(BitmapFactory.decodeStream(input));  
//                } else {  
//                    contactsInfo.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));  
//                }  
            }  
            cur.close();  
        }
        return result;  
    }  
  
	public List<ContactEntity> getSIMContactsInfos() {
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		ContactEntity contact;
		ContentResolver cr = getContentResolver();
		String SIM_URI_ADN = "content://icc/adn";// SIM卡
		Uri uri = Uri.parse(SIM_URI_ADN);
		Cursor cursor = cr.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			contact = new ContactEntity();
			contact.name = cursor.getString(cursor.getColumnIndex("name"));
			contact.phone_numbers.add(cursor.getString(cursor.getColumnIndex("number")));
//			SIMContactsInfo.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			result.add(contact);
		}
		cursor.close();
		return result;
	}

}
