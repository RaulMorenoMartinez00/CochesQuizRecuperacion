package es.android.coches.utilities.interfaz;

import java.util.List;

import es.android.coches.entidad.Pregunta;

public interface GestorPreguntas {
    List<String> generarRespuestasPosibles(String respuestaCorrecta, int numRespuestas);

    List<Pregunta> generarPreguntas(String recurso) throws Exception;
}
