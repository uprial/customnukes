package com.gmail.uprial.customnukes.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomStorage {
	protected static Character valueDelimiter = '=';
	
	protected final File dataFolder;
	protected final String fileName;
	protected final CustomLogger customLogger;

	protected Map<String,String> data;

	public CustomStorage(File dataFolder, String fileName, CustomLogger customLogger) {
		this.dataFolder = dataFolder;
		this.fileName = fileName;
		this.customLogger = customLogger;
		clear();
	}
	
	public void set(String key, String value) {
		data.put(key, value);
	}
	
	public void delete(String key) {
		data.remove(key);
	}
	
	public String get(String key) {
		return data.get(key);
	}
	
	public Set<Map.Entry<String,String>> entrySet() {
		return data.entrySet();
	}
	
    public void save() {
    	if(!dataFolder.exists())
    		 dataFolder.mkdir();

		try {
			saveData();
		} catch (IOException e) {
			customLogger.error(e.toString());
		}
    }

    public void load() {
    	File file = new File(getFileName());
    	if(file.exists()) {
			try {
				loadData();
			} catch (IOException e) {
				customLogger.error(e.toString());
			}
    	}
    }
    
    public void clear() {
		data = new HashMap<String, String>();
    }
    
    private void saveData() throws IOException {
		FileWriter fileWriter = new FileWriter(getFileName());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		String[] row = new String[2];
		for (Map.Entry<String,String> entry : data.entrySet()) {
			row[0] = entry.getKey().toString();
			row[1] = entry.getValue().toString();
			bufferedWriter.write(EUtils.join(row, valueDelimiter));
			bufferedWriter.newLine();
		}
			
		bufferedWriter.close();
	}
	
	private void loadData() throws IOException {
		FileReader fileReader = new FileReader(getFileName());
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		while((line = bufferedReader.readLine()) != null) {
			String[] row = EUtils.split(line, valueDelimiter);
			data.put(row[0], row[1]);
		}
			
		bufferedReader.close();	
	}

    private String getFileName() {
		File file = new File(dataFolder, fileName);
		return file.getPath().toString();
	} 	

}