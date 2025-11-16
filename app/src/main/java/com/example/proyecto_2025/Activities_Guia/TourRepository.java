package com.example.proyecto_2025.Activities_Guia;

import android.content.Context;
import java.util.*;

public class TourRepository {
    private static TourRepository instance;
    private final List<Tour> tours = new ArrayList<>();

    private TourRepository() {}

    public static TourRepository get() {
        if (instance == null) instance = new TourRepository();
        return instance;
    }

    public void seedIfEmpty(Context ctx) {
        if (!tours.isEmpty()) return;

        // 🔹 Lista de usuarios demo
        List<String> demoUsuarios = Arrays.asList(
                "Juan Perez",
                "Andres Lujan",
                "Maria Torres",
                "Luis Ccapa"
        );

        // 🔹 Lista de ubicaciones demo (nombre + coordenadas)
        List<Ubicacion> demoUbicaciones = Arrays.asList(
                new Ubicacion("Plaza Mayor de Lima", -12.04318, -77.02824),
                new Ubicacion("Parque Kennedy", -12.12186, -77.02824),
                new Ubicacion("Malecón Cisneros", -12.12850, -77.03000),
                new Ubicacion("Costa Verde Miraflores", -12.14637, -77.04279)
        );

        // ----------------------------------------------------------------------------------------------------
        //                                         TOURS DISPONIBLES
        // ----------------------------------------------------------------------------------------------------

        tours.add(new Tour(
                "Andes Travel",
                "Ruta del Inca",
                "Explora los caminos ancestrales del Cusco.",
                "2025-11-20",
                "07:00",
                "18:00",
                "Carlos Quispe",
                "987654321",
                "https://picsum.photos/seed/tour1/400",
                "disponible",
                "no solicitado",
                350.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Amazonía Viva",
                "Aventura en la Selva",
                "Descubre la biodiversidad de Madre de Dios.",
                "2025-12-05",
                "08:30",
                "17:00",
                "Lucía Ramírez",
                "986321654",
                "https://picsum.photos/seed/tour2/400",
                "disponible",
                "solicitado",
                400.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Machu Picchu Tours",
                "Maravilla del Mundo",
                "Visita guiada al Santuario Histórico de Machu Picchu.",
                "2025-11-25",
                "05:00",
                "16:00",
                "Miguel Torres",
                "985412377",
                "https://picsum.photos/seed/tour3/400",
                "disponible",
                "no solicitado",
                500.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Cusco Místico",
                "Montaña de 7 Colores",
                "Trekking por los Andes hasta Vinicunca.",
                "2025-11-30",
                "04:30",
                "15:00",
                "Ana Poma",
                "987321444",
                "https://picsum.photos/seed/tour4/400",
                "disponible",
                "solicitado",
                380.0,
                demoUsuarios,
                demoUbicaciones
        ));

        // ----------------------------------------------------------------------------------------------------
        //                                         TOURS PENDIENTES
        // ----------------------------------------------------------------------------------------------------

        tours.add(new Tour(
                "Apu Adventures",
                "Laguna Humantay",
                "Caminata a la laguna turquesa de Soraypampa.",
                "2025-11-03",
                "05:00",
                "14:30",
                "José Huamán",
                "987654777",
                "https://picsum.photos/seed/tour5/400",
                "pendiente",
                "no iniciado",
                320.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Selva y Cultura",
                "Misterios del Manu",
                "Recorre el parque nacional más biodiverso del Perú.",
                "2025-11-04",
                "06:30",
                "18:30",
                "María Quispe",
                "986544789",
                "https://picsum.photos/seed/tour6/400",
                "pendiente",
                "iniciado",
                450.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Inka Trek",
                "Camino Sagrado",
                "Trekking de 4 días por el Camino Inca.",
                "2025-11-10",
                "06:00",
                "17:00",
                "David Huanca",
                "981234567",
                "https://picsum.photos/seed/tour7/400",
                "pendiente",
                "iniciado",
                480.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Pacha Travel",
                "Aventura en Paracas",
                "Tour en bote por las Islas Ballestas y Reserva Nacional.",
                "2025-11-12",
                "08:00",
                "14:00",
                "Rosa Ñusta",
                "975654987",
                "https://picsum.photos/seed/tour8/400",
                "pendiente",
                "no iniciado",
                370.0,
                demoUsuarios,
                demoUbicaciones
        ));

        // ----------------------------------------------------------------------------------------------------
        //                                         TOURS FINALIZADOS
        // ----------------------------------------------------------------------------------------------------

        tours.add(new Tour(
                "Cusco Heritage",
                "City Tour Cusco",
                "Recorrido histórico por templos y calles coloniales.",
                "2025-10-10",
                "09:00",
                "13:00",
                "Carlos Torres",
                "987654321",
                "https://picsum.photos/seed/tour9/400",
                "finalizado",
                "",
                300.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Andean Spirit",
                "Valle Sagrado de los Incas",
                "Visita Pisac, Ollantaytambo y Chinchero.",
                "2025-10-15",
                "07:30",
                "17:30",
                "Lucía Ramírez",
                "986321654",
                "https://picsum.photos/seed/tour10/400",
                "finalizado",
                "",
                420.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Tierra Andina",
                "Mirador de Taray",
                "Hermosas vistas del Valle Sagrado.",
                "2025-10-20",
                "08:00",
                "12:00",
                "Miguel Quispe",
                "985412377",
                "https://picsum.photos/seed/tour11/400",
                "finalizado",
                "",
                280.0,
                demoUsuarios,
                demoUbicaciones
        ));

        tours.add(new Tour(
                "Explora Perú",
                "Sacsayhuamán y Qenqo",
                "Recorrido por sitios arqueológicos del Cusco.",
                "2025-10-22",
                "09:00",
                "14:00",
                "Ana Poma",
                "987321444",
                "https://picsum.photos/seed/tour12/400",
                "finalizado",
                "",
                310.0,
                demoUsuarios,
                demoUbicaciones
        ));
    }

    // Obtener TODOS los tours
    public List<Tour> all() {
        return new ArrayList<>(tours);
    }

    // Obtener tours por estado: disponible, pendiente, finalizado
    public List<Tour> byEstado(String estado) {
        List<Tour> result = new ArrayList<>();
        for (Tour t : tours) {
            if (t.getEstadoGeneral().equalsIgnoreCase(estado)) {
                result.add(t);
            }
        }
        return result;
    }

    // Obtener tour por nombre
    public Tour byNombre(String nombreTour) {
        for (Tour t : tours) {
            if (t.getNombreTour().equalsIgnoreCase(nombreTour)) {
                return t;
            }
        }
        return null;
    }
}
