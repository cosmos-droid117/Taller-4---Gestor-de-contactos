import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private static final String URL = "jdbc:sqlite:agenda.db";

    public PersonaDAO() {
        crearTabla();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS persona ("
                + "id INTEGER PRIMARY KEY, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT NOT NULL)";
        try (Connection c = conectar(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    public boolean crear(Persona p) {
        String sql = "INSERT INTO persona (id, nombre, telefono) VALUES (?, ?, ?)";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getTelefono());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo crear el contacto (¿ID repetido?): " + e.getMessage());
            return false;
        }
    }

    public List<Persona> buscar(String criterio, String valor) {
        List<Persona> lista = new ArrayList<>();
        String sql;
        switch (criterio) {
            case "id":       sql = "SELECT * FROM persona WHERE id = ?"; break;
            case "nombre":   sql = "SELECT * FROM persona WHERE nombre LIKE ?"; break;
            case "telefono": sql = "SELECT * FROM persona WHERE telefono = ?"; break;
            default: return lista;
        }
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (criterio.equals("id")) {
                ps.setInt(1, Integer.parseInt(valor));
            } else if (criterio.equals("nombre")) {
                ps.setString(1, "%" + valor + "%");
            } else {
                ps.setString(1, valor);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono")));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número.");
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }
        return lista;
    }

    public List<Persona> listarTodos() {
        List<Persona> lista = new ArrayList<>();
        try (Connection c = conectar(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM persona ORDER BY nombre")) {
            while (rs.next()) {
                lista.add(new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Persona p) {
        String sql = "UPDATE persona SET nombre = ?, telefono = ? WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setInt(3, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM persona WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
