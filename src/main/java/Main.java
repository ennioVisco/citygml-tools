import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import extractor.Extractor;
import extractor.GeometricObject;
import extractor.GeometricStrategy;
import extractor.NearEnoughStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;

import links.*;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Basic settings strings, they choose the file to load
     * and the file to generate
     */
    static final private String P = System.getProperty("user.dir");
    static final String IN_FILE_PATH = P + "/data/in.gml";
    static final String OUT_FILE_PATH = P + "/data/out.gml";

    public static void main(String[] args) throws Exception {
        CityModel cityGML;
        List<? extends GeometricObject> links;

        //GMLExporter exporter = null;
        Statistics statistics = null;


        LOGGER.info("Loading CityGML model at: "
                          + IN_FILE_PATH + "...");
        cityGML = loadCityModel(IN_FILE_PATH);

        LOGGER.info("Processing Link Extraction routine...");
        links = extractLinks(cityGML);

        LOGGER.info("Storing links at: " + OUT_FILE_PATH + "...");
        storeFile(links.toString());

        //System.out.println("Export to GML..");
        //exporter = new GMLExporter(city2D);
        //exporter.export();

        //statistics = exporter.getStatistics();
        //System.out.println("Writing statistics (CSV)..");
        //statistics.exportToCSV();
        //System.out.println("Writing statistics (Latex)..");
        //statistics.exportToLatex();
    }

    /**
     * IO Action: model loader from .gml file
     *
     * @param file the source file
     * @return a CityGML CityModel object that represents the whole model
     * @throws Exception if the file is not found or CityGML parsing fails
     */
    public static CityModel loadCityModel(String file) throws Exception {
        CityGMLContext ctx = CityGMLContext.getInstance();
        CityGMLBuilder builder = ctx.createCityGMLBuilder();

        CityGMLInputFactory in = builder.createCityGMLInputFactory();
        CityGMLReader reader = in.createCityGMLReader(new File(file));

        CityModel cityModel = (CityModel) reader.nextFeature();
        reader.close();

        return cityModel;
    }

    /**
     * Extraction wrapper for the extractor.Extractor module
     *
     * @param model the CityModel from citygml4j
     * @return a list of links
     */
    public static List<? extends GeometricObject> extractLinks(CityModel model)  {
        Extractor extractor = new Extractor(model);
        GeometricStrategy strategy = new NearEnoughStrategy();
        return extractor.extract(strategy);
    }

    /**
     * IO Action: stores the output in the destination file.
     *
     * @param text the content to store
     * @throws IOException thrown if the file is not found.
     */
    public static void storeFile(String text) throws IOException {
        // Outputting
        File file = new File(OUT_FILE_PATH);
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
    }
}
