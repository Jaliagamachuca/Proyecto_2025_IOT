package com.example.proyecto_2025.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    public enum Sender { ADMIN, GUIDE }

    private String id;
    private String offerId;
    private Sender sender;
    private String texto;
    private Date fecha;
    private boolean leido;

    public Message() {
        this.id = java.util.UUID.randomUUID().toString();
        this.fecha = new Date();
        this.leido = false;
    }

    public Message(String offerId, Sender sender, String texto) {
        this();
        this.offerId = offerId;
        this.sender = sender;
        this.texto = texto;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}