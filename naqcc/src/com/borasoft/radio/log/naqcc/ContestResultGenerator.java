package com.borasoft.radio.log.naqcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class ContestResultGenerator {
  static String outputDir;
  /**
   * @param args - e.g) -D -T pop3s -H pop3.live.com -U foo@hotmail.com -P Bar -O c:/temp2 -S "NAQCC Sprint Log"
   */
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // Check out the auto logger submission and writes each submission in 
    // its own file with a temporary file name.
    EMailReader emailReader = new EMailReader(args);
    emailReader.dumpMail();
    
    // Read in the submission files, build LogEntry items for score processing.
    // Rename the temporary files using the participants' radio callsigns after completing
    // the processing.
    outputDir = emailReader.getOutputDir();
    File dir = new File(outputDir);
    File[] files = dir.listFiles();
    File file;
    File newFile;
    InputStreamReader streamReader;
    AutoLoggerReader loggerReader;
    LogEntry entry;
    EntryCollector entries = new EntryCollector();
    boolean returnCode=true;
    for(int i=0; i<files.length; i++) {
      file = files[i];
      streamReader = new InputStreamReader(new FileInputStream(file));
      loggerReader = new AutoLoggerReader(streamReader);
      entry = loggerReader.readLogEntry();
      entries.add(entry.getCallArea(),entry);
      streamReader.close();
      newFile = new File(outputDir + "/" + entry.getCallsign() + ".log");
      returnCode=file.renameTo(newFile);
      if (!returnCode) {
        System.out.println("File rename failed: " + file.getName());
      }
    }
    
    // Sort the log entries based on the final score.
    entries.sort();
    Hashtable<String,LogEntry[]> result = entries.getSortedCollector();
//    Enumeration<String> enu = result.keys();
//    while(enu.hasMoreElements()) {
//      System.out.println(enu.nextElement());
//    }
    
    // Generate an HTML file with score results.
    FileOutputStream ostream = new FileOutputStream(outputDir + "/sprint_result.txt");
    OutputStreamWriter writer = new OutputStreamWriter(ostream);
    AutoLoggerWriter loggerWriter = new AutoLoggerWriter(writer);
    loggerWriter.writeText(result);
    writer.close();
    
    System.out.println("Exiting...");
    System.exit(0);
  }

}
