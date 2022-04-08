package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;
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

    private GpxGenerator(){

    }

    public static Document createGpx(Route route, ElevationProfile profile){

        Document doc = newDocument(); // voir plus bas

        Element root = doc.createElementNS("http://www.topografix.com/GPX/1/1", "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");



        //Iterate on each edge of the route, and adds the frompoint, and the elevation based on the length of the edges
        int position = 0;
        for(Edge edge : route.edges()){ //todo for i ou foreach ?
            //Edge edge = route.edges().get(i);
            //todo iterer avec la length de l'edge pour touver la position
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat",  String.format(Locale.ROOT, "%5f", edge.fromPoint().lat()));
            rtept.setAttribute("lon", String.format(Locale.ROOT, "%5f",edge.fromPoint().lon()));

            Element ele = doc.createElement("ele");
            ele.setTextContent(String.format(Locale.ROOT, "%2f",profile.elevationAt(position)));
            rtept.appendChild(ele);

            position += edge.length();
            rte.appendChild(rtept);


        }

        //Last point case
        Element rtept = doc.createElement("rtept");
        rtept.setAttribute("lat", String.format(Locale.ROOT, "%5f", route.edges().get(route.edges().size() -1).toPoint().lat()));
        rtept.setAttribute("lon", String.format(Locale.ROOT, "%5f",route.edges().get(route.edges().size() -1).toPoint().lon()));

        Element ele = doc.createElement("ele");
        ele.setTextContent(String.format(Locale.ROOT, "%2f",profile.elevationAt(position)));
        rtept.appendChild(ele);

        rte.appendChild(rtept);

        return doc;
    }

    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws IOException{
        //todo try catch ?
        try{
            Document doc = createGpx(route, profile);
            Writer w = new PrintWriter(fileName);
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        }catch(TransformerException e){
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
