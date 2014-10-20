/**
 * 
 */
package org.buptdavid.mp3.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.buptdavid.mp3.model.Mp3Info;

import android.os.Environment;


/**
 * @author weijielu
 *
 */
public class FileUtils {
	private String SDCardRoot;

	public String getSDPATH() {
		return SDCardRoot;
	}
	public FileUtils() {
		//得到当前外部存储设备的目录
		// /SDCARD
		SDCardRoot = Environment.getExternalStorageDirectory() + "/";
	}
	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDCardRoot + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDCardRoot + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断SD卡上的文件是否存在
	 */
	public boolean isFileExist(String fileName, String path){
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		//File file = new File(SDCardRoot + fileName);
		System.out.println("file---->" + file);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 读取目录中的Mp3文件的名字和大小
	 */
	public List<Mp3Info> getMp3Files(String path){
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(SDCardRoot + File.separator + path);
		
		File[]  files = file.listFiles();
		for(int i = 0; i < files.length; i++){
			if(files[i].getName().endsWith("mp3")){
				Mp3Info mp3Info = new Mp3Info();
				String name = files[i].getName();
				mp3Info.setMp3File(name);
				mp3Info.setMp3Size(String.valueOf(files[i].length()));
				mp3Info.setLrcName(name.substring(0, name.indexOf(".") + 1) + "lrc");
				
				mp3Infos.add(mp3Info);
			}
		}
		
		return mp3Infos;
	}

}