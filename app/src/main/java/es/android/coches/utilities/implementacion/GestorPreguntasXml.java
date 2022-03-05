package es.android.coches.utilities.implementacion;



import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import es.android.coches.entidad.Pregunta;
import es.android.coches.utilities.interfaz.GestorPreguntas;

public class GestorPreguntasXml implements GestorPreguntas {
    Context context;

    List<Pregunta> todasLasPreguntas;
    List<String> todasLasRespuestas;

    public GestorPreguntasXml(Context context){
        this.context = context;
    }

    @Override
    public List<String> generarRespuestasPosibles(String respuestaCorrecta, int numRespuestas) {
        List<String> respuestasPosibles = new ArrayList<>();
        respuestasPosibles.add(respuestaCorrecta);

        List<String> respuestasIncorrectas = new ArrayList<>(todasLasRespuestas);
        respuestasIncorrectas.remove(respuestaCorrecta);

        for(int i=0; i<numRespuestas-1; i++) {
            int indiceRespuesta = new Random().nextInt(respuestasIncorrectas.size());
            respuestasPosibles.add(respuestasIncorrectas.remove(indiceRespuesta));

        }
        Collections.shuffle(respuestasPosibles);
        return respuestasPosibles;
    }


    private Document leerXML(String recurso) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = factory.newDocumentBuilder();
        Document doc = constructor.parse(context.getAssets().open(recurso));
        doc.getDocumentElement().normalize();
        return doc;
    }

    @Override
    public List<Pregunta> generarPreguntas(String recurso) throws Exception {
        todasLasPreguntas = new LinkedList<>();
        todasLasRespuestas = new LinkedList<>();
        Document doc = leerXML(recurso);
        Element documentElement = doc.getDocumentElement();
        NodeList paises = documentElement.getChildNodes();
        for(int i=0; i<paises.getLength(); i++) {
            if(paises.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element pais = (Element) paises.item(i);
                //String nombre = pais.getAttribute("nombre");
                String nombre = pais.getElementsByTagName("nombre").item(0).getTextContent();
                String foto = pais.getElementsByTagName("foto").item(0).getTextContent();
                todasLasPreguntas.add(new Pregunta(nombre, foto));
                todasLasRespuestas.add(nombre);
            }
        }
        return todasLasPreguntas;
    }
}
