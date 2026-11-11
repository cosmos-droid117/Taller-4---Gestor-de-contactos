import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    private static final Scanner sc = new Scanner(System.in);
    private static final PersonaDAO dao = new PersonaDAO();

    private static final String CANCELAR = "cancelar";

    // Se lanza cuando el usuario escribe "cancelar" en cualquier pregunta
    private static class CancelarException extends RuntimeException {}

    public static void main(String[] args) {
        int opcion = -1;
        do {
            System.out.println("\n===== AGENDA DE CONTACTOS =====");
            System.out.println("1. Crear contacto");
            System.out.println("2. Consultar contacto");
            System.out.println("3. Actualizar contacto");
            System.out.println("4. Eliminar contacto");
            System.out.println("5. Listar todos");
            System.out.println("0. Salir");
            System.out.println("(En cualquier momento escribe '" + CANCELAR + "' para volver al menú)");

            try {
                opcion = leerEntero("Opción: ", 0);
                switch (opcion) {
                    case 1: crear(); break;
                    case 2: mostrar(buscarPorCriterio()); break;
                    case 3: actualizar(); break;
                    case 4: eliminar(); break;
                    case 5: mostrar(dao.listarTodos()); break;
                    case 0: System.out.println("Hasta luego."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (CancelarException e) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
            }
        } while (opcion != 0);
    }

    // ---------- Opciones del menú ----------

    private static void crear() {
        int id = leerEntero("ID: ", 1);
        String nombre = leerNombre("Nombre: ", false);
        String telefono = leerTelefono("Teléfono: ", false);

        if (dao.crear(new Persona(id, nombre, telefono))) {
            System.out.println("Contacto creado.");
        }
    }

    private static void actualizar() {
        Persona p = seleccionarContacto();
        if (p == null) return;

        String nombre = leerNombre("Nuevo nombre (Enter para dejar '" + p.getNombre() + "'): ", true);
        String telefono = leerTelefono("Nuevo teléfono (Enter para dejar '" + p.getTelefono() + "'): ", true);

        if (!nombre.isEmpty()) p.setNombre(nombre);
        if (!telefono.isEmpty()) p.setTelefono(telefono);

        System.out.println(dao.actualizar(p) ? "Contacto actualizado." : "No se pudo actualizar.");
    }

    private static void eliminar() {
        Persona p = seleccionarContacto();
        if (p == null) return;

        String r;
        do {
            r = leerLinea("¿Eliminar a " + p.getNombre() + "? (s/n): ");
        } while (!r.equalsIgnoreCase("s") && !r.equalsIgnoreCase("n"));

        if (r.equalsIgnoreCase("s")) {
            System.out.println(dao.eliminar(p.getId()) ? "Contacto eliminado." : "No se pudo eliminar.");
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }

    // ---------- Búsqueda ----------

    private static List<Persona> buscarPorCriterio() {
        System.out.println("Buscar por: 1. ID  2. Nombre  3. Teléfono");
        int op;
        do {
            op = leerEntero("Opción: ", 1);
            if (op > 3) System.out.println("Elige 1, 2 o 3.");
        } while (op > 3);

        switch (op) {
            case 1:
                return dao.buscar("id", String.valueOf(leerEntero("ID a buscar: ", 1)));
            case 2:
                return dao.buscar("nombre", leerNombre("Nombre a buscar: ", false));
            default:
                return dao.buscar("telefono", leerTelefono("Teléfono a buscar: ", false));
        }
    }

    // Para actualizar/eliminar: si hay varias coincidencias, se elige por ID
    private static Persona seleccionarContacto() {
        List<Persona> resultado = buscarPorCriterio();
        if (resultado.isEmpty()) {
            System.out.println("No se encontraron contactos.");
            return null;
        }
        if (resultado.size() == 1) {
            System.out.println(resultado.get(0));
            return resultado.get(0);
        }
        mostrar(resultado);
        while (true) {
            int id = leerEntero("Hay varias coincidencias, escribe el ID del contacto: ", 1);
            for (Persona p : resultado) {
                if (p.getId() == id) return p;
            }
            System.out.println("Ese ID no está en los resultados.");
        }
    }

    private static void mostrar(List<Persona> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay contactos para mostrar.");
            return;
        }
        for (Persona p : lista) System.out.println(p);
    }

    // ---------- Lectura y validación de datos ----------

    // Lee una línea; si el usuario escribe "cancelar" lanza la excepción
    private static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        String texto = sc.nextLine().trim();
        if (texto.equalsIgnoreCase(CANCELAR)) {
            throw new CancelarException();
        }
        return texto;
    }

    // Entero mayor o igual a 'minimo'. Repite hasta que sea válido.
    private static int leerEntero(String mensaje, int minimo) {
        while (true) {
            String texto = leerLinea(mensaje);
            try {
                int n = Integer.parseInt(texto);
                if (n >= minimo) return n;
                System.out.println("El número debe ser " + minimo + " o mayor.");
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido (no puede estar vacío).");
            }
        }
    }

    // Solo letras (con tildes y ñ) y espacios entre palabras.
    // Si permitirVacio es true, Enter devuelve "" (sirve para conservar el valor al actualizar).
    private static String leerNombre(String mensaje, boolean permitirVacio) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.isEmpty() && permitirVacio) return "";
            if (texto.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            } else if (!texto.matches("\\p{L}+( \\p{L}+)*")) {
                System.out.println("El nombre solo puede contener letras (sin números ni símbolos).");
            } else {
                return texto;
            }
        }
    }

    // Solo dígitos, entre 7 y 15.
    private static String leerTelefono(String mensaje, boolean permitirVacio) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.isEmpty() && permitirVacio) return "";
            if (texto.isEmpty()) {
                System.out.println("El teléfono no puede estar vacío.");
            } else if (!texto.matches("\\d{7,15}")) {
                System.out.println("El teléfono solo puede contener números (entre 7 y 15 dígitos).");
            } else {
                return texto;
            }
        }
    }
}
