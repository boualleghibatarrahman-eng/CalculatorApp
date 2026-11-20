package com.example.calculatorapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private EditText num1Txt, num2Txt, num3Txt;
    private TextView resultTxt;
    private Button avgBtn, maxBtn, minBtn, clearBtn, sortAscBtn, sortDescBtn, sqrtBtn, squareBtn, powerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num1Txt = findViewById(R.id.num1Txt);
        num2Txt = findViewById(R.id.num2Txt);
        num3Txt = findViewById(R.id.num3Txt);
        resultTxt = findViewById(R.id.resultTxt);
        avgBtn = findViewById(R.id.avgBtn);
        maxBtn = findViewById(R.id.maxBtn);
        minBtn = findViewById(R.id.minBtn);
        clearBtn = findViewById(R.id.clearBtn);
        sortAscBtn = findViewById(R.id.sortAscBtn);
        sortDescBtn = findViewById(R.id.sortDescBtn);
        sqrtBtn = findViewById(R.id.sqrtBtn);
        squareBtn = findViewById(R.id.squareBtn);
        powerBtn = findViewById(R.id.powerBtn);

        avgBtn.setOnClickListener(v -> {
            hideKeyboard();
            double[] nums = getNumbers();
            if (nums == null) return;
            resultTxt.setText(String.format("Average: %.2f", (nums[0] + nums[1] + nums[2]) / 3.0));
        });

        maxBtn.setOnClickListener(v -> {
            hideKeyboard();
            double[] nums = getNumbers();
            if (nums == null) return;
            resultTxt.setText(String.format("Max: %.2f", Math.max(nums[0], Math.max(nums[1], nums[2]))));
        });

        minBtn.setOnClickListener(v -> {
            hideKeyboard();
            double[] nums = getNumbers();
            if (nums == null) return;
            resultTxt.setText(String.format("Min: %.2f", Math.min(nums[0], Math.min(nums[1], nums[2]))));
        });

        sortAscBtn.setOnClickListener(v -> {
            hideKeyboard();
            double[] nums = getNumbers();
            if (nums == null) return;
            Arrays.sort(nums);
            resultTxt.setText(String.format("Asc: %.2f, %.2f, %.2f", nums[0], nums[1], nums[2]));
        });

        sortDescBtn.setOnClickListener(v -> {
            hideKeyboard();
            double[] nums = getNumbers();
            if (nums == null) return;
            Arrays.sort(nums);
            resultTxt.setText(String.format("Desc: %.2f, %.2f, %.2f", nums[2], nums[1], nums[0]));
        });

        View.OnClickListener unaryOperation = v -> {
            hideKeyboard();
            double num = getActiveNumber();
            if (Double.isNaN(num)) return;

            if (v.getId() == R.id.sqrtBtn) {
                if (num < 0) {
                    Toast.makeText(this, "Cannot √ negative", Toast.LENGTH_SHORT).show();
                    return;
                }
                resultTxt.setText(String.format("√%.2f = %.2f", num, Math.sqrt(num)));
            } else if (v.getId() == R.id.squareBtn) {
                resultTxt.setText(String.format("%.2f² = %.2f", num, num * num));
            }
        };

        sqrtBtn.setOnClickListener(unaryOperation);
        squareBtn.setOnClickListener(unaryOperation);

        powerBtn.setOnClickListener(v -> {
            hideKeyboard();
            String s1 = num1Txt.getText().toString().trim();
            String s2 = num2Txt.getText().toString().trim();
            if (s1.isEmpty() || s2.isEmpty()) {
                Toast.makeText(this, "Please enter Base and Exponent (Number 1 & 2)", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double base = Double.parseDouble(s1);
                double exponent = Double.parseDouble(s2);
                resultTxt.setText(String.format("%.2f^%.2f = %.2f", base, exponent, Math.pow(base, exponent)));
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.error_invalid, Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn.setOnClickListener(v -> {
            hideKeyboard();
            num1Txt.setText("");
            num2Txt.setText("");
            num3Txt.setText("");
            resultTxt.setText(R.string.result_default);
        });

        resultTxt.setOnClickListener(v -> {
            String result = resultTxt.getText().toString();
            if (!result.equals(getString(R.string.result_default))) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Result", result);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, R.string.copied_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private double[] getNumbers() {
        String s1 = num1Txt.getText().toString().trim();
        String s2 = num2Txt.getText().toString().trim();
        String s3 = num3Txt.getText().toString().trim();

        if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty()) {
            Toast.makeText(this, R.string.error_empty, Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            return new double[]{Double.parseDouble(s1), Double.parseDouble(s2), Double.parseDouble(s3)};
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private double getActiveNumber() {
        EditText activeField = null;
        if (num1Txt.hasFocus()) activeField = num1Txt;
        else if (num2Txt.hasFocus()) activeField = num2Txt;
        else if (num3Txt.hasFocus()) activeField = num3Txt;

        if (activeField == null) activeField = num1Txt;

        String text = activeField.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter a number in the selected field", Toast.LENGTH_SHORT).show();
            return Double.NaN;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid, Toast.LENGTH_SHORT).show();
            return Double.NaN;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}