package gt.edu.umg.gpscamara.FotosGuardadas;

public class Foto {
    private int id;
    private byte[] image;
    private double latitude;
    private double longitude;
    private long timestamp;
    private String nombre; // Nuevo atributo
    private boolean aceptado; // Nuevo atributo

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombre() {
        return nombre; // Nuevo getter
    }

    public void setNombre(String nombre) {
        this.nombre = nombre; // Nuevo setter
    }

    public boolean isAceptado() {
        return aceptado; // Nuevo getter
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado; // Nuevo setter
    }
}