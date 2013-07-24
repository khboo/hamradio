package com.borasoft.radio.log.naqcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

public final class ContestResultGenerator {
  static final String LOG_OUTPUT_DIR = "c:/naqcclogs";
  /**
   * @param args - e.g) -D -T pop3s -H pop3.live.com -U foo@hotmail.com -P Bar
   */
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // Check out the auto logger submission and writes each submission in 
    // its own file with a temporary file name.
    EMailReader emailReader = new EMailReader(args);
    emailReader.dumpMail(LOG_OUTPUT_DIR);
    
    // Read in the submission files, build LogEntry items for score processing.
    // Rename the temporary files using the participants' radio callsigns after completing
    // the processing.
    File dir = new File(LOG_OUTPUT_DIR);
    File[] files = dir.listFiles();
    File file;
    File newFile;
    InputStreamReader streamReader;
    AutoLoggerReader loggerReader;
    LogEntry entry;
    EntryCollector entries = new EntryCollector();
    for(int i=0; i<files.length; i++) {
      file = files[i];
      streamReader = new InputStreamReader(new FileInputStream(file));
      loggerReader = new AutoLoggerReader(streamReader);
      entry = loggerReader.readLogEntry();
      entries.add(entry.getCallArea(),entry);
      streamReader.close();
      newFile = new File(LOG_OUTPUT_DIR + "/" + entry.getCallsign() + ".log");
      file.renameTo(newFile);
    }
    
    // Sort the log entries based on the final score.
    entries.getSortedCollector();
    
    // Generate an HTML file with score results.
    
    System.exit(0);
  }

}
