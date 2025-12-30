package com.moscepa.entity;

public enum TypeQuestionnaire {

    EXERCICE(true, Integer.MAX_VALUE),
    QUIZ(true, Integer.MAX_VALUE),
    TEST(false, 1);

    private final boolean tentativesIllimitees;
    private final int tentativesMax;

    TypeQuestionnaire(boolean tentativesIllimitees, int tentativesMax) {
        this.tentativesIllimitees = tentativesIllimitees;
        this.tentativesMax = tentativesMax;
    }

    public boolean tentativesIllimitees() {
        return tentativesIllimitees;
    }

    public int tentativesMax() {
        return tentativesMax;
    }
}
