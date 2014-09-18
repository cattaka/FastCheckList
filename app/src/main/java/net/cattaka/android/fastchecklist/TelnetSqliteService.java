/*
 * Copyright (c) 2010, Takao Sumitomo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the 
 *       above copyright notice, this list of conditions
 *       and the following disclaimer.
 *     * Redistributions in binary form must reproduce
 *       the above copyright notice, this list of
 *       conditions and the following disclaimer in the
 *       documentation and/or other materials provided
 *       with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software
 * and documentation are those of the authors and should
 * not be interpreted as representing official policies,
 * either expressed or implied.
 */
package net.cattaka.android.fastchecklist;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class TelnetSqliteService extends Service {
	public static final String TAG = "TelnetSqliteTask";
	public static final char PREFIX_SUCCESS = 'S';
	public static final char PREFIX_ERROR = 'E';
	public static final char SEPALATOR = ',';
	public static final char PROMPT = '$';
	public static final char RESULT_HEADER = '+';
	public static final char RESULT_DATA = '-';
	public static final char COMMAND_SPECIAL = '.';
	
	private  TelnetSqliteServer telnetSqliteServer;
	
	public TelnetSqliteService() {
		super();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (this.telnetSqliteServer == null) {
			telnetSqliteServer = new TelnetSqliteServer(this, 8090);
			telnetSqliteServer.start();
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.telnetSqliteServer != null) {
			this.telnetSqliteServer.stopServerSocket();
			this.telnetSqliteServer.interrupt();
			try {
				this.telnetSqliteServer.join();
			} catch (InterruptedException e) {
				// 無視
				Log.d(TAG, e.getMessage(), e);
			}
		}
	}
	
	static class TelnetSqliteServer extends Thread {
		private Context context;
		private int port;
		private ServerSocket serverSocket;
		public TelnetSqliteServer(Context context, int port) {
			super("TelnetSqliteServer");
			this.context = context;
			this.port = port;
		}
	
		@Override
		public void run() {
			Log.d(TAG,"ServerSocket started.");
			try {
				this.serverSocket = new ServerSocket(port);
				while (true) {
					Socket socket = serverSocket.accept();
					
					ChildThread childThread = new ChildThread(context, socket);
					childThread.start();
				}
			} catch (IOException e) {
				Log.d(TAG,e.getMessage(),e);
			}
			Log.d(TAG,"ServerSocket finished.");
		}
		
		public void stopServerSocket() {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// 無視
				}
				try {
					this.join();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	static class ChildThread extends Thread {
		private Socket socket;
		private Context context;
		
		ChildThread(Context context, Socket socket) {
			this.context = context;
			this.socket = socket;
		}
		
		public void run() {
			Log.d(TAG,"ClientTask started.");
			InputStreamReader reader = null;
			OutputStreamWriter writer = null;
			SQLiteDatabase db = null;
			try {
				reader = new InputStreamReader(socket.getInputStream());
				writer = new OutputStreamWriter(socket.getOutputStream());
				
				String databaseFileName = readLine(reader);
				File databaseFile = context.getDatabasePath(databaseFileName); 
				if (!databaseFile.exists()) {
					writeError(writer, "DatabseFile is not exists : " + databaseFile);
				}
				writer.append(PREFIX_SUCCESS);
				writer.append('\n');
				writer.flush();
				
				db = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
				while (db.isOpen()) {
					writePrompt(writer);
					String line = readLine(reader);
					if (line.matches("\\s*")) {
						continue;
					}
					if (line.equals(COMMAND_SPECIAL + "quit")) {
						break;
					}
					
					// SQLの場合
					{
						String sql = line;
						Cursor cursor = null;
						try {
							cursor = db.rawQuery(sql, new String[0]);
							int rowCount = cursor.getCount();
							writer.append(PREFIX_SUCCESS);
							writer.append(String.valueOf(rowCount));
							writer.append('\n');
							writer.flush();
							
							int n = cursor.getColumnCount();
							if (n > 0) {
								writer.append(RESULT_HEADER);
								for (int i=0;i<n;i++) {
									if (i>0) {
										writer.append(SEPALATOR);
									}
									String val = cursor.getColumnName(i);
									writer.append(escapeColumnValue(val));
								}
								writer.append('\n');
								writer.flush();
								while(cursor.moveToNext()) {
									writer.append(RESULT_DATA);
									for (int i=0;i<n;i++) {
										if (i>0) {
											writer.append(SEPALATOR);
										}
										String val = cursor.getString(i);
										writer.append(escapeColumnValue(val));
										//writer.append(cursor.getString(i));
									}
									writer.append('\n');
									writer.flush();
								}
								writer.append('\n');
								writer.flush();
							} else {
								writer.append('\n');
							}
						} catch (SQLException e) {
							writeError(writer, e.getMessage());
						} catch (Exception e) {
							writeError(writer, e.getMessage());
						} finally {
							if (cursor != null) {
								cursor.close();
							}
						}
					}
				}
			} catch (IOException e) {
				Log.w(TAG, e.getMessage(), e);
			} finally {
				if (db != null) {
					db.close();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// 無視
						Log.w(TAG, e.getMessage(), e);
					}
				}
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// 無視
						Log.w(TAG, e.getMessage(), e);
					}
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// 無視
						Log.w(TAG, e.getMessage(), e);
					}
				}
			}
			Log.d(TAG,"ClientTask finished.");
		}
	}
	
	private static String readLine(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		int r;
		boolean escaped = false;
		while((r=reader.read()) != -1) {
			if (r == '\r') {
				// 無視
				continue;
			}
			if (escaped) {
				sb.append((char)r);
				escaped = false;
			} else {
				if (r == '\\') {
					escaped = true;
				} else if (r == '\n') {
					return sb.toString();
				} else {
					sb.append((char)r);
				}
			}
		}
		return sb.toString();
	}
	private static void writePrompt(Writer writer) throws IOException {
		writer.append(PROMPT);
		writer.append('\n');
		writer.flush();
	}
	private static void writeError(Writer writer, String message) throws IOException {
		writer.append(PREFIX_ERROR);
		writer.append(message);
		writer.append('\n');
		writer.flush();
	}
	
	/**
	 * 文字列内の改行、ダブルクオート、カンマをエスケープします。
	 * @param src
	 * @return
	 */
	public static String escapeColumnValue(String src) {
		if (src == null) {
			return "";
		} else if (src.length() == 0) {
			return "\"\"";
		}
		char delim = ',';
		char bracket = '\"';
		String result = src;
		if (src.indexOf(delim) != -1 || src.indexOf('\n') != -1) {
			if (src.indexOf('"') != -1) {
				result = bracket + replaceString(src, String.valueOf(bracket), String.valueOf(bracket)+String.valueOf(bracket)) + bracket;
			} else {
				result = bracket + src + bracket;
			}
		}
		return replaceString(result, "\n","\\\n");
	}
	public static String replaceString(String src, String target, String replace) {
		StringBuilder sb = new StringBuilder(src);
		int p = 0;
		while((p=sb.indexOf(target,p))!=-1) {
			sb.replace(p, p+target.length(), replace);
			p = p + replace.length();
		}
		return sb.toString();
	}
}
