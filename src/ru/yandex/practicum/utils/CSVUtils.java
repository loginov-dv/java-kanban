package ru.yandex.practicum.utils;

import java.util.ArrayList;
import java.util.List;

// Вспомогательный класс для работы со строками
public final class CSVUtils {

    // Метод для обработки строковых полей для вывода в toString()
    public static String escapeSpecialCharacters(String data) {
        if (data.contains(",") || data.contains("\"")) {
            // Экранируем кавычки внутри строки
            String escapedData = data.replace("\"", "\"\"");
            // Оборачиваем всю строку в кавычки
            escapedData = "\"" + escapedData + "\"";

            return escapedData;
        }

        return data;
    }

    // Метод для парсинга строки
    // Учитывает запятые и кавычки в полях
    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        // Считываем строку побуквенно
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Проверяем, есть ли следующая кавычка (экранирование)
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Добавляем одну кавычку в StringBuilder
                    currentField.append('"');
                    // Пропускаем следующую кавычку
                    i++;
                } else {
                    // Переключаем состояние
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Дошли до разделителя, добавляем поле в список
                fields.add(currentField.toString());
                // Начинаем обработку нового поля
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        // Добавляем поле
        fields.add(currentField.toString());

        return fields;
    }
}
