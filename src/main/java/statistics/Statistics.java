package statistics;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Statistics {

	private List<StatisticsEntry> entries;

	private File csvFile;

	public Statistics(File csvFile) throws IOException {
		this.csvFile = csvFile;

		entries = new ArrayList<StatisticsEntry>();

		if (csvFile.exists()) {
			CSVReader reader = new CSVReader(new FileReader(csvFile), ';', '\'');
			String[] nextLine;
			int lineCounter = 0;
			while ((nextLine = reader.readNext()) != null) {
				if (lineCounter > 0) {
					entries.add(new StatisticsEntry(nextLine));
				}
				lineCounter++;
			}
			reader.close();
		}
	}

	public void append(StatisticsEntry entry) {
		entries.add(entry);
	}

	public void exportToCSV() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ';', '\'');

		List<String[]> data = new ArrayList<String[]>();
		data.add(StatisticsEntry.HEADERS);
		for (StatisticsEntry entry : entries) {
			data.add(entry.toStringArray());
		}

		writer.writeAll(data);
		writer.close();
	}

	public void exportToLatex() throws IOException {
		String res = "\\begin{tabular}{|l|l|l|l|l|l|}\n";
		res += "\\hline\n";
		// \multicolumn{3}{ |c| }{Team sheet} \\
		for (int i = 0; i < StatisticsEntry.HEADERS.length; i++) {
			res += StatisticsEntry.HEADERS[i] + " ";
			if (i < StatisticsEntry.HEADERS.length - 1) {
				res += " & ";
			}
		}
		res += "\\\\ \\hline\n";
		res += "\\hline\n";

		for (StatisticsEntry entry : entries) {
			String[] data = entry.toStringArray();
			for (int i = 0; i < data.length; i++) {
				res += data[i] + " ";
				if (i < data.length - 1) {
					res += " & ";
				}
			}
			res += "\\\\ \\hline\n";
		}

		res += "\\end{tabular}";

		File latexFile = new File("statistics/experiments.tex");
		FileWriter writer = new FileWriter(latexFile);
		writer.write(res);
		writer.close();
	}
}
