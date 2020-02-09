package app;

import extractor.Extractor;
import extractor.geometric.GeometricObject;
import extractor.geometric.GeometricStrategy;
import extractor.geometric.NearEnoughStrategy;
import links.Link;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.builder.jaxb.CityGMLBuilderException;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Operations {

    /**
     * IO Action: model loader from .gml file
     *
     * @param file the source file
     * @return a CityGML CityModel object that represents the whole model
     * @throws Exception if the file is not found or CityGML parsing fails
     */
    public static CityModel loadCityModel(String file) throws CityGMLBuilderException, CityGMLReadException {
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
    public static List<? extends GeometricObject> extractLinks(CityModel model, int distance)  {
        Extractor extractor = new Extractor(model);
        GeometricStrategy strategy = new NearEnoughStrategy(distance);
        return extractor.extract(strategy);
    }

    /**
     * IO Action: stores the links in the destination file.
     *
     * @param links the links to store
     * @throws IOException thrown if the file is not found.
     */
    public static void storeFile(List<Link> links, String file) throws IOException {
        FileWriter writer = new FileWriter(file);
        for(Link link: links) {
            writer.write(link.toString() + System.lineSeparator());
        }
        writer.close();
    }
}
