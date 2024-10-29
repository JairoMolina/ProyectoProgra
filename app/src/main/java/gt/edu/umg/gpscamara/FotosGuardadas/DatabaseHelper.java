package gt.edu.umg.gpscamara.FotosGuardadas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FotosDB";
    private static final int DATABASE_VERSION = 2; // Incrementa la versión de la base de datos

    // Tabla Fotos
    public static final String TABLE_FOTOS = "fotos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_ACEPTADO = "aceptado";// Nueva columna para el nombre

    // Crear tabla
    private static final String CREATE_TABLE_FOTOS =
            "CREATE TABLE " + TABLE_FOTOS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_IMAGE + " BLOB NOT NULL, "
                    + COLUMN_NOMBRE + " TEXT NOT NULL, " // Columna para nombre
                    + COLUMN_LATITUDE + " DOUBLE NOT NULL, "
                    + COLUMN_LONGITUDE + " DOUBLE NOT NULL, "
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                    + COLUMN_ACEPTADO + " INTEGER NOT NULL);";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FOTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_FOTOS + " ADD COLUMN " + COLUMN_ACEPTADO + " INTEGER NOT NULL DEFAULT 0;"); // Cambiado a COLUMN_ACEPTADO
        }
    }

    public long insertFoto(byte[] imageBytes, String nombre, double latitude, double longitude, boolean aceptado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, imageBytes);
        values.put(COLUMN_NOMBRE, nombre); // Asegúrate de tener esta columna en tu tabla
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_ACEPTADO, aceptado ? 1 : 0); // Guardamos 1 para true y 0 para false

        long id = db.insert(TABLE_FOTOS, null, values);
        db.close();
        return id;
    }

    public void deleteFoto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOTOS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Obtener todas las fotos
    public List<Foto> getAllFotos() {
        List<Foto> fotos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FOTOS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            // Obtener los índices de las columnas
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
            int imageIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
            int latitudeIndex = cursor.getColumnIndexOrThrow(COLUMN_LATITUDE);
            int longitudeIndex = cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE);
            int timestampIndex = cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP);
            int nombreIndex = cursor.getColumnIndexOrThrow(COLUMN_NOMBRE); // Nuevo índice
            int aceptadoIndex = cursor.getColumnIndexOrThrow(COLUMN_ACEPTADO); // Nuevo índice

            do {
                Foto foto = new Foto();
                foto.setId(cursor.getInt(idIndex));
                foto.setImage(cursor.getBlob(imageIndex));
                foto.setLatitude(cursor.getDouble(latitudeIndex));
                foto.setLongitude(cursor.getDouble(longitudeIndex));
                foto.setTimestamp(cursor.getLong(timestampIndex));
                foto.setNombre(cursor.getString(nombreIndex)); // Asignar nombre
                foto.setAceptado(cursor.getInt(aceptadoIndex) == 1); // Asignar aceptado

                fotos.add(foto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return fotos;


    }
}