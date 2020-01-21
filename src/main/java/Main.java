import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import extractor.Extractor;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.builder.jaxb.CityGMLBuilderException;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import links.*;

public class Main {

    /**
     * Basic settings strings, they choose the file to load
     * and the file to generate
     */
    static final private String P = System.getProperty("user.dir");
    static final String IN_FILE_PATH = P + "/data/in.gml";
    static final String OUT_FILE_PATH = P + "/data/out.gml";

    public static void main(String[] args) throws Exception {
        CityModel cityGML;
        List<Link> links;

        //GMLExporter exporter = null;
        Statistics statistics = null;


        System.out.println("Loading CityGML model at: "
                          + IN_FILE_PATH + "...");
        cityGML = loadCityModel(IN_FILE_PATH);

        links = extractLinks(cityGML);
        System.out.println("Storing links at: " + OUT_FILE_PATH + "...");
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
    public static List<Link> extractLinks(CityModel model)  {
        Extractor extractor = new Extractor(model);
        return extractor.extract();
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
