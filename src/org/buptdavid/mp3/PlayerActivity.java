/**
 * 
 */
package org.buptdavid.mp3;

import org.buptdavid.mp3.model.Mp3Info;
import org.buptdavid.mp3.service.PlayerService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Mp3 Player Activity
 * @author weijielu
 * @see AppConstant
 * @see PlayerService
 */
public class PlayerActivity extends Activity {
	
	ImageButton startButton = null;
	ImageButton pauseButton = null;
	ImageButton stopButton = null;
	TextView lrcTextView = null;
	
	Mp3Info mp3Info = null;
	
	Handler handler;
	
	private IntentFilter intentFilter=null;
	private BroadcastReceiver receiver=null;
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		receiver = new LrcMessageBroadcastReceiver();
		registerReceiver(receiver,getIntentFilter());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		Intent intent = getIntent();
		mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		
		startButton = (ImageButton)findViewById(R.id.start);
		startButton.setOnClickListener(new StartOnClickListener());
		
		pauseButton = (ImageButton)findViewById(R.id.pause);
		pauseButton.setOnClickListener(new PauseOnClickListener());
		
		stopButton = (ImageButton)findViewById(R.id.stop);
		stopButton.setOnClickListener(new StopOnClickListener());
		
		lrcTextView = (TextView)findViewById(R.id.lrcText);
	}
	
	/**
	 * Start listener
	 * @author weijielu
	 *
	 */
	class StartOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// 设置动画效果
			startButton.startAnimation(getScaleAnimationSet());
			
			startService(AppConstant.PlayerMsg.PLAY_MSG);
		}
		
	}
	
	/**
	 * Pause listener
	 * @author weijielu
	 *
	 */
	class PauseOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// 设置动画效果
			pauseButton.startAnimation(getScaleAnimationSet());
			
			startService(AppConstant.PlayerMsg.PAUSE_MSG);
		}
		
	}
	
	/**
	 * Stop listener
	 * @author weijielu
	 *
	 */
	class StopOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// 设置动画效果
			stopButton.startAnimation(getScaleAnimationSet());
			
			startService(AppConstant.PlayerMsg.STOP_MSG);
		}
		
	}
	
	/**
	 * Start service base on MSG
	 * @param MSG
	 */
	private void startService(int MSG){
		Intent intent = new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.putExtra("MSG", MSG);
		intent.setClass(PlayerActivity.this, PlayerService.class);
		PlayerActivity.this.startService(intent);
	}
	
	/**
	 * INtent Filter
	 * @return
	 */
	private IntentFilter getIntentFilter(){
    	if(intentFilter == null){
    		intentFilter = new IntentFilter();
    		intentFilter.addAction(AppConstant.LRC_MESSAGE_ACTION);
    	}
    	return intentFilter;
    }
	
	/**
	 * Lrc Message broadcase receiver
	 * @author weijielu
	 *
	 */
	class LrcMessageBroadcastReceiver extends BroadcastReceiver{
    	
    	@Override
		public void onReceive(Context context, Intent intent) {
    		String lrcMessage=intent.getStringExtra("lrcMessage");
    		lrcTextView.setText(lrcMessage);
		}
    }

	/**
	 * 设置Scale动画效果
	 * @return AnimationSet对象
	 */
	private AnimationSet getScaleAnimationSet(){
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.1f, 1, 1.1f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillBefore(true);
		animationSet.setDuration(150);
		return animationSet;
	}
}
