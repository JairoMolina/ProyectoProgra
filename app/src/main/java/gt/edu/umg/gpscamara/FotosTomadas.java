package gt.edu.umg.gpscamara;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gt.edu.umg.gpscamara.FotosGuardadas.BitmapUtils;
import gt.edu.umg.gpscamara.FotosGuardadas.DatabaseHelper;

public class FotosTomadas extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private ImageView imageViewFotoTomada;
    private Button bttnGuardar;
    private Button bttnEliminar;
    private Button bttnTomarFoto;
    private EditText editTextNombre;
    private CheckBox checkBoxAcepto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private double currentLatitude;
    private double currentLongitude;
    private Bitmap currentImageBitmap;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotostomadas);

        // Inicializar vistas
        imageViewFotoTomada = findViewById(R.id.imageViewFotoTomada);
        bttnGuardar = findViewById(R.id.bttnGuardar);
        bttnEliminar = findViewById(R.id.bttnEliminar);
        bttnTomarFoto = findViewById(R.id.bttnTomarFoto);
        editTextNombre = findViewById(R.id.editTextNombre);
        checkBoxAcepto = findViewById(R.id.checkBoxAcepto);

        // Inicializar base de datos
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Obtener datos del intent
        currentImageBitmap = getIntent().getParcelableExtra("imageBitmap");
        currentLatitude = getIntent().getDoubleExtra("currentLatitude", 0.0);
        currentLongitude = getIntent().getDoubleExtra("currentLongitude", 0.0);

        // Mostrar la imagen
        if (currentImageBitmap != null) {
            imageViewFotoTomada.setImageBitmap(currentImageBitmap);
        }

        // Configurar el CheckBox para habilitar o deshabilitar el botón Guardar
        checkBoxAcepto.setOnCheckedChangeListener((buttonView, isChecked) -> validateInputs());

        // Agregar un TextWatcher al campo de nombre para monitorear cambios en el texto
        editTextNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario hacer nada aquí en este caso
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario hacer nada aquí en este caso
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Validar los inputs después de cada cambio en el texto del nombre
                validateInputs();
            }
        });

        bttnGuardar.setOnClickListener(v -> {
            if (currentImageBitmap != null) {
                String nombre = editTextNombre.getText().toString();
                boolean aceptado = checkBoxAcepto.isChecked(); // Obtener el estado del checkbox
                savePhotoToDatabase(currentImageBitmap, nombre, currentLatitude, currentLongitude, aceptado);
            }
        });
        // Configurar el botón Eliminar
        bttnEliminar.setOnClickListener(v -> {
            imageViewFotoTomada.setImageDrawable(null);
            currentImageBitmap = null;
            bttnGuardar.setEnabled(false);
            Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
        });

        // Configurar el botón Tomar Foto
        bttnTomarFoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        });
    }

    private void validateInputs() {
        String nombre = editTextNombre.getText().toString();
        bttnGuardar.setEnabled(!nombre.isEmpty() && currentImageBitmap != null); // Solo valida nombre e imagen
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            currentImageBitmap = (Bitmap) extras.get("data");
            if (currentImageBitmap != null) {
                imageViewFotoTomada.setImageBitmap(currentImageBitmap);
                validateInputs();
            }
        }
    }

    private void savePhotoToDatabase(Bitmap imageBitmap, String nombre, double latitude, double longitude, boolean aceptado) {
        executorService.execute(() -> {
            try {
                byte[] imageBytes = BitmapUtils.bitmapToByteArray(imageBitmap);
                long id = dbHelper.insertFoto(imageBytes, nombre, latitude, longitude, aceptado);

                runOnUiThread(() -> {
                    if (id != -1) {
                        Toast.makeText(this, "Foto guardada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al guardar la foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}