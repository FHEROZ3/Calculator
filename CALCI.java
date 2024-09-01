import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.Stack;

class Calculator extends JFrame implements ActionListener {
    static JFrame f;
    static JTextField display;
    String currentInput = "";
    Stack<String> operations = new Stack<>();
    boolean isResultDisplayed = false;

    Calculator() {
    }

    public static void main(String[] args) {
        f = new JFrame("CALCULATOR");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Calculator c = new Calculator();
        display = new JTextField(16);
        display.setEditable(false);

        // Creating buttons
        String[] buttonLabels = {
                "1", "2", "3", "+", 
                "4", "5", "6", "-", 
                "7", "8", "9", "*", 
                ".", "0", "C", "/", 
                "="
        };
        JPanel panel = new JPanel();
        panel.add(display);

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(c);
            panel.add(button);
        }

        f.add(panel);
        f.setSize(200, 220);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (isNumeric(command) || command.equals(".")) {
            if (isResultDisplayed) {
                currentInput = "";
                isResultDisplayed = false;
            }
            currentInput += command;
        } else if (command.equals("C")) {
            currentInput = "";
            operations.clear();
        } else if (command.equals("=")) {
            if (!currentInput.isEmpty()) {
                operations.push(currentInput);
            }
            String result = calculateResult();
            display.setText(result);
            currentInput = result;
            operations.clear();
            isResultDisplayed = true;
        } else { // Operator buttons
            if (!currentInput.isEmpty()) {
                operations.push(currentInput);
                currentInput = "";
            }
            operations.push(command);
        }
        display.setText(buildExpression());
    }

    private String calculateResult() {
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : operations) {
            if (isNumeric(token)) {
                values.push(Double.parseDouble(token));
            } else {
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    double b = values.pop();
                    double a = values.pop();
                    String op = operators.pop();
                    values.push(applyOperation(a, b, op));
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            String op = operators.pop();
            values.push(applyOperation(a, b, op));
        }

        return String.valueOf(values.pop());
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int precedence(String op) {
        if (op.equals("+") || op.equals("-")) {
            return 1;
        } else if (op.equals("*") || op.equals("/")) {
            return 2;
        }
        return -1;
    }

    private double applyOperation(double a, double b, String op) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    private String buildExpression() {
        StringBuilder expression = new StringBuilder();
        for (String token : operations) {
            expression.append(token);
        }
        expression.append(currentInput);
        return expression.toString();
    }
}
