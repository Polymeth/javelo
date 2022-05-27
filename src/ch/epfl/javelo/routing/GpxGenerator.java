package ch.epfl.javelo.routing;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

public final class GpxGenerator {

    private GpxGenerator() {
    }

    /**
     * Create the xml data that needs to be written in a gpx document
     *
     * @param route   the route computed by the algorithm for the path
     * @param profile the corresponding elevation profile of the route
     * @return Document containing path (needs to be written in a gpx document)
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument();

        Element root = doc.createElementNS("http://www.topografix.com/GPX/1/1", "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        //create elements needed for document
        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);
        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");
        Element rte = doc.createElement("rte");

        //Iterate on each edge of the route, and adds the frompoint, and the elevation based on the length of the edges
        int position = 0;
        for (Edge edge : route.edges()) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", String.format(Locale.ROOT, "%5f", Math.toDegrees(edge.fromPoint().lat())));
            rtept.setAttribute("lon", String.format(Locale.ROOT, "%5f", Math.toDegrees(edge.fromPoint().lon())));

            Element ele = doc.createElement("ele");
            ele.setTextContent(String.format(Locale.ROOT, "%2f", profile.elevationAt(position)));
            rtept.appendChild(ele);

            position += edge.length();
            rte.appendChild(rtept);
        }

        //Last point case
        Element rtept = doc.createElement("rtept");
        rtept.setAttribute("lat", String.format(Locale.ROOT, "%5f", Math.toDegrees(route.edges().get(route.edges().size() - 1).toPoint().lat())));
        rtept.setAttribute("lon", String.format(Locale.ROOT, "%5f", Math.toDegrees(route.edges().get(route.edges().size() - 1).toPoint().lon())));

        Element ele = doc.createElement("ele");
        ele.setTextContent(String.format(Locale.ROOT, "%2f", profile.elevationAt(position)));
        rtept.appendChild(ele);

        //append the created route to document
        rte.appendChild(rtept);
        root.appendChild(rte);

        return doc;
    }

    /**
     * Write the path and elevation in a gpx file
     *
     * @param fileName Name of the file written in project folder (needs to end in .gpx)
     * @param route    the route computed by the algorithm for the path
     * @param profile  the corresponding elevation profile of the route
     * @throws IOException
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws IOException {

        try {
            Document doc = createGpx(route, profile);
            Writer w = new PrintWriter(fileName);
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
