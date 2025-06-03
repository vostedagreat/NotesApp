package com.example.finalproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Import ImageView
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.R;
import com.example.finalproject.entities.Note; // Import your Note entity
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class StoreCode extends AppCompatActivity {

    private EditText editTextCode; // You might want to rename this to reflect its purpose
    private Button buttonSaveCode; // You might want to rename this
    private DatabaseReference notesRef; // Reference to the notes node in Firebase

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText; // Add these
    private ImageView imageSave; // Add this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note); // Changed this line
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Realtime Database and get a reference to the "notes" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notesRef = database.getReference("notes"); // You can change "notes" to a different path if needed

        // Initialize UI elements based on activity_create_note.xml
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        imageSave = findViewById(R.id.imageSave);

        // You might want to adapt your logic to use these new UI elements
        // For example, to get the code, you might now use inputNoteText

        imageSave.setOnClickListener(new View.OnClickListener() { // Assuming imageSave acts as the save button
            @Override
            public void onClick(View v) {
                saveCodeAsNoteToFirebase();
            }
        });
    }

    private void saveCodeAsNoteToFirebase() {
        // Now you would get the code from inputNoteText
        String codeContent = inputNoteText.getText().toString().trim();

        if (!codeContent.isEmpty()) {
            // Create a new Note object
            Note note = new Note();
            note.setCodeContent(codeContent);

            // Set other relevant fields for the Note (you might want to get these from user input as well)
            note.setTitle(inputNoteTitle.getText().toString().trim()); // Get title from inputNoteTitle
            note.setSubtitle(inputNoteSubtitle.getText().toString().trim()); // Get subtitle from inputNoteSubtitle
            note.setNoteText(""); // You can leave this empty or add a description field later

            // Get current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            note.setDateTime(dateTime);

            // Generate a unique key for the new note
            String noteId = notesRef.push().getKey();

            // Save the Note object to Firebase under the unique key
            notesRef.child(noteId).setValue(note)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(StoreCode.this, "Code saved as a note!", Toast.LENGTH_SHORT).show();
                            inputNoteText.setText(""); // Clear the input field
                            inputNoteTitle.setText("");
                            inputNoteSubtitle.setText("");
                        } else {
                            Toast.makeText(StoreCode.this, "Failed to save code as a note.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter some code to save.", Toast.LENGTH_SHORT).show();
        }
    }
}