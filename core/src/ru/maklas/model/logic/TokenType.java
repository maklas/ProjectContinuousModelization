package ru.maklas.model.logic;

public enum TokenType {

    header, //Хидеры (Program, Var)
    word, //Лубые слова (названия переменных, параметров, программы)
    number, //Число
    equals, // =
    end, //Конец строчки/комманды (;|\n)
    bracket, // [ or ] or ( or )
    sign,
    comma,
    ;

}
