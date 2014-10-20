/**
 * 
 */
package org.buptdavid.mp3.service;

import org.buptdavid.mp3.AppConstant;
import org.buptdavid.mp3.download.HttpDownloader;
import org.buptdavid.mp3.model.Mp3Info;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Download Service
 * @author weijielu
 *
 */
public class DownloadService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 用户点击ListActivity当中的一个条目时调用此方法
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 取出mp3Info SerializableExtra对象
		Mp3Info mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		
		// 为每一个Mp3Info对象生成一个下载线程
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		Thread thread = new Thread(downloadThread);
		thread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 下载线程类
	 * @author weijielu
	 */
	class DownloadThread implements Runnable{
		private Mp3Info mp3Info;
		
		public DownloadThread(Mp3Info mp3Info){
			this.mp3Info = mp3Info;
		}
		
		@Override
		public void run() {
			// 下载地址
			String mp3Url = AppConstant.URL.BASE_URL + mp3Info.getMp3File();
			HttpDownloader httpDownloader = new HttpDownloader();
			int mp3Result = httpDownloader.downloadFile(mp3Url, "mp3/", mp3Info.getMp3File());
			String lrcUrl = AppConstant.URL.BASE_URL + mp3Info.getLrcName();
			int lrcResult = httpDownloader.downloadFile(lrcUrl, "mp3/", mp3Info.getLrcName());
			
			String resultMessage = null;
			if(mp3Result == -1){
				resultMessage = "Mp3下载失败";
			}else if(mp3Result == 0){
				resultMessage = "Mp3已存在，无需重复下载";
			}else if(mp3Result  == 1){
				resultMessage = "Mp3下载成功";
			}
			
		}
	}
	
}
