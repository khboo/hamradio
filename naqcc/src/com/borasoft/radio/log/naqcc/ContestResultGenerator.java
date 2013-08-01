package com.borasoft.radio.log.naqcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.borasoft.radio.utils.Logger;

public final class ContestResultGenerator {
  private String outputDir;
  private Logger logger;
  private final String submissionOrderFilename = "submissions.lst";
  
  /**
   * @param args - e.g) -D -T pop3s -H pop3.live.com -U foo@hotmail.com -P Bar -O c:/temp2 -S "NAQCC Sprint Log"
   */
  public static void main(String[] args) throws FileNotFoundException, IOException {
    Logger logger= Logger.getInstance();
    logger.message("\nNAQCC Sprint Result Page Generator\n");
    logger.message("Program started.");
    ContestResultGenerator gen = new ContestResultGenerator();
    gen.readEMailAndArchive(args);
    gen.generateHTMLFromArchive(args); // read the value of -O, archive directory from args
    logger.message("Completed.");
    System.exit(0);
  }
  
  public ContestResultGenerator() {
    logger=Logger.getInstance();
  }
  
  public void readEMailAndArchive(String[] args) throws FileNotFoundException, IOException {
    // Check out the auto logger submission and writes each submission in 
    // its own file with a temporary file name.
    logger.message("Reading email and writing to temporary files...");
    EMailReader emailReader = new EMailReader(args);
    emailReader.dumpMail();
    if(emailReader.getNumEMailProcessed()==0) {
      return;
    }
    
    // Read in the submission files, build LogEntry items for score processing.
    // Rename the temporary files using the participants' radio callsigns after completing
    // the processing.
    outputDir = emailReader.getOutputDir();
    if(outputDir==null) { // the user did not provide log files output directory
      outputDir=System.getProperty("user.dir")+"/archives";
    }
    File dir = new File(outputDir);
    File[] files = dir.listFiles();
    File file;
    File newFile;
    InputStreamReader streamReader;
    AutoLoggerReader loggerReader;
    LogEntry entry;
    boolean returnCode=true;
    logger.message("Renaming temporary files to <callsign>.log...");
    Vector<String> callsigns = new Vector<String>();
    for(int i=0; i<files.length; i++) {
      file = files[i];
      streamReader = new InputStreamReader(new FileInputStream(file));
      loggerReader = new AutoLoggerReader(streamReader);
      entry = loggerReader.readLogEntry();
      streamReader.close();
      newFile = new File(outputDir + "/" + entry.getCallsign().trim() + ".log");
      if(newFile.exists()) {
        logger.warning(newFile + " already exists.");
        logger.warning("Did not rename " +file.getName()+" to "+newFile);
      } else {
        returnCode=file.renameTo(newFile);
        if (!returnCode) {
          logger.error("File rename failed for: " + file.getName());
        } else {
          callsigns.add(entry.getCallsign());
        }
      }
    }
    updateSubmissionOrderList(callsigns);
  }
  
  public void generateHTMLFromArchive(String[] args)  throws FileNotFoundException, IOException {
    for (int optind = 0; optind < args.length; optind++) {
      if (args[optind].equals("-O")) {
        outputDir = args[++optind];
      }
    }    
    String outputFilename = "sprint_result.html";
    logger.info("HTML file to be generated: " + outputFilename);
    // Read in the submission files from outputDir, build LogEntry items for score processing.
    File dir = new File(outputDir);
    File[] files = dir.listFiles();
    File file;
    InputStreamReader streamReader;
    AutoLoggerReader loggerReader;
    LogEntry entry;
    EntryCollector entries = new EntryCollector();
    logger.info("Number of files to be processed: " + files.length);
    logger.message("Processing started.");
    for(int i=0; i<files.length; i++) {
      file = files[i];
      if(!hasExtension(file.getName(),".log")) { // not a sprint file
        logger.warning("Skipping a file: " + file.getName());
        continue;
      }
      streamReader = new InputStreamReader(new FileInputStream(file));
      loggerReader = new AutoLoggerReader(streamReader);
      entry = loggerReader.readLogEntry();
      // Handle SWA and GAIN caterories.
      if(entry.getCategory().equalsIgnoreCase("SWA")) {
        entries.add(entry.getCallArea(),entry);
      } else if(entry.getCategory().equalsIgnoreCase("Gain")) {
        entries.add("GAIN",entry);
      } else {
        // TBD - log an exception - unknown category
        entries.add(entry.getCallArea(),entry);
      }
      streamReader.close();
    }
    logger.message("Log file processing finished.");
    
    // Sort the log entries based on the final score.
    entries.sort();
    Hashtable<String,LogEntry[]> result = entries.getSortedCollector();
    
    logger.message("Generating the results...");
    // Generate an HTML file with score results.
    FileOutputStream ostream = new FileOutputStream(outputFilename);
    OutputStreamWriter writer = new OutputStreamWriter(ostream);
    AutoLoggerWriter loggerWriter = new AutoLoggerWriter(writer);
//    loggerWriter.writeText(result);
//    loggerWriter.generateSoapbox(result);
    Vector<String> submissionOrder = getSubmissionOrder();
    loggerWriter.writeHTML(result,submissionOrder);
    logger.message("The results are in: " + outputFilename);
    writer.close();
  }
  
  private Vector<String> getSubmissionOrder() throws IOException {
    File file=new File(outputDir+"/"+submissionOrderFilename);
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    Vector<String> v= new Vector<String>();
    while ((line = reader.readLine()) != null) {
      v.add(line);
    }
    reader.close();
    return v;
  }
 
  private boolean hasExtension(String filename, String extension) {
    return filename.endsWith(extension);
  }
  
  // Create/Update a file with callsigns in the submitted order.
  // This is needed for soapbox generation.
  private void updateSubmissionOrderList(Vector<String> callsigns) throws IOException {
    File file = new File(outputDir+"/"+submissionOrderFilename);
    FileWriter writer=null;
    if(file.exists()) {
      writer = new FileWriter(file,true); // update the file with new submissions
    } else {
      writer = new FileWriter(file,false); // create a new file
    }
    String callsign=null;
    Enumeration<String> e=callsigns.elements();
    while(e.hasMoreElements()) {
      callsign=e.nextElement();
      writer.write(callsign+"\n");
    }
    writer.close();
  }

}
