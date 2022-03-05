package es.android.coches.entidad;

import java.util.List;

public class Pregunta {
    private String nombre;
    private String foto;
    private String respuestaCorrecta;
    private List<String> respuetas;

    public Pregunta(String nombre, String foto) {
        this.nombre = nombre;
        this.foto = foto;
        this.respuestaCorrecta = nombre;
    }

    public List<String> getRespuetas() {
        return respuetas;
    }

    public void setRespuetas(List<String> respuetas) {
        this.respuetas = respuetas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
}
