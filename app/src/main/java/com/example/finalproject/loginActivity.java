package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

public class loginActivity extends AppCompatActivity {

    // Declare variables for TextInputLayout and TextInputEditText
    private TextInputLayout loginUsernameLayout, loginEmailLayout, loginPasswordLayout;
    private TextInputEditText loginUsernameEditText, loginEmailEditText, loginPasswordEditText;
    private Intent intent;
    private FirebaseAuth lAuth;
    private ProgressBar loginProgress;
    private MaterialButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initialize Firebase Auth
        lAuth = FirebaseAuth.getInstance();

        //initialize views
        loginButton = findViewById(R.id.buttonLogin);
        loginProgress = findViewById(R.id.progressBar);

        // Initialize the button for navigating to signUpActivity
        Button button = findViewById(R.id.buttonSignUp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(loginActivity.this, signUpActivity.class);
                startActivity(intent1);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmuser();
            }
        });

        // Initialize TextInputLayout and TextInputEditText for username, email, and password
        loginUsernameLayout = findViewById(R.id.loginUsername);
        loginUsernameEditText = findViewById(R.id.loginUsernameText);

        loginEmailLayout = findViewById(R.id.loginEmail);
        loginEmailEditText = findViewById(R.id.loginEmailText);

        loginPasswordLayout = findViewById(R.id.loginPassword);
        loginPasswordEditText = findViewById(R.id.loginPasswordText);


    }
    private void confirmuser() {
        if (!validateInputs()) {
            return;
        }

        // Show progress and disable button
        loginProgress.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        loginButton.setText("AUTHENTICATING...");

        String email = Objects.requireNonNull(loginEmailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(loginPasswordEditText.getText()).toString().trim();

        lAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Hide progress and reset button
                        loginProgress.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                        loginButton.setText(R.string.login);//Reset button text

                        if (task.isSuccessful()){
                            FirebaseUser user = lAuth.getCurrentUser();
                            Toast.makeText(loginActivity.this,"Login Successful!", Toast.LENGTH_SHORT).show();

                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(loginActivity.this,
                                                            "Verification email sent to " + user.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(loginActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            intent = new Intent(loginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Reegistration failed";
                            Toast.makeText(loginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
            // Proceed with login logic (e.g., call an API or save data)
        }
        return isUsernameValid;
    }

    // Method to validate the username input
    private boolean validateUsername() {
        String username = Objects.requireNonNull(loginUsernameEditText.getText()).toString().trim();

        if (username.isEmpty()) {
            loginUsernameLayout.setError("Username cannot be empty");
            return false;
        } else if (username.length() < 4) {
            loginUsernameLayout.setError("Username must be at least 4 characters");
            return false;
        } else {
            loginUsernameLayout.setError(null);
            return true;
        }
    }

    // Method to validate the email input
    private boolean validateEmail() {
        String email = Objects.requireNonNull(loginEmailEditText.getText()).toString().trim();

        if (email.isEmpty()) {
            loginEmailLayout.setError("Email cannot be empty");
            return false;
        } else if (!isValidEmail(email)) {
            loginEmailLayout.setError("Invalid email address");
            return false;
        } else {
            loginEmailLayout.setError(null);
            return true;
        }
    }

    // Method to validate the password input
    private boolean validatePassword() {
        String password = Objects.requireNonNull(loginPasswordEditText.getText()).toString().trim();

        if (password.isEmpty()) {
            loginPasswordLayout.setError("Password cannot be empty");
            return false;
        } else if (password.length() < 6) {
            loginPasswordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            loginPasswordLayout.setError(null);
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