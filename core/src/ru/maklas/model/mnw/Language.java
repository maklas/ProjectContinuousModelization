package ru.maklas.model.mnw;


public enum Language {

    ENGLISH,
    RUSSIAN;

    @Override
    public String toString() {
        String capsed = name().replace('_', ' ');
        String lowerCase = capsed.toLowerCase();
        String firstCharacter = Character.toString(lowerCase.charAt(0));
        String others = lowerCase.substring(1, lowerCase.length());
        return firstCharacter.toUpperCase() + others;
    }
}
