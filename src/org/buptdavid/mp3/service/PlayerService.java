/**
 * 
 */
package org.buptdavid.mp3.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import org.buptdavid.mp3.AppConstant;
import org.buptdavid.mp3.lrc.LrcProcessor;
import org.buptdavid.mp3.model.Mp3Info;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

/**
 * Player Service
 * @author weijielu
 * @see LrcProcessor
 */
public class PlayerService extends Service {
	
	MediaPlayer mediaPlayer = null;
	
	private boolean isPlaying = false;
//	private boolean isPause = false;
	private boolean isReleased = false;
	
	private Handler handler = new Handler();
	private UpdateTimeCallback updateTimeCallback = null;
	private long begin = 0;
	private long nextTimeMill = 0;
	private long currentTimeMill = 0;
	private String message = null;
	private long pauseTimeMills = 0;
	private ArrayList<Queue> queues = null;
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Mp3Info mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		int MSG = intent.getIntExtra("MSG", 0);
		
		if(mp3Info != null){
			if(MSG == AppConstant.PlayerMsg.PLAY_MSG){
				play(mp3Info);
			}else if(MSG == AppConstant.PlayerMsg.PAUSE_MSG){
				pause(mp3Info);
			}else if(MSG == AppConstant.PlayerMsg.STOP_MSG){
				stop(mp3Info);
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * play mp3Info
	 * @param mp3Info
	 */
	private void play(Mp3Info mp3Info){
		if(!isPlaying){
			String path = getMp3Path(mp3Info);
			mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
			prepareLrc(mp3Info.getLrcName());
			handler.postDelayed(updateTimeCallback,5);
			begin=System.currentTimeMillis();
			isPlaying = true;
			isReleased = false;
        }
	}
	
	/**
	 * Pause mp3Info
	 * @param mp3Info
	 */
	private void pause(Mp3Info mp3Info){
		if(isPlaying){
			mediaPlayer.pause();
			handler.removeCallbacks(updateTimeCallback);
			pauseTimeMills = System.currentTimeMillis();
		}else {
			mediaPlayer.start();
			handler.postDelayed(updateTimeCallback,5);
			begin=System.currentTimeMillis() - pauseTimeMills + begin;
		}
		isPlaying = isPlaying?false:true;
	}
	
	/**
	 * Stop mp3Info
	 * @param mp3Info
	 */
	private void stop(Mp3Info mp3Info){
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (!isReleased) {
					handler.removeCallbacks(updateTimeCallback);
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased = true;
				}
				isPlaying = false;
			}
		}
	}
	
	
	/**
	 * get the path of mp3Info
	 * @param mp3Info
	 * @return
	 */
	private String getMp3Path(Mp3Info mp3Info){
		String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = SDCardRoot + File.separator + AppConstant.LOCAL_MP3_FORDER + File.separator + mp3Info.getMp3File();
		return path;
	}
	
	/**
	 * 根据歌词文件的名字读取歌词文件当中的信息
	 * @param lrcname
	 */
	private void prepareLrc(String lrcName){
		try {
			InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + AppConstant.LOCAL_MP3_FORDER + "/" + lrcName);
			LrcProcessor lrcProcessor = new LrcProcessor();
			queues = lrcProcessor.process(inputStream);
			updateTimeCallback = new UpdateTimeCallback(queues);
			begin = 0 ;
			currentTimeMill = 0 ;
			nextTimeMill = 0 ;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update Time callback
	 * @author weijielu
	 *
	 */
	class UpdateTimeCallback implements Runnable{
		Queue times = null;
		Queue messages = null;

		public UpdateTimeCallback(ArrayList<Queue> queues) {
			times = queues.get(0);
			messages = queues.get(1);
		}
		
		@Override
		public void run() {
			long offset = System.currentTimeMillis() - begin;
			if(currentTimeMill == 0){
				nextTimeMill = (Long)times.poll();
				message = (String)messages.poll();
			}
			if(offset >= nextTimeMill){
				Intent intent=new Intent();
				intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
				intent.putExtra("lrcMessage",message);
				
				sendBroadcast(intent);
				Object timeObject = times.poll();
				if(timeObject != null){
					nextTimeMill = (Long)timeObject;
				}
				
				Object messageObject = messages.poll();
				if(messageObject != null){
					message = (String)messageObject;
				}
				
			}
			currentTimeMill = currentTimeMill + 10;
			handler.postDelayed(updateTimeCallback, 10);
		}
	}
}
