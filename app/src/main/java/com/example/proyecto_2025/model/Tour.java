package com.example.proyecto_2025.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tour implements Serializable {
    public String id = UUID.randomUUID().toString();

    public Tour() {}

    public String empresaId;                  // opcional si manejas multi-empresa
    public String titulo;
    public String descripcionCorta;
    public double precioPorPersona;
    public int cupos;
    public List<String> idiomas = new ArrayList<>();
    public List<String> servicios = new ArrayList<>();   // "desayuno", "movilidad", etc.
    public List<String> imagenUris = new ArrayList<>();
    public long fechaInicioUtc;               // millis
    public long fechaFinUtc;                  // millis
    public List<PuntoRuta> ruta = new ArrayList<>();
    public String guiaId;                     // puede ser null hasta que acepte
    public double propuestaPagoGuia;          // fijo o %
    public boolean pagoEsPorcentaje;          // true => propuestaPagoGuia es %
    public TourEstado estado = TourEstado.BORRADOR;

    private Boolean incluyeDesayuno = false;
    private Boolean incluyeAlmuerzo = false;
    private Boolean incluyeCena = false;

    public String solicitudEstado;

    public long solicitudAceptadaUtc;

    public boolean esPublicable() {
        return titulo != null && !titulo.isEmpty()
                && descripcionCorta != null && !descripcionCorta.isEmpty()
                && precioPorPersona > 0 && cupos > 0
                && imagenUris.size() >= 2
                && ruta.size() >= 2
                && fechaInicioUtc > 0 && fechaFinUtc > fechaInicioUtc
                && guiaId != null && !guiaId.isEmpty()
                && estado == TourEstado.PENDIENTE_GUIA; // pasa a PUBLICADO cuando el guía acepta
    }

    public String precioTexto() {
        return (precioPorPersona <= 0) ? "—" : "S/ " + String.format("%.2f", precioPorPersona);
    }

    public boolean isIncluyeDesayuno() { return Boolean.TRUE.equals(incluyeDesayuno); }
    public void setIncluyeDesayuno(boolean incluyeDesayuno) { this.incluyeDesayuno = incluyeDesayuno; }

    public boolean isIncluyeAlmuerzo() { return Boolean.TRUE.equals(incluyeAlmuerzo); }
    public void setIncluyeAlmuerzo(boolean incluyeAlmuerzo) { this.incluyeAlmuerzo = incluyeAlmuerzo; }

    public boolean isIncluyeCena() { return Boolean.TRUE.equals(incluyeCena); }
    public void setIncluyeCena(boolean incluyeCena) { this.incluyeCena = incluyeCena; }


}
