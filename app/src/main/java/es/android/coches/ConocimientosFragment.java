package es.android.coches;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import es.android.coches.databinding.FragmentConocimientosBinding;
import es.android.coches.entidad.Pregunta;
import es.android.coches.utilities.implementacion.GestorPreguntasXml;
import es.android.coches.utilities.interfaz.GestorPreguntas;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConocimientosFragment extends Fragment {

    private FragmentConocimientosBinding binding;

    List<Pregunta> preguntas;
    int respuestaCorrecta;
    int pMaxima;
    int ultimaP;
    String cadena="";
    GestorPreguntas servicio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        servicio = new GestorPreguntasXml(getContext());
        try {
            preguntas = new ArrayList<>(servicio.generarPreguntas("coches.xml"));
            Collections.shuffle(preguntas);

            if(!fileExists(getContext(), "puntuacion.xml")){


                try {
                    salvarFichero("puntuacion.xml","<><puntuacion><maxima>"+ pMaxima +"</maxima><ultima>"+ ultimaP +"</ultima></puntuacion></>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                leerPuntuacionXml();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConocimientosBinding.inflate(inflater, container, false);

        try {
            presentarPregunta();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.botonRespuesta.setOnClickListener(v -> {
            int seleccionado = binding.radioGroup.getCheckedRadioButtonId();
            CharSequence mensaje = "";

            if(seleccionado==respuestaCorrecta){

                mensaje = "¡Acertaste!";
                ultimaP++;



            }else{
                mensaje = "Fallaste";
            }

            Snackbar.make(v, mensaje, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Siguiente", v1 -> {
                        try {
                            presentarPregunta();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    })
                    .show();
            v.setEnabled(false);
        });

        return binding.getRoot();
    }

    private void presentarPregunta() throws IOException, JSONException {
        if (preguntas.size() > 0) {
            binding.botonRespuesta.setEnabled(true);

            int pregunta = new Random().nextInt(preguntas.size());

            Pregunta preguntaActual = preguntas.remove(pregunta);
            preguntaActual.setRespuetas(servicio.generarRespuestasPosibles(preguntaActual.getRespuestaCorrecta(), binding.radioGroup.getChildCount()));

            InputStream coche = null;
            try {
                coche = getContext().getAssets().open(preguntaActual.getFoto());
                binding.coche.setImageBitmap(BitmapFactory.decodeStream(coche));
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.radioGroup.clearCheck();
            for (int i = 0; i < binding.radioGroup.getChildCount(); i++) {
                RadioButton radio = (RadioButton) binding.radioGroup.getChildAt(i);
                CharSequence respuesta = preguntaActual.getRespuetas().get(i);
                if (respuesta.equals(preguntaActual.getRespuestaCorrecta()))
                    respuestaCorrecta = radio.getId();

                radio.setText(respuesta);
            }
        } else {
            binding.coche.setVisibility(View.GONE);
            binding.radioGroup.setVisibility(View.GONE);
            binding.botonRespuesta.setVisibility(View.GONE);

            if(pMaxima < ultimaP) {
                pMaxima = ultimaP;
                cadena = "¡Has batido tu récord de aciertos! Has alcanzado " + pMaxima +" puntos";
            }
            salvarFichero("puntuacion.xml","<><puntuacion><maxima>"+ pMaxima +"</maxima><ultima>"+ ultimaP +"</ultima></puntuacion></>");
            if(cadena.equals("")) cadena = "Has conseguido \n"+ultimaP+" puntos";

            System.out.println(ultimaP);

            binding.textView.setText("¡Fin!\n"+cadena);
        }
    }

    private void salvarFichero(String fichero, String texto) {
        FileOutputStream fos;
        try {
            fos = getContext().openFileOutput(fichero, Context.MODE_PRIVATE);
            fos.write(texto.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    private void leerPuntuacionXml() throws Exception {
        Document doc = leerXMLfichero("puntuacion.xml");
        Element documentElement = doc.getDocumentElement();
        NodeList puntuaciones = documentElement.getChildNodes();
        for(int i=0; i<puntuaciones.getLength(); i++) {
            if(puntuaciones.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element puntuacion = (Element) puntuaciones.item(i);
                String puntuacionMaxima = puntuacion.getElementsByTagName("maxima").item(0).getTextContent();
                pMaxima = Integer.parseInt(puntuacionMaxima);
            }
        }
    }

    private Document leerXMLfichero(String fichero) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = factory.newDocumentBuilder();
        InputStream is = getContext().openFileInput(fichero);
        Document doc = constructor.parse(is);
        doc.getDocumentElement().normalize();
        return doc;
    }
}