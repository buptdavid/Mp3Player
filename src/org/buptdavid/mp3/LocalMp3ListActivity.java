/**
 * 
 */
package org.buptdavid.mp3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.buptdavid.mp3.model.Mp3Info;
import org.buptdavid.mp3.utils.FileUtils;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 本地 Mp3 List Activity
 * @author weijielu
 * @see PlayerActivity
 * @see Mp3Info
 */
public class LocalMp3ListActivity extends ListActivity {
	List<Mp3Info> mp3Infos = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		
	}

	@Override
	protected void onResume() {
		FileUtils fileUtils = new FileUtils();
		mp3Infos = fileUtils.getMp3Files(AppConstant.LOCAL_MP3_FORDER + File.separator);
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		for(Iterator<Mp3Info> it = mp3Infos.iterator(); it.hasNext();){
			Mp3Info mp3Info = it.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mp3_name", mp3Info.getMp3Name());
			map.put("mp3_file", mp3Info.getMp3File());
			map.put("mp3_size", mp3Info.getMp3Size());
			map.put("lrc_name", mp3Info.getLrcName());
			map.put("lrc_size", mp3Info.getLrcSize());
			list.add(map);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(LocalMp3ListActivity.this, list, R.layout.mp3info_item, new String[]{"mp3_file", "mp3_size"}, new int[]{R.id.mp3_file, R.id.mp3_size});
		LocalMp3ListActivity.this.setListAdapter(simpleAdapter);
		
		super.onResume();
	}

	/**
	 * 点击某行进入播放页面
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(mp3Infos != null){
			Mp3Info mp3Info = mp3Infos.get(position);
			Intent intent = new Intent();
			intent.putExtra("mp3Info", mp3Info);
			intent.setClass(this, PlayerActivity.class);
			this.startActivity(intent);
		}
		
		super.onListItemClick(l, v, position, id);
	}

}
