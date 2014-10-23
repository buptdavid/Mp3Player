package org.buptdavid.mp3;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.buptdavid.mp3.download.HttpDownloader;
import org.buptdavid.mp3.model.Mp3Info;
import org.buptdavid.mp3.service.DownloadService;
import org.buptdavid.mp3.xml.Mp3ListContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 服务器上Mp3 List Activity
 * @author weijielu
 * @see DownloadService
 */
public class RemoteMp3ListActivity extends ListActivity {
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;
	private Handler handler;
	List<Mp3Info> mp3Infos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_mp3_list);
		handler = new NetworkHandler();
		
		// 启动下载线程，更新服务器上的Mp3 List
		Thread networkThread = new NetworkThread();
		networkThread.start();
	}

	/**
	 * 在用户点击MENU按钮后调用该方法，我们在该方法中添加自己的按钮控件
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, UPDATE, 1, R.string.mp3list_update);
		menu.add(0, ABOUT, 2, R.string.mp3list_about);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 点击按钮控件后的Action
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == UPDATE){
			Thread networkThread = new NetworkThread();
			networkThread.start();
		}else if(item.getItemId() == ABOUT){
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 点击某个Mp3后的Action:下载该Mp3和lrc歌词
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// 根据用户点击Mp3列表的位置来获得Mp3Info对象
		Mp3Info mp3Info = mp3Infos.get(position);
		
		Intent intent = new Intent();
		// 将Mp3Info对象存入到intent对象中
		intent.putExtra("mp3Info", mp3Info);
		intent.setClass(this, DownloadService.class);
		
		// 启动Service
		this.startService(intent);
		
		super.onListItemClick(l, v, position, id);
	}
	
	/**
	 * Network handler
	 * @author weijielu
	 */
	@SuppressLint("HandlerLeak")
	class NetworkHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			//下载并解析XML
			mp3Infos = parse((String)msg.obj);
			
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			for(Iterator it = mp3Infos.iterator(); it.hasNext();){
				Mp3Info mp3Info = (Mp3Info) it.next();
				HashMap<String ,String> map = new HashMap<String, String>();
				map.put("mp3_file", mp3Info.getMp3File());
				map.put("mp3_size", mp3Info.getMp3Size());
				map.put("mp3_name", mp3Info.getMp3Name());
				list.add(map);
			}
			
			// 结果显示到mp3info_item.xml中
			SimpleAdapter simpleAdapter = new SimpleAdapter(RemoteMp3ListActivity.this, list, R.layout.mp3info_item, new String[]{"mp3_file", "mp3_size"}, new int[]{R.id.mp3_file, R.id.mp3_size});
			RemoteMp3ListActivity.this.setListAdapter(simpleAdapter);
		}
	}
	
	/**
	 * NetWrok Thread
	 * @author weijielu
	 */
	class NetworkThread extends Thread{
		@Override
		public void run(){
			Message msg = handler.obtainMessage();
			msg.obj = HttpDownloader.download("http://9.123.154.208:8080/mp3/resources.xml");
			handler.sendMessage(msg);
		}
	}
	
	
	/**
	 * parse XML String
	 * @param xmlStr
	 * @return
	 */
	private List<Mp3Info> parse(String xmlStr){
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> infos = new ArrayList<Mp3Info>();
		
		try{
			XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
			xmlReader.setContentHandler(mp3ListContentHandler);
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return infos;
	}
}
