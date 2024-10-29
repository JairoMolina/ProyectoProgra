package gt.edu.umg.gpscamara;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.graphics.Bitmap;
import android.widget.Toast;

import gt.edu.umg.gpscamara.FotosGuardadas.BitmapUtils;
import gt.edu.umg.gpscamara.FotosGuardadas.DatabaseHelper;
import gt.edu.umg.gpscamara.FotosGuardadas.Foto;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.FotoViewHolder> {
    private List<Foto> listaFotos;
    private DatabaseHelper dbHelper;
    private Context context;


    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvCoordenadas;
        TextView tvNombre; // Agregar TextView para el nombre
        TextView tvAceptado; // Agregar TextView para el estado de aceptación
        Button btnEliminar;

        FotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFoto);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
            tvNombre = itemView.findViewById(R.id.tvNombre); // Inicializar
            tvAceptado = itemView.findViewById(R.id.tvAceptado); // Inicializar
            btnEliminar = itemView.findViewById(R.id.btnEliminarFoto);
        }
    }

    // Método para actualizar la lista de fotos
    public void actualizarFotos(List<Foto> nuevasFotos) {
        this.listaFotos = nuevasFotos;
        notifyDataSetChanged();
    }


    public FotosAdapter(Context context, List<Foto> fotos) {
        this.context = context;
        this.listaFotos = fotos;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public FotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FotoViewHolder holder, int position) {
        Foto foto = listaFotos.get(position);

        // Convertir bytes a Bitmap
        Bitmap bitmap = BitmapUtils.byteArrayToBitmap(foto.getImage());
        holder.imageView.setImageBitmap(bitmap);

        // Mostrar coordenadas
        holder.tvCoordenadas.setText(String.format("Latitud: %.4f\nLongitud: %.4f",
                foto.getLatitude(), foto.getLongitude()));

        // Mostrar el nombre
        holder.tvNombre.setText(foto.getNombre()); // Asumiendo que tienes un método getNombre()

        // Mostrar el estado de aceptación
        holder.tvAceptado.setText(foto.isAceptado() ? "Aceptado" : "Rechazado"); // Asumiendo que tienes un método isAceptado()

        // Configurar botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Eliminar foto")
                    .setMessage("¿Estás seguro de que deseas eliminar esta foto?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        dbHelper.deleteFoto(foto.getId());
                        listaFotos.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaFotos.size());
                        Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }
}