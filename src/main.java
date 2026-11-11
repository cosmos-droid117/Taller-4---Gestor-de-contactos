import java.util.List;
import java.util.Scanner;

public class main {
    private static final Scanner sc = new Scanner(System.in);
    private static final PersonaDAO dao = new PersonaDAO();

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("\n===== AGENDA DE CONTACTOS =====");
            System.out.println("1. Crear contacto");
            System.out.println("2. Consultar contacto");
            System.out.println("3. Actualizar contacto");
            System.out.println("4. Eliminar contacto");
            System.out.println("5. Listar todos");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1: crear(); break;
                case 2: consultar(); break;
                case 3: actualizar(); break;
                case 4: eliminar(); break;
                case 5: mostrar(dao.listarTodos()); break;
                case 0: System.out.println("Hasta luego."); break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private static void crear() {
        int id = leerEntero("ID: ");
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine().trim();
        if (nombre.isEmpty() || telefono.isEmpty()) {
            System.out.println("Nombre y teléfono no pueden estar vacíos.");
            return;
        }
        if (dao.crear(new Persona(id, nombre, telefono))) {
            System.out.println("Contacto creado.");
        }
    }

    private static void consultar() {
        List<Persona> resultado = buscarPorCriterio();
        mostrar(resultado);
    }

    private static void actualizar() {
        Persona p = seleccionarContacto();
        if (p == null) return;

        System.out.print("Nuevo nombre (Enter para dejar '" + p.getNombre() + "'): ");
        String nombre = sc.nextLine().trim();
        System.out.print("Nuevo teléfono (Enter para dejar '" + p.getTelefono() + "'): ");
        String telefono = sc.nextLine().trim();

        if (!nombre.isEmpty()) p.setNombre(nombre);
        if (!telefono.isEmpty()) p.setTelefono(telefono);

        System.out.println(dao.actualizar(p) ? "Contacto actualizado." : "No se pudo actualizar.");
    }

    private static void eliminar() {
        Persona p = seleccionarContacto();
        if (p == null) return;

        System.out.print("¿Eliminar a " + p.getNombre() + "? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println(dao.eliminar(p.getId()) ? "Contacto eliminado." : "No se pudo eliminar.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    // Busca por id, nombre o teléfono y devuelve la lista de coincidencias
    private static List<Persona> buscarPorCriterio() {
        System.out.println("Buscar por: 1. ID  2. Nombre  3. Teléfono");
        int op = leerEntero("Opción: ");
        String criterio;
        switch (op) {
            case 1: criterio = "id"; break;
            case 2: criterio = "nombre"; break;
            case 3: criterio = "telefono"; break;
            default:
                System.out.println("Opción inválida.");
                return new java.util.ArrayList<>();
        }
        System.out.print("Valor a buscar: ");
        String valor = sc.nextLine().trim();
        return dao.buscar(criterio, valor);
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
        int id = leerEntero("Hay varias coincidencias, escribe el ID del contacto: ");
        for (Persona p : resultado) {
            if (p.getId() == id) return p;
        }
        System.out.println("Ese ID no está en los resultados.");
        return null;
    }

    private static void mostrar(List<Persona> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay contactos para mostrar.");
            return;
        }
        for (Persona p : lista) System.out.println(p);
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }
}
