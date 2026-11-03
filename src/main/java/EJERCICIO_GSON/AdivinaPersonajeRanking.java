package EJERCICIO_GSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AdivinaPersonajeRanking {
    private static final String RUTA_FICHERO =
            "C:\\Users\\JuanManuelCabreraL\\IdeaProjects\\FICHEROSJSON\\src\\main\\java\\EJERCICIO_GSON\\";

    private static final String NOMBRE_FICHERO = "personajes.json";
    private static final String FICHERO_RANKING = "ranking.json";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Random random = new Random();
        String jugarDeNuevo = "s";

        try {
            //lee el fichero json usando un lector de archivos y gson
            BufferedReader reader = new BufferedReader(new FileReader(RUTA_FICHERO + NOMBRE_FICHERO));
            Type tipoLista = new TypeToken<List<Personaje>>(){}.getType();
            List<Personaje> listaPersonajes = gson.fromJson(reader, tipoLista);
            reader.close();

            //carga el ranking actual si ya existe el fichero
            List<Jugador> listaRanking = cargarRanking();

            System.out.print("Introduce tu nombre: ");
            String nombreJugador = sc.nextLine().trim();

            //bucle principal para permitir volver a jugar
            while (jugarDeNuevo.equalsIgnoreCase("s")) {
                //selecciona un personaje al azar de la lista
                Personaje personajeSeleccionado = listaPersonajes.get(random.nextInt(listaPersonajes.size()));
                List<String> pistas = personajeSeleccionado.getPistas();

                boolean acertado = false;
                int puntosPartida = 0;
                System.out.println("\nNUEVA PARTIDA DE ADIVINA EL PERSONAJE");

                //recorre las pistas una a una
                for (int i = 0; i < pistas.size(); i++) {
                    System.out.println("Pista " + (i + 1) + ": " + pistas.get(i));
                    System.out.print("Quién soy?: ");
                    String respuestaUsuario = sc.nextLine().trim();

                    //comprueba si la respuesta coincide con el nombre del personaje
                    if (respuestaUsuario.equalsIgnoreCase(personajeSeleccionado.getNombre())) {
                        System.out.println("Has acertado el personaje: " + personajeSeleccionado.getNombre());
                        acertado = true;
                        //calcula los puntos según las pistas usadas (más puntos cuantas menos pistas)
                        puntosPartida = (pistas.size() - i) * 10;
                        break;
                    } else {
                        System.out.println("Fallaste");
                    }
                }

                //si agota las pistas sin acertar
                if (!acertado) {
                    System.out.println("Error: El personaje correcto era: " + personajeSeleccionado.getNombre());
                } else {
                    System.out.println("Has conseguido " + puntosPartida + " puntos en esta ronda.");
                    //actualiza el ranking con la puntuación del jugador
                    actualizarRanking(listaRanking, nombreJugador, puntosPartida);
                    guardarRanking(gson, listaRanking);
                }

                //muestra el ranking actual por pantalla
                mostrarRanking(listaRanking);

                //pregunta si desea volver a jugar
                System.out.print("\nDeseas volver a jugar? (s/n): ");
                jugarDeNuevo = sc.nextLine().trim();
            }

            System.out.println("Gracias por jugar");

        } catch (Exception e) {
            System.out.println("Error al procesar el fichero JSON: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    //método auxiliar para cargar el ranking desde el fichero json
    private static List<Jugador> cargarRanking() {
        List<Jugador> ranking = new ArrayList<>();
        File fichero = new File(RUTA_FICHERO + FICHERO_RANKING);
        if (fichero.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fichero));
                Gson gson = new Gson();
                Type tipoListaRanking = new TypeToken<List<Jugador>>(){}.getType();
                List<Jugador> listaLeida = gson.fromJson(reader, tipoListaRanking);
                if (listaLeida != null) {
                    ranking = listaLeida;
                }
                reader.close();
            } catch (Exception e) {
                //si hay error al leer, se devuelve la lista vacía
            }
        }
        return ranking;
    }

    //método auxiliar para actualizar o sumar puntos al jugador en el ranking
    private static void actualizarRanking(List<Jugador> ranking, String nombre, int puntosNuevos) {
        boolean encontrado = false;
        for (Jugador j : ranking) {
            if (j.getNombre().equalsIgnoreCase(nombre)) {
                j.sumarPuntos(puntosNuevos);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            ranking.add(new Jugador(nombre, puntosNuevos));
        }
        //ordena el ranking de mayor a menor puntuación
        Collections.sort(ranking, (j1, j2) -> Integer.compare(j2.getPuntos(), j1.getPuntos()));
    }

    //método auxiliar para guardar el ranking actualizado en el fichero json
    private static void guardarRanking(Gson gson, List<Jugador> ranking) {
        try {
            FileWriter writer = new FileWriter(RUTA_FICHERO + FICHERO_RANKING);
            gson.toJson(ranking, writer);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error al guardar el ranking en el fichero.");
        }
    }

    //método auxiliar para mostrar el ranking en consola
    private static void mostrarRanking(List<Jugador> ranking) {
        System.out.println("\nCLASIFICACIÓN");
        for (int i = 0; i < ranking.size(); i++) {
            Jugador j = ranking.get(i);
            System.out.println((i + 1) + ". " + j.getNombre() + " - " + j.getPuntos() + " puntos");
        }
    }

    //clase auxiliar interna para mapear el json de personajes
    private static class Personaje {
        private String nombre;
        private List<String> pistas;

        public String getNombre() {
            return nombre;
        }

        public List<String> getPistas() {
            return pistas;
        }
    }

    //clase auxiliar para representar al jugador y su puntuación
    private static class Jugador {
        private String nombre;
        private int puntos;

        public Jugador(String nombre, int puntos) {
            this.nombre = nombre;
            this.puntos = puntos;
        }

        public String getNombre() {
            return nombre;
        }

        public int getPuntos() {
            return puntos;
        }

        public void sumarPuntos(int puntosNuevos) {
            this.puntos += puntosNuevos;
        }
    }
}