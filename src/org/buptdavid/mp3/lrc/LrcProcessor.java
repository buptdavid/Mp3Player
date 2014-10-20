/**
 * 
 */
package org.buptdavid.mp3.lrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weijielu
 *
 */
public class LrcProcessor {
	
	public ArrayList<Queue> process(InputStream inputStream){
		Queue<Long> timeMills = new LinkedList<Long>();
		Queue<String> messages = new LinkedList<String>();
		
		ArrayList<Queue> queues = new ArrayList<Queue>();
		
		InputStreamReader inputReader = null;
		try {
			inputReader = new InputStreamReader(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		String temp = null;
		
		try {
			while((temp = bufferedReader.readLine()) != null){
				if(!temp.equals("")){
					String timeStr = temp.substring(1, temp.indexOf("]"));
					Long timeMill = time2Long(timeStr);
					timeMills.offer(timeMill);
					
					String message = temp.substring(temp.indexOf("]") + 1, temp.length());
					messages.offer(message);
				}
			}
			
			queues.add(timeMills);
			queues.add(messages);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return queues;
	}
	
	/**
	 * 处理歌词内容并放到ArrayList<Queue>中
	 * @param str
	 * @return
	 */
	public ArrayList<Queue> processStr(String str){
		ArrayList<Queue> queues = new ArrayList<Queue>();
		
		if(str != null){
			Queue<Long> timeMills = new LinkedList<Long>();
			Queue<String> messages = new LinkedList<String>();
			
			String[] strArray = str.split("\r\n");
			for(int i = 0; i < strArray.length; i++){
				String timeStr = strArray[i].substring(1, strArray[i].indexOf("]") - 1);
				Long timeMill = time2Long(timeStr);
				timeMills.offer(timeMill);
				
				String message = strArray[i].substring(strArray[i].indexOf("]"), strArray[i].length());
				messages.offer(message);
			}
			
			queues.add(timeMills);
			queues.add(messages);
		}
		
		return queues;
	}

	/**
	 * convert to mill sec
	 * @param timeStr
	 * @return
	 */
	private Long time2Long(String timeStr){
		String s[] = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String ss[] = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		
		return min * 60 * 1000 + sec * 1000 + mill * 10L;
	}
}
