package app;

import extractor.geometric.GeometricObject;
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
import org.citygml4j.builder.jaxb.CityGMLBuilderException;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.reader.CityGMLReadException;

import java.io.IOException;
import java.util.List;

import static app.Operations.*;


public class FileSystemApp {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Application entry point.
     *
     * @param args application command line arguments
     */
    public static void main(String[] args) {
        FileSystemApp app = new FileSystemApp();
        app.run(args);
    }

    /**
     * Runs the application
     *
     * @param args an array of String arguments to be parsed
     */
    private void run(String[] args) {

        CommandLine line = parseArguments(args);

        if (line.hasOption("command")) {

            //System.out.println(line.getOptionValue("role"));
            String command = line.getOptionValue("command");

            if( command.equals("e")) //if "Extraction" commmand...
                if(line.hasOption("input") && line.hasOption("distance"))
                    runExtractor(line.getOptionValue("input"), line.getOptionValue("distance"));
            else
                printAppHelp();

        } else { //Default: Peer...
            printAppHelp();
            LOGGER.info("No parameter provided. Terminating...");
        }
    }

    private void runExtractor(String input, String distance) {
        CityModel cityGML;
        List<? extends GeometricObject> links;

        //GMLExporter exporter = null;
        //Statistics statistics = null;

        LOGGER.info("Loading CityGML model at: " + input + "...");
        try {
            cityGML = loadCityModel(input);
            LOGGER.info("Processing Link Extraction routine...");
            links = extractLinks(cityGML, Integer.parseInt(distance));
            store((List<Link>)links, input);

        } catch(CityGMLReadException e) {
            LOGGER.error("Unable to read the CityGML file.");
            e.printStackTrace();
        } catch (CityGMLBuilderException e) {
            LOGGER.error("Undefined error when loading CityGML file.");
            e.printStackTrace();
        }

        //System.out.println(links.toString());

        //System.out.println("Export to GML..");
        //exporter = new GMLExporter(city2D);
        //exporter.export();

        //statistics = exporter.getStatistics();
        //System.out.println("Writing statistics (CSV)..");
        //statistics.exportToCSV();
        //System.out.println("Writing statistics (Latex)..");
        //statistics.exportToLatex();
    }

    private void store(List<Link> data, String file) {
        String output_base = FilenameUtils.getBaseName(file) + "_topo.";
        String output = output_base + FilenameUtils.getExtension(file);

        LOGGER.info("Storing links at: " + output + "...");
        try{
            storeFile(data, output);
        } catch (IOException e) {
            LOGGER.error("Unable to store the output file.");
            e.printStackTrace();
        }
    }

    /**
     * Parses application arguments
     *
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
     *
     * @return application <code>Options</code>
     */
    private Options getOptions() {

        Options options = new Options();

        options.addOption("c", "command", true, "operation to execute");
        options.addOption("i", "input", true, "CityGML file to load");
        options.addOption("d", "distance", true, "max distance (for extraction routine)");
        return options;
    }

    /**
     * Prints application help
     */
    private void printAppHelp() {
        Options options = getOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CityGMLTools", options, true);
    }
}
