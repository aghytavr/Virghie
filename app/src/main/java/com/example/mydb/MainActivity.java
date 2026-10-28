package myDB;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private EditText etName, etID;
    private TextView tvAgeLabel;
    private SeekBar seekBarAge;
    private Button btnAdd, btnEdit, btnDelete, btnView;

    private int selectedAge = 17; // Default min age

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        etName = findViewById(R.id.etStudentName);
        etID = findViewById(R.id.etStudentID);
        tvAgeLabel = findViewById(R.id.tvAgeLabel);
        seekBarAge = findViewById(R.id.seekBarAge);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);

        // Setup SeekBar untuk Rentang 17 s/d 50 (max slider = 33, offset = 17)
        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedAge = progress + 17;
                tvAgeLabel.setText("Student Age: " + selectedAge);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setupActions();
    }

    private void setupActions() {
        // ADD DATA
        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String id = etID.getText().toString().trim();

            if (name.isEmpty() || id.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = myDb.insertData(id, name, selectedAge);
            if (isInserted) {
                Toast.makeText(MainActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Failed to Insert Data (ID may already exist)", Toast.LENGTH_SHORT).show();
            }
        });

        // VIEW DATA
        btnView.setOnClickListener(v -> {
            Cursor res = myDb.getAllData();
            if (res.getCount() == 0) {
                showMessage("Database Status", "No Data Found");
                return;
            }

            StringBuilder buffer = new StringBuilder();
            while (res.moveToNext()) {
                buffer.append("Student ID: ").append(res.getString(0)).append("\n");
                buffer.append("Name: ").append(res.getString(1)).append("\n");
                buffer.append("Age: ").append(res.getInt(2)).append("\n\n");
            }

            showMessage("Student Records", buffer.toString());
        });

        // EDIT DATA
        btnEdit.setOnClickListener(v -> {
            String id = etID.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter Student ID to update", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = myDb.updateData(id, name, selectedAge);
            if (isUpdated) {
                Toast.makeText(MainActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Data Update Failed (ID not found)", Toast.LENGTH_SHORT).show();
            }
        });

        // DELETE DATA
        btnDelete.setOnClickListener(v -> {
            String id = etID.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter Student ID to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer deletedRows = myDb.deleteData(id);
            if (deletedRows > 0) {
                Toast.makeText(MainActivity.this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Data Delete Failed (ID not found)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void clearInputs() {
        etName.setText("");
        etID.setText("");
        seekBarAge.setProgress(0); // Kembali ke umur 17
    }
}