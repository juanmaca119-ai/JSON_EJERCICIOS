package EJERCICIO_GSON;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AdivinaPersonaje {
    private static final String RUTA_FICHERO =
            "C:\\Users\\JuanManuelCabreraL\\IdeaProjects\\FICHEROSJSON\\src\\main\\java\\EJERCICIO_GSON\\";

    private static final String NOMBRE_FICHERO = "personajes.json";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        Random random = new Random();
        String jugarDeNuevo = "s";

        try {
            //lee el fichero json usando un lector de archivos y gson
            BufferedReader reader = new BufferedReader(new FileReader(RUTA_FICHERO + NOMBRE_FICHERO));
            Type tipoLista = new TypeToken<List<Personaje>>(){}.getType();
            List<Personaje> listaPersonajes = gson.fromJson(reader, tipoLista);
            reader.close();

            //bucle principal para permitir volver a jugar
            while (jugarDeNuevo.equalsIgnoreCase("s")) {
                //selecciona un personaje al azar de la lista
                Personaje personajeSeleccionado = listaPersonajes.get(random.nextInt(listaPersonajes.size()));
                List<String> pistas = personajeSeleccionado.getPistas();

                boolean acertado = false;
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
                        break;
                    } else {
                        System.out.println("Fallaste");
                    }
                }

                //si agota las pistas sin acertar
                if (!acertado) {
                    System.out.println("Error: El personaje correcto era: " + personajeSeleccionado.getNombre());
                }

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
}