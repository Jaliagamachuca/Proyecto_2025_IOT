package com.example.proyecto_2025.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Offer implements Serializable {
    public enum Status {PENDIENTE, ACEPTADA, RECHAZADA, VENCIDA}
    public enum PayMode {FIJO, POR_HORA}

    private String id;
    private String guideId;
    private String tourId;
    private Date startDate;
    private Date endDate;
    private PayMode payMode;
    private double amount;
    private Status status;
    private List<Message> mensajes = new ArrayList<>();

    public Offer(String id, String guideId, String tourId, Date startDate, Date endDate,
                 PayMode payMode, double amount, Status status) {
        this.id=id; this.guideId=guideId; this.tourId=tourId; this.startDate=startDate; this.endDate=endDate;
        this.payMode=payMode; this.amount=amount; this.status=status;
        this.mensajes = new ArrayList<>();
    }
    public String getId(){return id;}
    public String getGuideId(){return guideId;}
    public String getTourId(){return tourId;}
    public Date getStartDate(){return startDate;}
    public Date getEndDate(){return endDate;}
    public PayMode getPayMode(){return payMode;}
    public double getAmount(){return amount;}
    public Status getStatus(){return status;}
    public void setStatus(Status s){this.status=s;}

    public List<Message> getMensajes() {
        if (mensajes == null) mensajes = new ArrayList<>();
        return mensajes;
    }
    public void setMensajes(List<Message> mensajes) {
        this.mensajes = mensajes;
    }

    public void addMensaje(Message m) {
        if (mensajes == null) mensajes = new ArrayList<>();
        mensajes.add(m);
    }

    public int getMensajesNoLeidos() {
        int count = 0;
        if (mensajes != null) {
            for (Message m : mensajes) {
                if (!m.isLeido() && m.getSender() != Message.Sender.ADMIN) {
                    count++;
                }
            }
        }
        return count;
    }
}
