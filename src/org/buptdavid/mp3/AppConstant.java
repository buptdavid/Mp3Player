/**
 * 
 */
package org.buptdavid.mp3;

/**
 * @author weijielu
 *
 */
public interface AppConstant {

	public class PlayerMsg{
		public static final int PLAY_MSG = 1;
		public static final int PAUSE_MSG = 2;
		public static final int STOP_MSG = 3;
	}
	
	public class URL{
		public static final String BASE_URL = "http://9.123.154.208:8080/mp3/";
	}
	
	public static final String LRC_MESSAGE_ACTION = "org.buptdavid.mp3.lrcmessage.action";
	
	public static final String LOCAL_MP3_FORDER = "mp3";
}
