package com.borasoft.radio.log.naqcc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AutoLoggerWriter {
	private PrintWriter writer;
	private LogEntry logEntry;

	public AutoLoggerWriter(OutputStreamWriter writer, LogEntry logEntry) {
		this.writer = new PrintWriter(writer);
		this.logEntry = logEntry;
	}
	
	public void writeHTML() {
		generateProlog();
		generateScores();
		generateSoapbox();
		generateEpilog();
	}
	
	private void generateProlog() {
		
	}
	
	private void generateScores() {
		// <pre>		
		writer.println("<pre>");

		writer.println("<span class=\"red\">SWA Category - W3 Division</span>");
		writer.println("Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna");
		writer.println
			(logEntry.getCallsign() + "   "
			 + logEntry.getQSOs() + "   "
			 + logEntry.getMemberQSOs() + "   "
			 + logEntry.getPoints() + "   "
			 + logEntry.getMultipliers() + "   "
			 + logEntry.getScore() + " x"
			 + logEntry.getBonusMult() + "   "
			 + logEntry.getFinal() + "   "
			 + logEntry.getAntenna());
		/*
		<span class="red">SWA Category - W1 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W2 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W3 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W4 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W5 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W6 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W7 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W8 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W9 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - W0 Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - Canada Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">SWA Category - DX Division</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		<span class="red">GAIN ANTENNA Category</span>
		Call   QSOs Mbrs Pts  Mul  Sco Bon Final  160 Antenna
		*/
		
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
		writer.println("1st SWA Gain:");
		
		// </pre>	
		writer.println("</pre>");		
	}
	
	private void generateSoapbox() {
		// <span class="blackboldmedium">SOAPBOX:</span><br>
		writer.println("<span class=\"blackboldmedium\">SOAPBOX:</span><br>");
		String callsign = logEntry.getCallsign();
		String soapbox = logEntry.getSoapbox();
		writer.println(callsign + " - " + soapbox + "<br/><br/>");
	}
	
	private void generateEpilog() {
		
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
		AutoLoggerWriter loggerWriter = new AutoLoggerWriter(writer,logEntry);
		loggerWriter.writeHTML();
		writer.close();
	}

}
