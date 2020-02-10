package app;

import links.Link;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import statistics.Statistics;

import java.io.IOException;
import java.util.List;

import static app.Operations.*;


public class FileSystemApp {
    private static final Logger LOGGER = LogManager.getLogger();

    private CommandLine line;

    /**
     * Application entry point.
     * @param args application command line arguments
     */
    public static void main(String[] args) {
        FileSystemApp app = new FileSystemApp();
        app.run(args);
    }

    /**
     * Runs the application
     * @param args an array of String arguments to be parsed
     */
    private void run(String[] args) {
        line = parseArguments(args);

        if (line.hasOption("command")) {

            String command = line.getOptionValue("command");
            switch(command) {
                case "e": // "extraction" commmand...
                case "extract":
                    processExtract();
                    break;
                case "s": // "extraction" commmand...
                case "stats":
                    processStats();
                    break;
                default: // unknown commmand...
                    printAppHelp();
            }

        } else { // no command
            printAppHelp();
            LOGGER.info("No parameter provided. Terminating...");
        }
    }

    /**
     * Handler for Extraction arguments, that triggers the
     * link extraction routine
     */
    private void processExtract() {
        if(line.hasOption("input") && line.hasOption("distance")) {
            String input = line.getOptionValue("input");
            String distance = line.getOptionValue("distance");
            String output = ".";

            if(line.hasOption("output"))
                output = line.getOptionValue("output");

            List<Link> data = runExtractor(input, distance);
            store(data, input, output);
        }
    }

    /**
     * Not working: should generate a report of some meaningful stats.
     */
    private void processStats() {
        Statistics statistics = null;

        //GMLExporter exporter = null;
        System.out.println("Export to GML..");
        //exporter = new GMLExporter(city2D);
        //exporter.export();

        //statistics = exporter.getStatistics();
        try {
            System.out.println("Writing statistics (CSV)..");
            statistics.exportToCSV();
            System.out.println("Writing statistics (Latex)..");
            statistics.exportToLatex();
        } catch (IOException e) {
            LOGGER.error("Unable to write stats file.");
            e.printStackTrace();
        }
    }

    /**
     * Routine to store the extracted links in a given file directory.
     * @param data links to be stored
     * @param file file from which the link were extracted
     * @param o_dir directory where the new file should be stored
     */
    private void store(List<Link> data, String file, String o_dir) {
        String output_base = FilenameUtils.getBaseName(file) + "_topo.";
        String output = output_base + FilenameUtils.getExtension(file);

        LOGGER.info("Storing links at: " + output + "...");
        try{
            storeFile(data, o_dir + output);
        } catch (IOException e) {
            LOGGER.error("Unable to store the output file.");
            e.printStackTrace();
        }
    }

    /**
     * Parses application arguments
     * @param args application arguments
     * @return <code>CommandLine</code> which represents a list of application
     * arguments.
     */
    private CommandLine parseArguments(String[] args) {

        Options options = getOptions();
        CommandLine line = null;

        CommandLineParser parser = new DefaultParser();

        try {
            line = parser.parse(options, args);

        } catch (ParseException ex) {

            System.err.println("Failed to parse command line arguments");
            System.err.println(ex.toString());
            printAppHelp();

            System.exit(1);
        }

        return line;
    }

    /**
     * Generates application command line options
     * @return application <code>Options</code>
     */
    private Options getOptions() {

        Options options = new Options();

        options.addOption("c", "command", true, "operation to execute");
        options.addOption("i", "input", true, "CityGML file to load");
        options.addOption("d", "distance", true, "max distance (for extraction routine)");
        options.addOption("o", "output", true, "output file directory");
        return options;
    }

    /**
     * Prints application usage instructions
     */
    private void printAppHelp() {
        Options options = getOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CityGMLTools", options, true);
    }
}
