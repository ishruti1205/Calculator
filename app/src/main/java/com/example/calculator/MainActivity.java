package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView output, user_input;
    MaterialButton btn_AC, btn_C, btn_percentage, btn_divide,
                           btn_7, btn_8, btn_9, btn_multiply,
                           btn_4, btn_5, btn_6, btn_minus,
                           btn_1, btn_2, btn_3, btn_plus,
                           btn_00, btn_0, btn_decimal, btn_equalsTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Fetch padding defined in XML
            int currentPaddingLeft = v.getPaddingLeft();
            int currentPaddingTop = v.getPaddingTop();
            int currentPaddingRight = v.getPaddingRight();
            int currentPaddingBottom = v.getPaddingBottom();

            // Adjust padding by adding system insets to the XML-defined padding
            v.setPadding(
                    currentPaddingLeft + systemBars.left, // Add inset to the existing left padding
                    50 + systemBars.top,   // Add inset to the existing top padding
                    currentPaddingRight + systemBars.right, // Add inset to the existing right padding
                    systemBars.bottom // Add inset to the existing bottom padding
            );

            return insets;
        });

        user_input = findViewById(R.id.user_input);

        output = findViewById(R.id.output);

        assignId(btn_AC, R.id.btn_AC);
        assignId(btn_C, R.id.btn_C);
        assignId(btn_percentage, R.id.btn_percentage);
        assignId(btn_divide, R.id.btn_divide);
        assignId(btn_multiply, R.id.btn_multiply);
        assignId(btn_minus, R.id.btn_minus);
        assignId(btn_plus, R.id.btn_plus);
        assignId(btn_equalsTo, R.id.btn_equalsTo);
        assignId(btn_00, R.id.btn_00);
        assignId(btn_0, R.id.btn_0);
        assignId(btn_decimal, R.id.btn_decimal);

        assignId(btn_7, R.id.btn_7);
        assignId(btn_8, R.id.btn_8);
        assignId(btn_9, R.id.btn_9);
        assignId(btn_4, R.id.btn_4);
        assignId(btn_5, R.id.btn_5);
        assignId(btn_6, R.id.btn_6);
        assignId(btn_1, R.id.btn_1);
        assignId(btn_2, R.id.btn_2);
        assignId(btn_3, R.id.btn_3);

    }

    public void assignId(MaterialButton btn , int id){
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();
        String dataToCalculate = user_input.getText().toString();

        // Reset on AC press
        if (buttonText.equals("AC")){
            output.setText("0");
            user_input.setText("0");
            return;
        }

        // Handle backspace (=) button
        if (buttonText.equals("=")){
            user_input.setText(output.getText());
            return;
        }

        // Handle backspace (C) button
        if (buttonText.equals("C")) {
            if (dataToCalculate.length() > 1) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            } else {
                dataToCalculate = "0";
            }
        } else {
            // Handle initial 0 in input
            if (dataToCalculate.equals("0") && !buttonText.equals(".")) {
                dataToCalculate = buttonText;
            } else {
                dataToCalculate += buttonText;
            }
        }

        // Set the updated input
        user_input.setText(dataToCalculate);

        // Calculate the result
        String finalResult = getResult(dataToCalculate);

        // Set the result
        output.setText(finalResult);
    }

    String getResult(String data){
        try{

            if (data.contains("%")) {
                data = handlePercentage(data);
            }

            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            if (finalResult.endsWith(".0")){
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        }
        catch(Exception e){
            return "Err";
        }
        finally {
            Context.exit();
        }
    }

    private String handlePercentage(String data) {

        // Simple handling: If the expression ends with %, just divide by 100
        if (data.endsWith("%")) {
            double value = Double.parseDouble(data.substring(0, data.length() - 1));
            return String.valueOf(value / 100);
        }

        // Handle cases where % is used in the middle of an expression (e.g., 100%5)
        if (data.contains("%")) {
            StringBuilder processedData = new StringBuilder();
            String[] parts = data.split("(?<=[0-9])%");

            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    processedData.append(parts[i]);
                } else {
                    // Replace % with /100 for percentage calculation
                    processedData.append("/100*").append(parts[i]);
                }
            }

            return processedData.toString();
        }

        return data;
    }

}