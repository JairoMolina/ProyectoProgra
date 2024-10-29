package gt.edu.umg.gpscamara;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gt.edu.umg.gpscamara.FotosGuardadas.DatabaseHelper;
import gt.edu.umg.gpscamara.FotosGuardadas.Foto;

public class VerFotosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FotosAdapter adapter;
    private DatabaseHelper dbHelper;  // Cambio de AppDataBase a DatabaseHelper
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_fotos);

        recyclerView = findViewById(R.id.recyclerViewFotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DatabaseHelper en lugar de AppDataBase
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        cargarFotos();
    }

    private void cargarFotos() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Foto> fotos = dbHelper.getAllFotos();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Pasar el contexto al adaptador
                        adapter = new FotosAdapter(VerFotosActivity.this, fotos);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}