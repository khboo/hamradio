package com.borasoft.radio.log.naqcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class AutoLoggerWriter {
	private PrintWriter writer;
	private final String[] callAreas = {
	    "W1","W2","W3","W4","W5","W6","W7","W8","W9","W0","Canada","DX","Gain"
	};

	public AutoLoggerWriter(OutputStreamWriter writer) {
		this.writer = new PrintWriter(writer);
	}
	
	public void writeHTML(Hashtable<String,LogEntry[]> entries) throws IOException{
		generateProlog();
		generateScores(entries);
		generateSoapbox(entries);
		generateEpilog();
	}
	
	public void writeText(Hashtable<String,LogEntry[]> entries) throws IOException {
	  Enumeration<String> enu = entries.keys();
	  String key;
	  LogEntry[] entryArray;
	  LogEntry entry;
	  while(enu.hasMoreElements()) {
	    key = enu.nextElement();
	    writer.println("SWA Category - " + key + " Division");
	    writer.printf("%6s %4s %4s %3s %3s %4s %4s %5s %s\n","Call  ","QSOs","Mbrs","Pts","Mul"," Sco","Bon","Final","80-40-20 Antenna");
	    entryArray = entries.get(key);
	    String bonus = "";
	    for(int i=entryArray.length-1; i>=0; i--) {
	      entry = entryArray[i];
	      // formatting for bonus
	      bonus = entry.getBonusMult();
	      if (bonus.equalsIgnoreCase("1")) {
	        bonus = "";
	      } else {
	        bonus = "x" + bonus;
	      }
	      // callsign(6), qso(4), member qso(4), points(3), multiplier(3), score(4), bonus(3), final(5), antenna
	      writer.printf("%6s %4s %4s %3d %3s %4d %4s %5d %s\n",entry.getCallsign(),entry.getQSOs(),entry.getMemberQSOs(),entry.getPoints(),entry.getMultipliers(),entry.getScore(),bonus,entry.getFinal(),entry.getAntenna());
	    }
	    writer.println();
	  }
	}
	
	private void generateProlog() throws FileNotFoundException, IOException {
	  int c;
	  InputStreamReader reader = new InputStreamReader(new FileInputStream(new File("prolog.tmp")));
	  while((c=reader.read()) != -1) {
	    writer.write(c);
	  }
	  writer.println();
	  writer.flush();   	
	}
	
	private void generateScores(Hashtable<String,LogEntry[]> entries) {
		// <pre>		
		writer.println("<pre>");
		
    String key;
    LogEntry[] entryArray;
    LogEntry entry;

    for (int i=0; i<callAreas.length;i++) {
      key = callAreas[i];
      if(i==callAreas.length-1) { // Gain antenna catefory
        // <span class="red">GAIN Antenna Category</span>
        writer.println("<span class=\"red\">GAIN Antenna Category</span>");
      } else {        
        // <span class="red">SWA Category - W1 Division</span>
        writer.println("<span class=\"red\">SWA Category - " + key + " Division</span>");
      }
      writer.printf("%6s %4s %4s %3s %3s %4s %4s %5s %s\n","Call  ","QSOs","Mbrs","Pts","Mul"," Sco","Bon","Final","80-40-20 Antenna");
      entryArray = entries.get(key);
      if(entryArray==null) {
        writer.println();
        continue;
      }
      String bonus = "";
      for(int j=entryArray.length-1; j>=0; j--) {
        entry = entryArray[j];
        // formatting for bonus
        bonus = entry.getBonusMult();
        if (bonus.equalsIgnoreCase("1")) {
          bonus = "";
        } else {
          bonus = "x" + bonus;
        }
        // callsign(6), qso(4), member qso(4), points(3), multiplier(3), score(4), bonus(3), final(5), antenna
        writer.printf("%6s %4s %4s %3d %3s %4d %4s %5d %s\n",entry.getCallsign(),entry.getQSOs(),entry.getMemberQSOs(),entry.getPoints(),entry.getMultipliers(),entry.getScore(),bonus,entry.getFinal(),entry.getAntenna());
      }
      writer.println();
    }
		
		/*
		<span class="red">AWARD WINNERS:</span>
		1st SWA W1:
		1st SWA W2:
		1st SWA W3:
		1st SWA W4:
		1st SWA W5:
		1st SWA W6:
		1st SWA W7:
		1st SWA W8:
		1st SWA W9:
		1st SWA W0:
		1st SWA Canada:
		1st SWA DX:
		1st Gain:
		*/
		writer.println("<span class=\"red\">AWARD WINNERS:</span>");
		for (int i=1;i<10;i++) {
			writer.println("1st SWA W" + i + ":");
		}
		writer.println("1st SWA W0:");
		writer.println("1st SWA Canada:");
		writer.println("1st SWA DX:");
		writer.println("1st Gain:");
		
		// </pre>	
		writer.println("</pre>");		
	}
	
	public void generateSoapbox(Hashtable<String,LogEntry[]> entries) {
		// <span class="blackboldmedium">SOAPBOX:</span><br>
		writer.println("<span class=\"blackboldmedium\">SOAPBOX:</span><br>");
		
    Enumeration<String> enu = entries.keys();
    String key;
    LogEntry[] entryArray;
    LogEntry entry;
    while(enu.hasMoreElements()) {
      key = enu.nextElement();
      entryArray = entries.get(key);
      for(int i=entryArray.length-1; i>=0; i--) {
        entry = entryArray[i];
        if(entry.getSoapbox().trim().length() != 0) {
          writer.println(entry.getCallsign() + " - " + entry.getSoapbox());
          writer.println("<br/><br/>");
        }
      }
    }
	}
	
	private void generateEpilog() throws IOException {
    int c;
    InputStreamReader reader = new InputStreamReader(new FileInputStream(new File("epilog.tmp")));
    while((c=reader.read()) != -1) {
      writer.write(c);
    }
    writer.println();
    writer.flush(); 		
	}
	
	// test only
	public static void main(String[] args) throws FileNotFoundException, IOException {
		FileInputStream stream = new FileInputStream("autologger_sample.txt");
		InputStreamReader reader = new InputStreamReader(stream);
		AutoLoggerReader logReader = new AutoLoggerReader(reader);
		LogEntry logEntry = logReader.readLogEntry();
		reader.close();	
		
		FileOutputStream ostream = new FileOutputStream("autologger_sample.html");
		OutputStreamWriter writer = new OutputStreamWriter(ostream);
		AutoLoggerWriter loggerWriter = new AutoLoggerWriter(writer);
		//loggerWriter.writeHTML();
		writer.close();
	}

}
