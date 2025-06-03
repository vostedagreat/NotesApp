package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.finalproject.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class signUpActivity extends AppCompatActivity {

    private TextInputLayout signupUsernameLayout, signupEmailLayout, signupPasswordLayout;
    private TextInputEditText signupUsernameEditText, signupEmailEditText, signupPasswordEditText;
    private Intent intent;
    private FirebaseAuth mAuth;
    private ProgressBar signupProgress;
    private MaterialButton signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        signupButton = findViewById(R.id.signupButton);
        signupProgress = findViewById(R.id.progressBar);


        Button button = findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(signUpActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registernewuser();
            }
        });

        // Reference to Spinner
        Spinner countrySpinner = findViewById(R.id.countrySelect);

        // List of countries
        List<String> countries = Arrays.asList("Kenya", "USA", "Canada", "UK", "Germany", "France", "Japan");

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);

        // Attach adapter to Spinner
        countrySpinner.setAdapter(adapter);

        // Handle selection event
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item (which is likely a String if you're using a simple ArrayAdapter)
                String selectedCountry = parent.getItemAtPosition(position).toString();
                Toast.makeText(signUpActivity.this, "Selected: " + selectedCountry, Toast.LENGTH_SHORT).show();

                // If your adapter holds objects instead of simple strings, you would do something like:
                // YourObject selectedObject = (YourObject) parent.getItemAtPosition(position);
                // String selectedCountry = selectedObject.getCountryName(); // Assuming YourObject has a getCountryName() method
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize TextInputLayout and TextInputEditText
        signupUsernameLayout = findViewById(R.id.signupUsername);
        signupUsernameEditText = findViewById(R.id.signupUsernameText);

        signupEmailLayout = findViewById(R.id.signupEmail);
        signupEmailEditText = findViewById(R.id.signupEmailText);

        signupPasswordLayout = findViewById(R.id.signupPassword);
        signupPasswordEditText = findViewById(R.id.signupPasswordText);


    }

    private void registernewuser() {
        if (!validateInputs()) {
            return;
        }

        // Show progress and disable button
        signupProgress.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false);
        signupButton.setText("CREATING...");

        String email = Objects.requireNonNull(signupEmailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(signupPasswordEditText.getText()).toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progress and reset button
                        signupProgress.setVisibility(View.GONE);
                        signupButton.setEnabled(true);
                        signupButton.setText(R.string.sign_up); // Reset button text

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(signUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(signUpActivity.this,
                                                            "Verification email sent to " + user.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(signUpActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            intent = new Intent(signUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Registration failed";
                            Toast.makeText(signUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to validate all inputs
    private boolean validateInputs() {
        boolean isUsernameValid = validateUsername();
        boolean isEmailValid = validateEmail();
        boolean isPasswordValid = validatePassword();

        if (isUsernameValid && isEmailValid && isPasswordValid) {
            Toast.makeText(this, "All inputs are valid!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // Method to validate the username input
    private boolean validateUsername() {
        String username = Objects.requireNonNull(signupUsernameEditText.getText()).toString().trim();

        if (username.isEmpty()) {
            signupUsernameLayout.setError("Username cannot be empty");
            return false;
        } else if (username.length() < 4) {
            signupUsernameLayout.setError("Username must be at least 4 characters");
            return false;
        } else {
            signupUsernameLayout.setError(null);
            return true;
        }
    }

    // Method to validate the email input
    private boolean validateEmail() {
        String email = Objects.requireNonNull(signupEmailEditText.getText()).toString().trim();

        if (email.isEmpty()) {
            signupEmailLayout.setError("Email cannot be empty");
            return false;
        } else if (!isValidEmail(email)) {
            signupEmailLayout.setError("Invalid email address");
            return false;
        } else {
            signupEmailLayout.setError(null);
            return true;
        }
    }

    // Method to validate the password input
    private boolean validatePassword() {
        String password = Objects.requireNonNull(signupPasswordEditText.getText()).toString().trim();

        if (password.isEmpty()) {
            signupPasswordLayout.setError("Password cannot be empty");
            return false;
        } else if (password.length() < 6) {
            signupPasswordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            signupPasswordLayout.setError(null);
            return true;
        }
    }

    // Helper method to validate email format using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}