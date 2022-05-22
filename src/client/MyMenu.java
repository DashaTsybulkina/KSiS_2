package client;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMenu {
    private static Scanner scConsole = new Scanner(System.in);

    public static short choose(String sQuestion, String[] Options) { // не больше 9-ти вариантов ответа
        short nChoice;
        String answer;
        StringBuilder optionsDigits = new StringBuilder();

        System.out.println(sQuestion + "\n\tВарианты: ");
        for (int i = 0; i < Options.length; i++) {
            System.out.println("\t" + (i + 1) + " - " + Options[i]);
            optionsDigits.append(i + 1);
        }

        answer = getAnythingFromConsole("", "^[" + optionsDigits + "]$", "Нужно ввести " +
                "цифру (одну из предложенных).");
        nChoice = (short) (Short.parseShort(answer) - 1);

        return nChoice;
    }

    public static String getAnythingFromConsole(String Question, String regEx, String clarification) {
        String sInput;
        String sOutput;
        String stringIfNothingIsFoundInString = "";
        boolean isIncorrect;
        boolean nothingIsFoundIsNotAllowed = findRegEx(regEx, "\\^\\$", "")[0].equals("");
        if (!nothingIsFoundIsNotAllowed) {
            stringIfNothingIsFoundInString = regEx + "++++++";
        }

        if (!Question.equals("")) {
            System.out.println(Question);
        }

        do {
            sInput = scConsole.nextLine().trim();
            sOutput = findRegEx(sInput, regEx, stringIfNothingIsFoundInString)[0];
            if (sOutput.equals(stringIfNothingIsFoundInString)) {
                System.err.println("Данные введены неверно. " + clarification + "\nПовторите попытку:");
                isIncorrect = true;
            } else {
                isIncorrect = false;
            }
        } while (isIncorrect);

        System.out.println();
        return sOutput;
    }

    private static String[] findRegEx(String sInput, String regEx, String outputIfNothingFound) {
        ArrayList<String> arrStringOutput = new ArrayList<>();
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(sInput);

        if (matcher.find()) {
            do {
                arrStringOutput.add(matcher.group());
            } while (matcher.find());
        } else
            arrStringOutput.add(outputIfNothingFound);

        return arrStringOutput.toArray(new String[0]);
    }
}
